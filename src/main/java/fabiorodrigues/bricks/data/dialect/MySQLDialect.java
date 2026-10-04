package fabiorodrigues.bricks.data.dialect;

import java.util.List;

/**
 * Dialeto MySQL / MariaDB.
 *
 * <p>Upsert usa {@code ON DUPLICATE KEY UPDATE}; as colunas de conflito sao ignoradas
 * (o MySQL usa qualquer PK/UNIQUE violada).</p>
 */
public final class MySQLDialect implements SqlDialect {

    /** Instancia partilhada (sem estado). */
    public static final MySQLDialect INSTANCE = new MySQLDialect();

    private MySQLDialect() {}

    @Override
    public String quoteIdentifier(String name) {
        return Dialects.quote(name, "`", "`");
    }

    @Override
    public String columnType(ColumnType type) {
        switch (type) {
            case ID: return "INT AUTO_INCREMENT PRIMARY KEY";
            case STRING: return "VARCHAR(255)";
            case TEXT: return "TEXT";
            case INTEGER: return "INT";
            case LONG: return "BIGINT";
            case DOUBLE: return "DOUBLE";
            case DECIMAL: return "DECIMAL(19,4)";
            case BOOLEAN: return "BOOLEAN";
            case DATE: return "DATE";
            case DATETIME: return "DATETIME";
            default: throw new IllegalArgumentException("Tipo nao suportado: " + type);
        }
    }

    @Override
    public String paginate(String sql, Integer limit, Integer offset, boolean hasOrderBy) {
        return Dialects.limitOffset(sql, limit, offset, "18446744073709551615");
    }

    @Override
    public String createTableIfNotExists(String table, String definitions) {
        return "CREATE TABLE IF NOT EXISTS " + quoteIdentifier(table) + " (" + definitions + ")";
    }

    @Override
    public String upsert(String table, List<String> columns, List<String> conflictColumns, List<String> updateColumns) {
        Dialects.requireColumns(columns);
        String insert = "INSERT INTO " + quoteIdentifier(table)
            + " (" + Dialects.join(columns, this::quoteIdentifier) + ")"
            + " VALUES (" + Dialects.placeholders(columns.size()) + ")"
            + " ON DUPLICATE KEY UPDATE ";
        if (updateColumns.isEmpty()) {
            // atribuicao neutra: ignora o conflito sem o INSERT IGNORE engolir outros erros
            String first = quoteIdentifier(columns.get(0));
            return insert + first + " = " + first;
        }
        return insert + Dialects.join(updateColumns, c -> quoteIdentifier(c) + " = VALUES(" + quoteIdentifier(c) + ")");
    }
}
