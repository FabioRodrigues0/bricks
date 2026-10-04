package fabiorodrigues.bricks.data;

import fabiorodrigues.bricks.data.config.DbConfig;
import fabiorodrigues.bricks.data.dialect.ColumnType;
import fabiorodrigues.bricks.data.dialect.SqlDialect;
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
 *     .conflictOn("id")
 *     .execute();
 * }</pre>
 *
 * <p>Nota: o metodo de filtro condicional e {@code .when()} — {@code .if()} e uma palavra
 * reservada em Java e nao pode ser usado como nome de metodo.</p>
 */
public class Query {

    private enum Type { SELECT, INSERT, UPDATE, DELETE, CREATE_TABLE, RAW }

    private final SqlDialect dialect;
    private Type type;

    // SELECT
    private final List<String> selectCols = new ArrayList<>();
    private String fromClause;
    private final List<String[]> joins = new ArrayList<>();
    private final List<Object[]> wheres = new ArrayList<>();
    private String orderByField;
    private String orderByDir = "ASC";
    private Integer limitVal;
    private Integer offsetVal;
    private Class<?> groupParentClass;
    private String groupParentKey;
    private String groupChildListField;
    private Class<?> groupChildClass;
    private String groupChildKey;

    // INSERT
    private String insertTable;
    private Map<String, Object> insertVals;
    private String[] onDupFields;
    private List<String> conflictOnFields = List.of();

    // UPDATE
    private String updateTable;
    private Map<String, Object> setVals;

    // DELETE
    private String deleteTable;

    // CREATE TABLE
    private String createTableName;
    private final List<String[]> tableCols = new ArrayList<>();

    // RAW
    private String rawSql;
    private List<Object> rawParams = List.of();

    // UNION — cada entry e [Query query, boolean isAll]
    private final List<Object[]> unions = new ArrayList<>();

