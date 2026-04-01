package com.bricks.data;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.data.DB;
import fabiorodrigues.bricks.data.QueryResult;
import fabiorodrigues.bricks.data.WhereOperator;
import fabiorodrigues.bricks.data.config.SQLiteConfig;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DBTest {

    private static File tempDb;

    record Aluno(int id, String nome, int turma) {}
    record AlunoComNotas(int id, String nome, List<Nota> notas) {}
    record Nota(int id, String texto) {}

    @BeforeAll
    static void setup() throws Exception {
        tempDb = Files.createTempFile("bricks_test_", ".db").toFile();
        DB.configure(new SQLiteConfig(tempDb.getAbsolutePath()));

        DB.query()
            .createTableIfNotExists("alunos")
            .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
            .column("nome", "TEXT NOT NULL")
            .column("turma", "INTEGER")
            .execute();
    }

    @AfterAll
    static void cleanup() {
        if (tempDb != null) tempDb.delete();
    }

    @BeforeEach
    void limpar() {
        DB.query().deleteFrom("alunos").execute();
    }

    @Test
    @Order(1)
    void createTableNaoFalha() {
        // Se chegou aqui sem excecao, a tabela foi criada
        assertDoesNotThrow(() ->
            DB.query().createTableIfNotExists("alunos")
                .column("id", "INTEGER PRIMARY KEY AUTOINCREMENT")
                .column("nome", "TEXT NOT NULL")
                .execute()
        );
    }

    @Test
    @Order(2)
    void insertDevolvIdGerado() {
        int id = DB.query()
            .insertInto("alunos")
            .values(Map.of("nome", "Fabio", "turma", 1))
            .execute();

        assertTrue(id > 0);
    }

    @Test
    @Order(3)
    void selectMapeiaRecord() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Ana", "turma", 2)).execute();

        List<Aluno> alunos = DB.query()
            .select("id", "nome", "turma")
            .from("alunos")
            .execute(Aluno.class);

        assertEquals(1, alunos.size());
        assertEquals("Ana", alunos.get(0).nome());
        assertEquals(2, alunos.get(0).turma());
    }

    @Test
    @Order(4)
    void whereFiltraResultados() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Ana", "turma", 1)).execute();
        DB.query().insertInto("alunos").values(Map.of("nome", "Bruno", "turma", 2)).execute();

        List<Aluno> turma1 = DB.query()
            .select("id", "nome", "turma")
            .from("alunos")
            .where("turma", "=", 1)
            .execute(Aluno.class);

        assertEquals(1, turma1.size());
        assertEquals("Ana", turma1.get(0).nome());
    }

    @Test
    @Order(5)
    void whereComEnum() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Carlos", "turma", 3)).execute();

        List<Aluno> resultado = DB.query()
            .select("id", "nome", "turma")
            .from("alunos")
            .where("turma", WhereOperator.GTE, 3)
            .execute(Aluno.class);

        assertEquals(1, resultado.size());
    }

    @Test
    @Order(6)
    void whenCondicionalAplicado() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Diana", "turma", 1)).execute();
        DB.query().insertInto("alunos").values(Map.of("nome", "Eva", "turma", 2)).execute();

        Integer turmaFiltro = 1;
        List<Aluno> resultado = DB.query()
            .select("id", "nome", "turma")
            .from("alunos")
            .when(turmaFiltro != null, q -> q.where("turma", "=", turmaFiltro))
            .execute(Aluno.class);

        assertEquals(1, resultado.size());
        assertEquals("Diana", resultado.get(0).nome());
    }

    @Test
    @Order(7)
    void whenCondicionalIgnorado() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Fabio", "turma", 1)).execute();
        DB.query().insertInto("alunos").values(Map.of("nome", "Gabi", "turma", 2)).execute();

        List<Aluno> todos = DB.query()
            .select("id", "nome", "turma")
            .from("alunos")
            .when(false, q -> q.where("turma", "=", 1))
            .execute(Aluno.class);

        assertEquals(2, todos.size());
    }

    @Test
    @Order(8)
    void updateModificaLinhas() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Hugo", "turma", 1)).execute();

        int afetados = DB.query()
            .update("alunos")
            .set(Map.of("nome", "Hugo Silva"))
            .where("nome", "=", "Hugo")
            .execute();

        assertEquals(1, afetados);

        List<Aluno> resultado = DB.query()
            .select("id", "nome", "turma").from("alunos")
            .where("nome", "=", "Hugo Silva")
            .execute(Aluno.class);

        assertEquals(1, resultado.size());
    }

    @Test
    @Order(9)
    void deleteRemoveLinhas() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Ines", "turma", 1)).execute();

        int afetados = DB.query()
            .deleteFrom("alunos")
            .where("nome", "=", "Ines")
            .execute();

        assertEquals(1, afetados);

        List<Aluno> resultado = DB.query()
            .select("id", "nome", "turma").from("alunos").execute(Aluno.class);

        assertTrue(resultado.isEmpty());
    }

    @Test
    @Order(10)
    void executeRawDevolveMapa() {
        DB.query().insertInto("alunos").values(Map.of("nome", "Joao", "turma", 1)).execute();

        QueryResult result = DB.query()
            .select("nome", "turma")
            .from("alunos")
            .executeRaw();

        assertFalse(result.isEmpty());
        assertEquals("Joao", result.first().get("nome"));
    }

    @Test
    @Order(11)
    void limitRestringe() {
        for (int i = 1; i <= 5; i++) {
            DB.query().insertInto("alunos").values(Map.of("nome", "Aluno" + i, "turma", i)).execute();
        }

        List<Aluno> resultado = DB.query()
            .select("id", "nome", "turma").from("alunos")
            .orderBy("id", "ASC")
            .limit(3).offset(0)
            .execute(Aluno.class);

        assertEquals(3, resultado.size());
    }

    @Test
    @Order(12)
    void insertOrReplaceComSQLite() {
        int id1 = DB.query()
            .insertInto("alunos")
            .values(Map.of("nome", "Karla", "turma", 1))
            .execute();

        // INSERT OR REPLACE com mesmo id — nao deve falhar
        assertDoesNotThrow(() ->
            DB.query()
                .insertInto("alunos")
                .values(Map.of("id", id1, "nome", "Karla Atualizada", "turma", 1))
                .onDuplicateUpdate("nome")
                .execute()
        );
    }
}
