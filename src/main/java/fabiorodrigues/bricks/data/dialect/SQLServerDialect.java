package fabiorodrigues.bricks.data.dialect;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialeto SQL Server / Azure SQL Database.
 *
 * <ul>
 *   <li>Paginacao com {@code OFFSET n ROWS FETCH NEXT m ROWS ONLY}, que exige ORDER BY —
 *       sem ordenacao e acrescentado {@code ORDER BY (SELECT NULL)} (ordem indefinida).
 *       Com {@code SELECT DISTINCT} isso falha, por isso e exigido ORDER BY.</li>
 *   <li>Upsert com {@code MERGE ... WITH (HOLDLOCK)}, que exige colunas de conflito.</li>
 *   <li>Texto em {@code NVARCHAR} — {@code TEXT}/{@code NTEXT} sao obsoletos e nao se comparam com {@code =}.</li>
 * </ul>
 */
public final class SQLServerDialect implements SqlDialect {

    /** Instancia partilhada (sem estado). */
    public static final SQLServerDialect INSTANCE = new SQLServerDialect();

    private SQLServerDialect() {}

    @Override
    public String quoteIdentifier(String name) {
        return Dialects.quote(name, "[", "]");
    }

    @Override
    public String columnType(ColumnType type) {
        switch (type) {
            case ID: return "INT IDENTITY(1,1) PRIMARY KEY";
            case STRING: return "NVARCHAR(255)";
            case TEXT: return "NVARCHAR(MAX)";
            case INTEGER: return "INT";
            case LONG: return "BIGINT";
            case DOUBLE: return "FLOAT";
            case DECIMAL: return "DECIMAL(19,4)";
            case BOOLEAN: return "BIT";
            case DATE: return "DATE";
            case DATETIME: return "DATETIME2";
            default: throw new IllegalArgumentException("Tipo nao suportado: " + type);
        }
    }

    @Override
    public String paginate(String sql, Integer limit, Integer offset, boolean hasOrderBy) {
        Dialects.validatePagination(limit, offset);
        if (limit == null && offset == null) return sql;
        if (!hasOrderBy && sql.regionMatches(true, 0, "SELECT DISTINCT", 0, "SELECT DISTINCT".length())) {
            // o SQL Server rejeita ORDER BY (SELECT NULL) com DISTINCT (erro 145)
            throw new IllegalStateException("SQL Server: SELECT DISTINCT com limit/offset requer .orderBy(...).");
        }
        StringBuilder sb = new StringBuilder(sql);
        if (!hasOrderBy) sb.append(" ORDER BY (SELECT NULL)");
        sb.append(" OFFSET ").append(offset != null ? offset : 0).append(" ROWS");
        if (limit != null) sb.append(" FETCH NEXT ").append(limit).append(" ROWS ONLY");
        return sb.toString();
    }

    @Override
    public String createTableIfNotExists(String table, String definitions) {
        String quoted = quoteIdentifier(table);
        return "IF OBJECT_ID(N'" + quoted.replace("'", "''") + "', N'U') IS NULL"
            + " CREATE TABLE " + quoted + " (" + definitions + ")";
    }

    @Override
    public String upsert(String table, List<String> columns, List<String> conflictColumns, List<String> updateColumns) {
        Dialects.requireColumns(columns);
        if (conflictColumns.isEmpty()) {
            throw new IllegalArgumentException(
                "SQL Server requer .conflictOn(...) para upsert (MERGE) — usar uma coluna PK ou UNIQUE.");
        }
        List<String> on = new ArrayList<>();
        for (String c : conflictColumns) {
            on.add("target." + quoteIdentifier(c) + " = source." + quoteIdentifier(c));
        }

        StringBuilder sb = new StringBuilder("MERGE INTO ").append(quoteIdentifier(table)).append(" WITH (HOLDLOCK) AS target")
            .append(" USING (VALUES (").append(Dialects.placeholders(columns.size())).append("))")
            .append(" AS source (").append(Dialects.join(columns, this::quoteIdentifier)).append(")")
            .append(" ON ").append(String.join(" AND ", on));
        if (!updateColumns.isEmpty()) {
            sb.append(" WHEN MATCHED THEN UPDATE SET ")
                .append(Dialects.join(updateColumns, c -> "target." + quoteIdentifier(c) + " = source." + quoteIdentifier(c)));
        }
        return sb.append(" WHEN NOT MATCHED THEN INSERT (").append(Dialects.join(columns, this::quoteIdentifier)).append(")")
            .append(" VALUES (").append(Dialects.join(columns, c -> "source." + quoteIdentifier(c))).append(");")
            .toString();
    }
}
