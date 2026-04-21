package fabiorodrigues.bricks.data;

import fabiorodrigues.bricks.data.config.DbConfig;
import fabiorodrigues.bricks.data.mapper.ResultMapper;

import java.sql.*;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Builder de queries SQL. Suporta SELECT, INSERT, UPDATE, DELETE e CREATE TABLE.
 * Criado via {@link DB#query()}.
 *
 * <p>SELECT com filtros condicionais:</p>
 * <pre>{@code
 * List<Aluno> alunos = DB.query()
 *     .select("id", "nome", "turma")
 *     .from("alunos")
 *     .where("ativo", "=", 1)
 *     .when(filtroTurma != null, q -> q.where("turma", "=", filtroTurma))
 *     .orderBy("nome", "ASC")
 *     .limit(20)
 *     .execute(Aluno.class);
 * }</pre>
 *
 * <p>INSERT com upsert:</p>
 * <pre>{@code
 * int id = DB.query()
 *     .insertInto("alunos")
 *     .values(Map.of("nome", "Fabio", "turma", 1))
 *     .onDuplicateUpdate("nome", "turma")
 *     .execute();
 * }</pre>
 *
 * <p>Nota: o metodo de filtro condicional e {@code .when()} — {@code .if()} e uma palavra
 * reservada em Java e nao pode ser usado como nome de metodo.</p>
 */
public class Query {

    private enum Type { SELECT, INSERT, UPDATE, DELETE, CREATE_TABLE }

    private final DbConfig config;
    private Type type;

    // SELECT
    private final List<String> selectCols = new ArrayList<>();
    private String fromClause;
    private final List<String[]> joins = new ArrayList<>();
    private final List<Object[]> wheres = new ArrayList<>();
    private String orderByField;
    private String orderByDir = "ASC";
    private int limitVal = -1;
    private int offsetVal = 0;
    private Class<?> groupParentClass;
    private String groupParentKey;
    private String groupChildListField;
    private Class<?> groupChildClass;
    private String groupChildKey;

    // INSERT
    private String insertTable;
    private Map<String, Object> insertVals;
    private String[] onDupFields;
    private String conflictOnField;

    // UPDATE
    private String updateTable;
    private Map<String, Object> setVals;

    // DELETE
    private String deleteTable;

    // CREATE TABLE
    private String createTableName;
    private final List<String[]> tableCols = new ArrayList<>();

    Query(DbConfig config) {
        this.config = config;
    }

    // --- SELECT ---

    /**
     * Define as colunas a selecionar.
     *
     * @param columns {@code String...} — colunas (ex: {@code "id"}, {@code "q.id_user"}, {@code "COUNT(*) as total"})
     * @return esta query para encadeamento
     */
    public Query select(String... columns) {
        this.type = Type.SELECT;
        Collections.addAll(selectCols, columns);
        return this;
    }

    /**
     * Define a tabela principal do SELECT.
     *
     * @param table {@code String} — nome da tabela, com alias opcional (ex: {@code "alunos a"})
     * @return esta query para encadeamento
     */
    public Query from(String table) {
        this.fromClause = table;
        return this;
    }

    /**
     * Adiciona um INNER JOIN.
     *
     * @param table     {@code String} — tabela a juntar, com alias opcional
     * @param condition {@code String} — condicao ON (ex: {@code "a.id = b.aluno_id"})
     * @return esta query para encadeamento
     */
    public Query join(String table, String condition) {
        joins.add(new String[]{"INNER", table, condition});
        return this;
    }

    /**
     * Adiciona um LEFT JOIN.
     *
     * @param table     {@code String} — tabela a juntar, com alias opcional
     * @param condition {@code String} — condicao ON
     * @return esta query para encadeamento
     */
    public Query leftJoin(String table, String condition) {
        joins.add(new String[]{"LEFT", table, condition});
        return this;
    }

    /**
     * Adiciona uma condicao WHERE com operador em {@code String}.
     *
     * <pre>{@code
     * .where("idade", ">=", 18)
     * .where("nome", "LIKE", "%Silva%")
     * .where("id", "IN", List.of(1, 2, 3))
     * .where("removido_em", "IS NULL", null)
     * }</pre>
     *
     * @param field    {@code String} — campo ou expressao (ex: {@code "a.turma"})
     * @param operator {@code String} — operador SQL (ex: {@code "="}, {@code "LIKE"}, {@code "IS NULL"})
     * @param value    o valor a comparar (ignorado para IS NULL / IS NOT NULL)
     * @return esta query para encadeamento
     */
    public Query where(String field, String operator, Object value) {
        wheres.add(new Object[]{field, operator.toUpperCase(), value});
        return this;
    }

    /**
     * Adiciona uma condicao WHERE com {@link WhereOperator} type-safe.
     *
     * <pre>{@code
     * .where("idade", WhereOperator.GTE, 18)
     * .where("removido_em", WhereOperator.IS_NULL, null)
     * }</pre>
     *
     * @param field    {@code String} — campo ou expressao
     * @param operator {@code WhereOperator} — operador de comparacao
     * @param value    o valor a comparar (ignorado para IS_NULL / IS_NOT_NULL)
     * @return esta query para encadeamento
     */
    public Query where(String field, WhereOperator operator, Object value) {
        return where(field, operator.getSql(), value);
    }

    /**
     * Bloco condicional — aplica operacoes ao query apenas se a condicao for verdadeira.
     * Equivalente ao {@code .if()} do Compose — {@code if} e palavra reservada em Java.
     *
     * <pre>{@code
     * .when(filtro != null, q -> q
     *     .join("categorias c", "a.id_categoria = c.id")
     *     .where("c.nome", "=", filtro)
     * )
     * }</pre>
     *
     * @param condition {@code boolean} — condicao de aplicacao
     * @param block     {@code UnaryOperator<Query>} — operacoes a aplicar se a condicao for verdadeira
     * @return esta query para encadeamento
     */
    public Query when(boolean condition, UnaryOperator<Query> block) {
        if (condition) {
            block.apply(this);
        }
        return this;
    }

    /**
     * Define a ordenacao do resultado.
     *
     * @param field     {@code String} — campo de ordenacao (ex: {@code "q.id"}, {@code "nome"})
     * @param direction {@code String} — {@code "ASC"} ou {@code "DESC"}
     * @return esta query para encadeamento
     */
    public Query orderBy(String field, String direction) {
        this.orderByField = field;
        this.orderByDir = direction;
        return this;
    }

    /**
     * Limita o numero de linhas devolvidas.
     *
     * @param n {@code int} — numero maximo de linhas
     * @return esta query para encadeamento
     */
    public Query limit(int n) {
        this.limitVal = n;
        return this;
    }

    /**
     * Define o offset (numero de linhas a saltar).
     *
     * @param n {@code int} — numero de linhas a saltar
     * @return esta query para encadeamento
     */
    public Query offset(int n) {
        this.offsetVal = n;
        return this;
    }

    /**
     * Define o agrupamento pai para mapeamento 1:N.
     * Agrupa linhas duplicadas do pai numa unica instancia com lista de filhos.
     *
     * @param cls      a classe record do objeto pai
     * @param keyField {@code String} — campo/coluna que identifica unicamente o pai (ex: {@code "id"})
     * @return esta query para encadeamento
     * @see #groupChild(String, Class, String)
     */
    public Query groupParent(Class<?> cls, String keyField) {
        this.groupParentClass = cls;
        this.groupParentKey = keyField;
        return this;
    }

    /**
     * Define o agrupamento filho para mapeamento 1:N.
     * Deve ser chamado apos {@link #groupParent(Class, String)}.
     *
     * <pre>{@code
     * .groupParent(Categoria.class, "id")
     * .groupChild("ebooks", Ebook.class, "id")
     * }</pre>
     *
     * @param listField {@code String} — nome do campo {@code List<T>} no record pai
     * @param cls       a classe record do objeto filho
     * @param keyField  {@code String} — campo/coluna que identifica unicamente o filho (para dedup)
     * @return esta query para encadeamento
     */
    public Query groupChild(String listField, Class<?> cls, String keyField) {
        this.groupChildListField = listField;
        this.groupChildClass = cls;
        this.groupChildKey = keyField;
        return this;
    }

    // --- INSERT ---

    /**
     * Inicia um INSERT na tabela indicada.
     *
     * @param table {@code String} — nome da tabela
     * @return esta query para encadeamento
     */
    public Query insertInto(String table) {
        this.type = Type.INSERT;
        this.insertTable = table;
        return this;
    }

    /**
     * Define os valores a inserir.
     *
     * @param values {@code Map<String, Object>} — mapa coluna → valor
     * @return esta query para encadeamento
     */
    public Query values(Map<String, Object> values) {
        this.insertVals = values;
        return this;
    }

    /**
     * Adiciona um par campo-valor ao INSERT, de forma individual e legivel.
     * Alternativa ao {@link #values(Map)} para melhor clareza visual.
     *
     * <pre>{@code
     * DB.query()
     *     .insertInto("professores")
     *     .value("nome", "Joao Silva")
     *     .value("grau", "Doutor")
     *     .execute();
     * }</pre>
     *
     * @param field nome da coluna
     * @param val   valor a inserir
     * @return esta query para encadeamento
     */
    public Query value(String field, Object val) {
        if (this.insertVals == null) this.insertVals = new java.util.LinkedHashMap<>();
        this.insertVals.put(field, val);
        return this;
    }

    /**
     * Ativa upsert — em caso de chave duplicada, atualiza os campos indicados.
     * Em SQLite usa {@code INSERT OR REPLACE}; em MySQL usa {@code ON DUPLICATE KEY UPDATE};
     * em PostgreSQL usa {@code ON CONFLICT ... DO UPDATE SET} (ver {@link #conflictOn(String)}).
     *
     * @param fields {@code String...} — campos a atualizar em caso de conflito
     * @return esta query para encadeamento
     */
    public Query onDuplicateUpdate(String... fields) {
        this.onDupFields = fields;
        return this;
    }

    /**
     * Define o campo de conflito para upsert no PostgreSQL.
     * Ignorado por MySQL e SQLite.
     *
     * @param field {@code String} — campo com UNIQUE constraint que define o conflito
     * @return esta query para encadeamento
     */
    public Query conflictOn(String field) {
        this.conflictOnField = field;
        return this;
    }

    // --- UPDATE ---

    /**
     * Inicia um UPDATE na tabela indicada.
     *
     * @param table {@code String} — nome da tabela
     * @return esta query para encadeamento
     */
    public Query update(String table) {
        this.type = Type.UPDATE;
        this.updateTable = table;
        return this;
    }

    /**
     * Define os valores a atualizar.
     *
     * @param values {@code Map<String, Object>} — mapa coluna → novo valor
     * @return esta query para encadeamento
     */
    public Query set(Map<String, Object> values) {
        this.setVals = values;
        return this;
    }

    // --- DELETE ---

    /**
     * Inicia um DELETE na tabela indicada.
     *
     * @param table {@code String} — nome da tabela
     * @return esta query para encadeamento
     */
    public Query deleteFrom(String table) {
        this.type = Type.DELETE;
        this.deleteTable = table;
        return this;
    }

    // --- CREATE TABLE ---

    /**
     * Inicia um CREATE TABLE IF NOT EXISTS.
     *
     * <pre>{@code
     * DB.query()
     *     .createTableIfNotExists("alunos")
     *     .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
     *     .column("nome", "TEXT NOT NULL")
     *     .column("turma", "INTEGER")
     *     .execute();
     * }</pre>
     *
     * @param table {@code String} — nome da tabela
     * @return esta query para encadeamento
     */
    public Query createTableIfNotExists(String table) {
        this.type = Type.CREATE_TABLE;
        this.createTableName = table;
        return this;
    }

    /**
     * Adiciona uma coluna ao CREATE TABLE.
     *
     * @param name       {@code String} — nome da coluna
     * @param definition {@code String} — definicao SQL (ex: {@code "TEXT NOT NULL"}, {@code "INTEGER DEFAULT 0"})
     * @return esta query para encadeamento
     */
    public Query column(String name, String definition) {
        tableCols.add(new String[]{name, definition});
        return this;
    }

    // --- Execute ---

    /**
     * Executa a query SELECT e mapeia o resultado para uma lista de instancias do tipo dado.
     * Suporta {@link #groupParent}/{@link #groupChild} para relacoes 1:N.
     *
     * @param type {@code Class<T>} — a classe record destino
     * @param <T>  o tipo do record
     * @return lista de instancias mapeadas
     * @throws RuntimeException se ocorrer erro SQL ou de reflexao
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> execute(Class<T> type) {
        String sql = buildSelectSql();
        List<Object> params = collectWhereParams();

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (groupParentClass != null && groupChildClass != null) {
                    return (List<T>) ResultMapper.mapGrouped(rs,
                        groupParentClass, groupParentKey,
                        groupChildListField, groupChildClass, groupChildKey);
                }
                return ResultMapper.mapList(rs, type);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar SELECT: " + sql + " | " + e.getMessage(), e);
        }
    }

    /**
     * Executa a query SELECT e devolve o resultado bruto sem mapeamento para classe.
     *
     * @return QueryResult com todas as linhas como mapas coluna → valor
     * @throws RuntimeException se ocorrer erro SQL
     */
    public QueryResult executeRaw() {
        String sql = buildSelectSql();
        List<Object> params = collectWhereParams();

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql)) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                List<Map<String, Object>> rows = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    for (int i = 1; i <= cols; i++) {
                        row.put(meta.getColumnLabel(i), rs.getObject(i));
                    }
                    rows.add(row);
                }
                return new QueryResult(rows);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar SELECT: " + sql + " | " + e.getMessage(), e);
        }
    }

    /**
     * Executa INSERT, UPDATE, DELETE ou CREATE TABLE.
     * <ul>
     *   <li>INSERT — devolve o id gerado (ou 0 se nao aplicavel)</li>
     *   <li>UPDATE / DELETE — devolve o numero de linhas afetadas</li>
     *   <li>CREATE TABLE — devolve 0</li>
     * </ul>
     *
     * @return id gerado (INSERT) ou linhas afetadas (UPDATE/DELETE), ou 0 (CREATE TABLE)
     * @throws RuntimeException se ocorrer erro SQL
     */
    public int execute() {
        if (type == null) throw new IllegalStateException("Tipo de query nao definido.");

        String sql;
        List<Object> params;

        switch (type) {
            case INSERT -> { sql = buildInsertSql(); params = new ArrayList<>(insertVals.values()); }
            case UPDATE -> { sql = buildUpdateSql(); params = collectUpdateParams(); }
            case DELETE -> { sql = buildDeleteSql(); params = collectWhereParams(); }
            case CREATE_TABLE -> { sql = buildCreateTableSql(); params = List.of(); }
            default -> throw new IllegalStateException("execute() sem Class<T> e apenas para INSERT/UPDATE/DELETE/CREATE TABLE");
        }

        try (PreparedStatement ps = DB.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindParams(ps, params);
            ps.executeUpdate();

            if (type == Type.INSERT) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
                return 0;
            }
            return ps.getUpdateCount();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar query: " + sql + " | " + e.getMessage(), e);
        }
    }

    // --- SQL builders ---

    private String buildSelectSql() {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(selectCols.isEmpty() ? "*" : String.join(", ", selectCols));
        sb.append(" FROM ").append(fromClause);

        for (String[] j : joins) {
            sb.append(" ").append(j[0]).append(" JOIN ").append(j[1]).append(" ON ").append(j[2]);
        }

        appendWhere(sb);

        if (orderByField != null) {
            sb.append(" ORDER BY ").append(orderByField).append(" ").append(orderByDir);
        }
        if (limitVal >= 0) {
            sb.append(" ").append(config.limitSyntax(limitVal, offsetVal));
        }
        return sb.toString();
    }

    private String buildInsertSql() {
        List<String> cols = new ArrayList<>(insertVals.keySet());
        String colList = String.join(", ", cols);
        String placeholders = cols.stream().map(c -> "?").collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder();
        if (onDupFields != null && !config.supportsOnDuplicateKey()) {
            sb.append("INSERT OR REPLACE INTO ");
        } else {
            sb.append("INSERT INTO ");
        }
        sb.append(insertTable).append(" (").append(colList).append(") VALUES (").append(placeholders).append(")");

        if (onDupFields != null && config.supportsOnDuplicateKey()) {
            sb.append(" ").append(config.onConflictSyntax(onDupFields, conflictOnField));
        }
        return sb.toString();
    }

    private String buildUpdateSql() {
        String sets = setVals.keySet().stream()
            .map(k -> k + " = ?")
            .collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder("UPDATE ").append(updateTable).append(" SET ").append(sets);
        appendWhere(sb);
        if (limitVal >= 0) sb.append(" ").append(config.limitSyntax(limitVal, offsetVal));
        return sb.toString();
    }

    private String buildDeleteSql() {
        StringBuilder sb = new StringBuilder("DELETE FROM ").append(deleteTable);
        appendWhere(sb);
        if (limitVal >= 0) sb.append(" ").append(config.limitSyntax(limitVal, 0));
        return sb.toString();
    }

    private String buildCreateTableSql() {
        String cols = tableCols.stream()
            .map(c -> c[0] + " " + c[1])
            .collect(Collectors.joining(", "));
        return "CREATE TABLE IF NOT EXISTS " + createTableName + " (" + cols + ")";
    }

    private void appendWhere(StringBuilder sb) {
        if (wheres.isEmpty()) return;
        sb.append(" WHERE ");
        List<String> clauses = new ArrayList<>();
        for (Object[] w : wheres) {
            String op = (String) w[1];
            if (op.equals("IS NULL") || op.equals("IS NOT NULL")) {
                clauses.add(w[0] + " " + op);
            } else if (op.equals("IN") || op.equals("NOT IN")) {
                Collection<?> vals = (Collection<?>) w[2];
                String ph = vals.stream().map(v -> "?").collect(Collectors.joining(", "));
                clauses.add(w[0] + " " + op + " (" + ph + ")");
            } else {
                clauses.add(w[0] + " " + op + " ?");
            }
        }
        sb.append(String.join(" AND ", clauses));
    }

    private List<Object> collectWhereParams() {
        List<Object> params = new ArrayList<>();
        for (Object[] w : wheres) {
            String op = (String) w[1];
            if (op.equals("IS NULL") || op.equals("IS NOT NULL")) continue;
            if (op.equals("IN") || op.equals("NOT IN")) {
                params.addAll((Collection<?>) w[2]);
            } else {
                params.add(w[2]);
            }
        }
        return params;
    }

    private List<Object> collectUpdateParams() {
        List<Object> params = new ArrayList<>(setVals.values());
        params.addAll(collectWhereParams());
        return params;
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

}
