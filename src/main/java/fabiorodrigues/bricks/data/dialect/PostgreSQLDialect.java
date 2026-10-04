package fabiorodrigues.bricks.data.dialect;

import java.util.List;

/**
 * Dialeto PostgreSQL.
 *
 * <p>Nota: identificadores entre aspas sao sensiveis a maiusculas. Usar nomes em minusculas
 * no builder para que continuem a bater com SQL raw sem aspas ({@code where}, {@code from}).</p>
 */
public final class PostgreSQLDialect implements SqlDialect {

    /** Instancia partilhada (sem estado). */
    public static final PostgreSQLDialect INSTANCE = new PostgreSQLDialect();

    private PostgreSQLDialect() {}

    @Override
    public String quoteIdentifier(String name) {
        return Dialects.quote(name, "\"", "\"");
    }

    @Override
    public String columnType(ColumnType type) {
        switch (type) {
            case ID: return "SERIAL PRIMARY KEY";
            case STRING: return "VARCHAR(255)";
            case TEXT: return "TEXT";
            case INTEGER: return "INTEGER";
            case LONG: return "BIGINT";
            case DOUBLE: return "DOUBLE PRECISION";
            case DECIMAL: return "NUMERIC(19,4)";
            case BOOLEAN: return "BOOLEAN";
            case DATE: return "DATE";
            case DATETIME: return "TIMESTAMP";
            default: throw new IllegalArgumentException("Tipo nao suportado: " + type);
        }
    }

    @Override
    public String paginate(String sql, Integer limit, Integer offset, boolean hasOrderBy) {
        return Dialects.limitOffset(sql, limit, offset, "ALL");
    }

    @Override
    public String createTableIfNotExists(String table, String definitions) {
        return "CREATE TABLE IF NOT EXISTS " + quoteIdentifier(table) + " (" + definitions + ")";
    }

    @Override
    public String upsert(String table, List<String> columns, List<String> conflictColumns, List<String> updateColumns) {
        Dialects.requireColumns(columns);
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(quoteIdentifier(table))
            .append(" (").append(Dialects.join(columns, this::quoteIdentifier)).append(")")
            .append(" VALUES (").append(Dialects.placeholders(columns.size())).append(")")
            .append(" ON CONFLICT");
        if (!conflictColumns.isEmpty()) {
            sb.append(" (").append(Dialects.join(conflictColumns, this::quoteIdentifier)).append(")");
        }
        if (updateColumns.isEmpty()) {
            return sb.append(" DO NOTHING").toString();
        }
        if (conflictColumns.isEmpty()) {
            throw new IllegalArgumentException("PostgreSQL requer .conflictOn(...) para upsert com DO UPDATE.");
        }
        return sb.append(" DO UPDATE SET ")
            .append(Dialects.join(updateColumns, c -> quoteIdentifier(c) + " = EXCLUDED." + quoteIdentifier(c)))
            .toString();
    }
}