    Query(DbConfig config) {
        this.dialect = config.dialect();
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
     * Limita o numero de linhas devolvidas. Apenas para SELECT.
     * Em SQL Server, paginar sem {@link #orderBy} da ordem indefinida.
     *
     * @param n {@code int} — numero maximo de linhas ({@code >= 1})
     * @return esta query para encadeamento
     */
    public Query limit(int n) {
        this.limitVal = n;
        return this;
    }

    /**
     * Define o offset (numero de linhas a saltar). Apenas para SELECT.
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

    /**
     * Adiciona um UNION com outra query — remove duplicados.
     *
     * <pre>{@code
     * DB.query()
     *     .select("id", "nome").from("tabela_a")
     *     .union(
     *         DB.query().select("id", "nome").from("tabela_b")
     *     )
     *     .execute(Classe.class);
     * }</pre>
     *
     * @param other a query a unir
     * @return esta query para encadeamento
     */
    public Query union(Query other) {
        this.unions.add(new Object[]{other, false});
        return this;
    }

    /**
     * Adiciona um UNION ALL com outra query — mantem duplicados.
     *
     * <pre>{@code
     * DB.query()
     *     .select("id", "nome").from("tabela_a")
     *     .unionAll(
     *         DB.query().select("id", "nome").from("tabela_b")
     *     )
     *     .execute(Classe.class);
     * }</pre>
     *
     * @param other a query a unir
     * @return esta query para encadeamento
     */
    public Query unionAll(Query other) {
        this.unions.add(new Object[]{other, true});
        return this;
    }

    // --- RAW ---

    /**
     * Define SQL escrito a mao, para o que o builder nao cobre ({@code GROUP BY}, {@code HAVING},
     * subqueries, {@code OR}, ...). Os valores vao por {@code ?}, pela ordem de {@code params}.
     * O SQL nao e traduzido entre dialetos.
     *
     * <pre>{@code
     * List<TurmaTotal> totais = DB.query()
     *     .raw("SELECT turma, COUNT(*) AS total FROM alunos WHERE nome LIKE ? GROUP BY turma HAVING COUNT(*) > ?",
     *          "J%", 2)
     *     .execute(TurmaTotal.class);
     *
     * int afetados = DB.query()
     *     .raw("UPDATE alunos SET turma = turma + 1 WHERE turma < ?", 3)
     *     .execute();
     * }</pre>
     *
     * <p>Executar com {@link #execute(Class)} ou {@link #executeRaw()} se devolver linhas,
     * ou com {@link #execute()} / {@link #executeResult()} caso contrario
     * (num INSERT devolve o id gerado).</p>
     *
     * @param sql    {@code String} — SQL completo com {@code ?} nos valores
     * @param params {@code Object...} — valores dos {@code ?}, por ordem
     * @return esta query para encadeamento
     */
    public Query raw(String sql, Object... params) {
        this.type = Type.RAW;
        this.rawSql = sql;
        this.rawParams = Arrays.asList(params);
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
     * Adiciona um par campo-valor ao INSERT ou UPDATE, de forma individual e legivel.
     * Alternativa ao {@link #values(Map)} e {@link #set(Map)} para melhor clareza visual.
     *
     * <pre>{@code
     * DB.query()
     *     .insertInto("professores")
     *     .value("nome", "Joao Silva")
     *     .value("grau", "Doutor")
     *     .execute();
     *
     * DB.query()
     *     .update("professores")
     *     .value("grau", "Catedratico")
     *     .where("id", "=", 1)
     *     .execute();
     * }</pre>
     *
     * @param field nome da coluna
     * @param val   valor a inserir ou atualizar
     * @return esta query para encadeamento
     */
    public Query value(String field, Object val) {
        if (this.type == Type.UPDATE) {
            if (this.setVals == null) this.setVals = new LinkedHashMap<>();
            this.setVals.put(field, val);
        } else {
            if (this.insertVals == null) this.insertVals = new LinkedHashMap<>();
            this.insertVals.put(field, val);
        }
        return this;
    }

    /**
     * Ativa upsert — em caso de chave duplicada, atualiza os campos indicados.
     * Em SQLite e PostgreSQL usa {@code ON CONFLICT ... DO UPDATE SET}; em MySQL usa
     * {@code ON DUPLICATE KEY UPDATE}; em SQL Server usa {@code MERGE} (ver {@link #conflictOn(String...)}).
     *
     * <p>Em SQL Server nao se insere valor explicito numa coluna {@code IDENTITY}: fazer upsert
     * por uma chave natural ({@code numero}, {@code email}).</p>
     *
     * @param fields {@code String...} — campos a atualizar em caso de conflito (nenhum = ignorar o conflito)
     * @return esta query para encadeamento
     */
    public Query onDuplicateUpdate(String... fields) {
        this.onDupFields = fields;
        return this;
    }

    /**
     * Define os campos de conflito para upsert.
     * Obrigatorio em SQL Server e PostgreSQL; opcional em SQLite; ignorado por MySQL.
     *
     * @param fields {@code String...} — campos com PK/UNIQUE que definem o conflito
     * @return esta query para encadeamento
     */
    public Query conflictOn(String... fields) {
        this.conflictOnFields = List.of(fields);
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
     *     .column("id", ColumnType.ID)
     *     .column("nome", ColumnType.STRING, "NOT NULL")
     *     .column("turma", ColumnType.INTEGER)
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
     * Adiciona uma coluna ao CREATE TABLE com definicao SQL raw (especifica do dialeto).
     *
     * @param name       {@code String} — nome da coluna
     * @param definition {@code String} — definicao SQL (ex: {@code "TEXT NOT NULL"}, {@code "INTEGER DEFAULT 0"})
     * @return esta query para encadeamento
     */
    public Query column(String name, String definition) {
        tableCols.add(new String[]{name, definition});
        return this;
    }

    /**
     * Adiciona uma coluna ao CREATE TABLE com tipo portavel entre bases de dados.
     *
     * @param name {@code String} — nome da coluna
     * @param type {@code ColumnType} — tipo portavel (ex: {@code ColumnType.STRING})
     * @return esta query para encadeamento
     */
    public Query column(String name, ColumnType type) {
        return column(name, dialect.columnType(type));
    }

    /**
     * Adiciona uma coluna ao CREATE TABLE com tipo portavel e restricoes.
     *
     * <pre>{@code
     * .column("numero", ColumnType.STRING, "NOT NULL UNIQUE")
     * }</pre>
     *
     * @param name        {@code String} — nome da coluna
     * @param type        {@code ColumnType} — tipo portavel
     * @param constraints {@code String} — restricoes SQL (ex: {@code "NOT NULL"}, {@code "DEFAULT 0"})
     * @return esta query para encadeamento
     */
    public Query column(String name, ColumnType type, String constraints) {
        return column(name, dialect.columnType(type) + " " + constraints);
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
        List<Object> params = collectSelectParams();

        try {
            return DB.withConnection(conn -> {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    bindParams(ps, params);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (groupParentClass != null && groupChildClass != null) {
                            return (List<T>) ResultMapper.mapGrouped(rs,
                                groupParentClass, groupParentKey,
                                groupChildListField, groupChildClass, groupChildKey);
                        }
                        return ResultMapper.mapList(rs, type);
                    }
                }
            });
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
        List<Object> params = collectSelectParams();

        try {
            return DB.withConnection(conn -> {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    bindParams(ps, params);
                    try (ResultSet rs = ps.executeQuery()) {
                        return new QueryResult(readRows(rs));
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar SELECT: " + sql + " | " + e.getMessage(), e);
        }
    }

    /**
     * Executa INSERT, UPDATE, DELETE ou CREATE TABLE e devolve um resultado detalhado.
     * <ul>
     *   <li>INSERT — inclui linhas afetadas, chaves geradas e, se possivel, a linha criada em {@link QueryResult#getRows()}</li>
     *   <li>UPDATE / DELETE — inclui linhas afetadas</li>
     *   <li>CREATE TABLE — inclui 0 linhas afetadas</li>
     * </ul>
     *
     * <pre>{@code
     * QueryResult result = DB.query()
     *     .insertInto("alunos")
     *     .values(Map.of("nome", "Fabio", "turma", 1))
     *     .executeResult();
     *
     * int id = result.getGeneratedIdAsInt();
     * Map<String, Object> aluno = result.first();
     * }</pre>
     *
     * @return resultado detalhado da operacao
     * @throws RuntimeException se ocorrer erro SQL
     */
    public QueryResult executeResult() {
        return executeResult(true);
    }

    private QueryResult executeResult(boolean fetchInsertedRow) {
        if (type == null) throw new IllegalStateException("Tipo de query nao definido.");

        String sql;
        List<Object> params;

        switch (type) {
            case INSERT -> { sql = buildInsertSql(); params = new ArrayList<>(insertVals.values()); }
            case UPDATE -> { sql = buildUpdateSql(); params = collectUpdateParams(); }
            case DELETE -> { sql = buildDeleteSql(); params = collectWhereParams(); }
            case CREATE_TABLE -> { sql = buildCreateTableSql(); params = List.of(); }
            case RAW -> { sql = rawSql; params = rawParams; }
            default -> throw new IllegalStateException("executeResult() e apenas para INSERT/UPDATE/DELETE/CREATE TABLE");
        }

        boolean isInsert = isInsert();
        try {
            return DB.withConnection(conn -> {
                try (PreparedStatement ps = isInsert
                        ? conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                        : conn.prepareStatement(sql)) {
                    bindParams(ps, params);
                    int affectedRows = ps.executeUpdate();

                    List<Map<String, Object>> generatedKeys = isInsert ? readGeneratedKeys(ps) : List.of();
                    Object generatedId = firstGeneratedId(generatedKeys);
                    List<Map<String, Object>> rows = isInsert && fetchInsertedRow
                        ? fetchInsertedRow(conn, generatedId)
                        : List.of();

                    return new QueryResult(rows, generatedKeys, affectedRows, generatedId);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar query: " + sql + " | " + e.getMessage(), e);
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
        QueryResult result = executeResult(false);
        if (isInsert()) {
            return result.getGeneratedIdAsInt();
        }
        return result.getAffectedRows();
    }

    private boolean isInsert() {
        return type == Type.INSERT
            || (type == Type.RAW && rawSql.stripLeading().regionMatches(true, 0, "INSERT", 0, 6));
    }

    // --- SQL builders ---

    private String buildSelectSql() {
        if (type == Type.RAW) return rawSql;
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
        String sql = dialect.paginate(sb.toString(), limitVal, offsetVal, orderByField != null);
        sb.setLength(0);
        sb.append(sql);

        for (Object[] entry : unions) {
            Query unionQuery = (Query) entry[0];
            boolean isAll = (boolean) entry[1];
            sb.append(isAll ? " UNION ALL " : " UNION ");
            sb.append(unionQuery.buildSelectSql());
        }
        return sb.toString();
    }

    private String buildInsertSql() {
        // bind segue insertVals.values() — mesma ordem que keySet()
        List<String> cols = new ArrayList<>(insertVals.keySet());
        if (onDupFields != null) {
            return dialect.upsert(insertTable, cols, conflictOnFields, List.of(onDupFields));
        }
        String colList = cols.stream().map(dialect::quoteIdentifier).collect(Collectors.joining(", "));
        String placeholders = cols.stream().map(c -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + dialect.quoteIdentifier(insertTable) + " (" + colList + ") VALUES (" + placeholders + ")";
    }

    private String buildUpdateSql() {
        ensureUpdateHasValues();

        ensureNoPagination("UPDATE");

        String sets = setVals.keySet().stream()
            .map(k -> dialect.quoteIdentifier(k) + " = ?")
            .collect(Collectors.joining(", "));

        StringBuilder sb = new StringBuilder("UPDATE ").append(dialect.quoteIdentifier(updateTable)).append(" SET ").append(sets);
        appendWhere(sb);
        return sb.toString();
    }

    private String buildDeleteSql() {
        ensureNoPagination("DELETE");

        StringBuilder sb = new StringBuilder("DELETE FROM ").append(deleteTable);
        appendWhere(sb);
        return sb.toString();
    }

    private String buildCreateTableSql() {
        String cols = tableCols.stream()
            .map(c -> dialect.quoteIdentifier(c[0]) + " " + c[1])
            .collect(Collectors.joining(", "));
        return dialect.createTableIfNotExists(createTableName, cols);
    }

    private void ensureNoPagination(String statement) {
        if (limitVal != null || offsetVal != null) {
            throw new IllegalStateException(statement + " nao suporta limit/offset — filtrar com .where(...).");
        }
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
        ensureUpdateHasValues();

        List<Object> params = new ArrayList<>(setVals.values());
        params.addAll(collectWhereParams());
        return params;
    }

    private void ensureUpdateHasValues() {
        if (setVals == null || setVals.isEmpty()) {
            throw new IllegalStateException("UPDATE requer pelo menos um valor. Use .value(\"coluna\", valor) ou .set(Map.of(...)).");
        }
    }

    private List<Object> collectSelectParams() {
        if (type == Type.RAW) return rawParams;
        List<Object> params = collectWhereParams();
        for (Object[] entry : unions) {
            Query unionQuery = (Query) entry[0];
            params.addAll(unionQuery.collectSelectParams());
        }
        return params;
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    private List<Map<String, Object>> readGeneratedKeys(PreparedStatement ps) throws SQLException {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            return readRows(keys);
        }
    }

    private Object firstGeneratedId(List<Map<String, Object>> generatedKeys) {
        if (generatedKeys.isEmpty() || generatedKeys.get(0).isEmpty()) {
            return null;
        }
        return generatedKeys.get(0).values().iterator().next();
    }

    private List<Map<String, Object>> fetchInsertedRow(Connection conn, Object generatedId) {
        if (generatedId == null || insertTable == null) {
            return List.of();
        }

        String sql = "SELECT * FROM " + dialect.quoteIdentifier(insertTable) + " WHERE " + dialect.quoteIdentifier("id") + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, generatedId);
            try (ResultSet rs = ps.executeQuery()) {
                return readRows(rs);
            }
        } catch (SQLException ignored) {
            return List.of();
        }
    }

    private List<Map<String, Object>> readRows(ResultSet rs) throws SQLException {
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
        return rows;
    }

}
