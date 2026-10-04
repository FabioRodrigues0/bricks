package com.bricks.data;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.data.DB;
import fabiorodrigues.bricks.data.QueryResult;
import fabiorodrigues.bricks.data.config.SQLServerConfig;
import fabiorodrigues.bricks.data.config.SQLiteConfig;
import fabiorodrigues.bricks.data.dialect.ColumnType;
import org.junit.jupiter.api.*;

import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Cenario de integracao contra SQL Server em Docker. So corre com {@code MSSQL_SA_PASSWORD} definida.
 * Variaveis opcionais: {@code MSSQL_PORT} (padrao 1433), {@code MSSQL_DATABASE} (padrao {@code escola}).
 */
@Tag("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLServerIntegrationTest {

    record Aluno(int id, String numero, String nome, int turma) {}

    @BeforeAll
    static void setup() throws Exception {
        String password = System.getenv("MSSQL_SA_PASSWORD");
        Assumptions.assumeTrue(password != null && !password.isEmpty(), "MSSQL_SA_PASSWORD nao definida");

        String port = System.getenv().getOrDefault("MSSQL_PORT", "1433");
        String database = System.getenv().getOrDefault("MSSQL_DATABASE", "escola");
        DB.configure(SQLServerConfig.localDocker(database).port(Integer.parseInt(port)).password(password));

        try (Statement st = DB.getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS alunos_it");
        }
    }

    @AfterAll
    static void cleanup() {
        DB.configure(new SQLiteConfig());
    }

    private static void criarTabela() {
        DB.query()
            .createTableIfNotExists("alunos_it")
            .column("id", ColumnType.ID)
            .column("numero", ColumnType.STRING, "NOT NULL UNIQUE")
            .column("nome", ColumnType.STRING, "NOT NULL")
            .column("turma", ColumnType.INTEGER)
            .execute();
    }

    @Test
    @Order(1)
    void createTableIfNotExistsDuasVezes() {
        assertDoesNotThrow(SQLServerIntegrationTest::criarTabela);
        assertDoesNotThrow(SQLServerIntegrationTest::criarTabela);
    }

    @Test
    @Order(2)
    void insereLinhasComIdsGerados() {
        for (int i = 1; i <= 25; i++) {
            QueryResult r = DB.query()
                .insertInto("alunos_it")
                .value("numero", "A" + i)
                .value("nome", "Aluno " + i)
                .value("turma", i % 3)
                .executeResult();
            assertTrue(r.getGeneratedIdAsInt() > 0);
            assertEquals("A" + i, r.first().get("numero"));
        }
    }

    @Test
    @Order(3)
    void paginacaoComOrderBy() {
        List<Aluno> pagina = DB.query()
            .select("id", "numero", "nome", "turma").from("alunos_it")
            .orderBy("id", "ASC")
            .limit(10).offset(20)
            .execute(Aluno.class);

        assertEquals(5, pagina.size());
        assertEquals("A21", pagina.get(0).numero());
    }

    @Test
    @Order(4)
    void distinctComPaginacao() {
        assertThrows(IllegalStateException.class, () -> DB.query()
            .select("DISTINCT turma").from("alunos_it")
            .limit(2)
            .executeRaw());

        QueryResult r = DB.query()
            .select("DISTINCT turma").from("alunos_it")
            .orderBy("turma", "ASC")
            .limit(2)
            .executeRaw();

        assertEquals(2, r.size());
    }

    @Test
    @Order(5)
    void upsertDuasVezesDeixaUmaLinha() {
        for (String nome : List.of("Primeiro", "Segundo")) {
            DB.query()
                .insertInto("alunos_it")
                .value("numero", "U1")
                .value("nome", nome)
                .value("turma", 9)
                .onDuplicateUpdate("nome")
                .conflictOn("numero")
                .execute();
        }

        List<Aluno> r = DB.query()
            .select("id", "numero", "nome", "turma").from("alunos_it")
            .where("numero", "=", "U1")
            .execute(Aluno.class);

        assertEquals(1, r.size());
        assertEquals("Segundo", r.get(0).nome());
    }

    @Test
    @Order(6)
    void whereComAcentos() {
        DB.query().insertInto("alunos_it").values(Map.of("numero", "J1", "nome", "João", "turma", 1)).execute();

        List<Aluno> r = DB.query()
            .select("id", "numero", "nome", "turma").from("alunos_it")
            .where("nome", "=", "João")
            .execute(Aluno.class);

        assertEquals(1, r.size());
        assertEquals("João", r.get(0).nome());
    }

    @Test
    @Order(7)
    void updateEDeleteComOperadorDiferente() {
        int atualizados = DB.query()
            .update("alunos_it")
            .value("turma", 7)
            .where("numero", "=", "J1")
            .execute();
        assertEquals(1, atualizados);

        int apagados = DB.query().deleteFrom("alunos_it").where("turma", "<>", 7).execute();
        assertEquals(26, apagados);
    }
}
