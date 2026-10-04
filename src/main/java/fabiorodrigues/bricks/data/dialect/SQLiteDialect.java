package fabiorodrigues.bricks.data.dialect;

import java.util.List;

/**
 * Dialeto SQLite.
 *
 * <p>Upsert usa {@code ON CONFLICT ... DO UPDATE} (SQLite 3.24+; sem alvo de conflito requer 3.35+)
 * em vez de {@code INSERT OR REPLACE} — o REPLACE apaga e reinsere a linha, mudando o rowid
 * e disparando cascatas de DELETE.</p>
 */
public final class SQLiteDialect implements SqlDialect {

    /** Instancia partilhada (sem estado). */
    public static final SQLiteDialect INSTANCE = new SQLiteDialect();

    private SQLiteDialect() {}

    @Override
    public String quoteIdentifier(String name) {
        return Dialects.quote(name, "\"", "\"");
    }

    @Override
    public String columnType(ColumnType type) {
        switch (type) {
            case ID: return "INTEGER PRIMARY KEY AUTOINCREMENT";
            case STRING:
            case TEXT:
            case DATE:
            case DATETIME: return "TEXT";
            case INTEGER:
            case LONG:
            case BOOLEAN: return "INTEGER";
            case DOUBLE: return "REAL";
            case DECIMAL: return "NUMERIC";
            default: throw new IllegalArgumentException("Tipo nao suportado: " + type);
        }
    }

    @Override
    public String paginate(String sql, Integer limit, Integer offset, boolean hasOrderBy) {
        return Dialects.limitOffset(sql, limit, offset, "-1");
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
        return sb.append(" DO UPDATE SET ")
            .append(Dialects.join(updateColumns, c -> quoteIdentifier(c) + " = excluded." + quoteIdentifier(c)))
            .toString();
    }
}
