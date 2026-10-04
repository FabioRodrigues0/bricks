package com.bricks.data.dialect;

import static org.junit.jupiter.api.Assertions.*;

import fabiorodrigues.bricks.data.dialect.ColumnType;
import fabiorodrigues.bricks.data.dialect.MySQLDialect;
import fabiorodrigues.bricks.data.dialect.PostgreSQLDialect;
import fabiorodrigues.bricks.data.dialect.SQLServerDialect;
import fabiorodrigues.bricks.data.dialect.SQLiteDialect;
import org.junit.jupiter.api.Test;

import java.util.List;

/** Testa apenas o SQL gerado pelos dialetos — sem base de dados. */
class SQLServerDialectTest {

    private final SQLServerDialect mssql = SQLServerDialect.INSTANCE;
    private final SQLiteDialect sqlite = SQLiteDialect.INSTANCE;

    @Test
    void quoteIdentifierUsaParentesesRetosEEscapa() {
        assertEquals("[alunos]", mssql.quoteIdentifier("alunos"));
        assertEquals("[dbo].[alunos]", mssql.quoteIdentifier("dbo.alunos"));
        assertEquals("[a]]b]", mssql.quoteIdentifier("a]b"));
        assertEquals("\"a\"\"b\"", sqlite.quoteIdentifier("a\"b"));
        assertEquals("`user`", MySQLDialect.INSTANCE.quoteIdentifier("user"));
    }

    @Test
    void paginacaoComOrderBy() {
        assertEquals("SELECT * FROM t ORDER BY id ASC OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY",
            mssql.paginate("SELECT * FROM t ORDER BY id ASC", 10, 20, true));
    }

    @Test
    void paginacaoSemOrderByAcrescentaSelectNull() {
        assertEquals("SELECT * FROM t ORDER BY (SELECT NULL) OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY",
            mssql.paginate("SELECT * FROM t", 5, null, false));
    }

    @Test
    void distinctPaginadoSemOrderByFalha() {
        assertThrows(IllegalStateException.class,
            () -> mssql.paginate("SELECT DISTINCT turma FROM t", 2, null, false));
        assertEquals("SELECT DISTINCT turma FROM t ORDER BY turma ASC OFFSET 0 ROWS FETCH NEXT 2 ROWS ONLY",
            mssql.paginate("SELECT DISTINCT turma FROM t ORDER BY turma ASC", 2, null, true));
    }

    @Test
    void paginacaoSoComOffset() {
        assertEquals("SELECT * FROM t ORDER BY id ASC OFFSET 3 ROWS",
            mssql.paginate("SELECT * FROM t ORDER BY id ASC", null, 3, true));
        assertEquals("SELECT * FROM t LIMIT -1 OFFSET 3", sqlite.paginate("SELECT * FROM t", null, 3, false));
    }

    @Test
    void semPaginacaoDevolveSqlOriginal() {
        assertEquals("SELECT * FROM t", mssql.paginate("SELECT * FROM t", null, null, false));
        assertEquals("SELECT * FROM t", sqlite.paginate("SELECT * FROM t", null, null, false));
    }

    @Test
    void limitZeroOuOffsetNegativoFalham() {
        assertThrows(IllegalArgumentException.class, () -> mssql.paginate("SELECT 1", 0, null, true));
        assertThrows(IllegalArgumentException.class, () -> sqlite.paginate("SELECT 1", 0, null, true));
        assertThrows(IllegalArgumentException.class, () -> mssql.paginate("SELECT 1", 1, -1, true));
    }

    @Test
    void sqlitePaginaComLimitOffset() {
        assertEquals("SELECT * FROM t LIMIT 10 OFFSET 20", sqlite.paginate("SELECT * FROM t", 10, 20, false));
    }

    @Test
    void createTableIfNotExistsUsaObjectId() {
        assertEquals("IF OBJECT_ID(N'[alunos]', N'U') IS NULL CREATE TABLE [alunos] ([id] INT)",
            mssql.createTableIfNotExists("alunos", "[id] INT"));
        assertEquals("IF OBJECT_ID(N'[o''neil]', N'U') IS NULL CREATE TABLE [o'neil] ([id] INT)",
            mssql.createTableIfNotExists("o'neil", "[id] INT"));
    }

    @Test
    void sqliteCreateTableIfNotExists() {
        assertEquals("CREATE TABLE IF NOT EXISTS \"alunos\" (\"id\" INTEGER)",
            sqlite.createTableIfNotExists("alunos", "\"id\" INTEGER"));
    }

    @Test
    void tiposSqlServer() {
        assertEquals("INT IDENTITY(1,1) PRIMARY KEY", mssql.columnType(ColumnType.ID));
        assertEquals("NVARCHAR(255)", mssql.columnType(ColumnType.STRING));
        assertEquals("NVARCHAR(MAX)", mssql.columnType(ColumnType.TEXT));
        assertEquals("BIT", mssql.columnType(ColumnType.BOOLEAN));
        assertEquals("DATETIME2", mssql.columnType(ColumnType.DATETIME));
        for (ColumnType t : ColumnType.values()) {
            assertNotNull(mssql.columnType(t));
            assertNotNull(sqlite.columnType(t));
            assertNotNull(MySQLDialect.INSTANCE.columnType(t));
            assertNotNull(PostgreSQLDialect.INSTANCE.columnType(t));
        }
    }

    @Test
    void upsertMerge() {
        String sql = mssql.upsert("alunos", List.of("numero", "nome", "turma"), List.of("numero"), List.of("nome", "turma"));
        assertEquals("MERGE INTO [alunos] WITH (HOLDLOCK) AS target"
            + " USING (VALUES (?, ?, ?)) AS source ([numero], [nome], [turma])"
            + " ON target.[numero] = source.[numero]"
            + " WHEN MATCHED THEN UPDATE SET target.[nome] = source.[nome], target.[turma] = source.[turma]"
            + " WHEN NOT MATCHED THEN INSERT ([numero], [nome], [turma]) VALUES (source.[numero], source.[nome], source.[turma]);",
            sql);
    }

    @Test
    void upsertMergeSemUpdateSoInsere() {
        String sql = mssql.upsert("alunos", List.of("numero", "nome"), List.of("numero"), List.of());
        assertFalse(sql.contains("WHEN MATCHED"));
        assertTrue(sql.endsWith(";"));
    }

    @Test
    void upsertMergeSemConflitoFalha() {
        assertThrows(IllegalArgumentException.class,
            () -> mssql.upsert("alunos", List.of("nome"), List.of(), List.of("nome")));
    }

    @Test
    void upsertSqliteOnConflict() {
        assertEquals("INSERT INTO \"alunos\" (\"numero\", \"nome\") VALUES (?, ?)"
                + " ON CONFLICT (\"numero\") DO UPDATE SET \"nome\" = excluded.\"nome\"",
            sqlite.upsert("alunos", List.of("numero", "nome"), List.of("numero"), List.of("nome")));
        assertEquals("INSERT INTO \"alunos\" (\"id\", \"nome\") VALUES (?, ?) ON CONFLICT DO NOTHING",
            sqlite.upsert("alunos", List.of("id", "nome"), List.of(), List.of()));
    }

    @Test
    void upsertMySQLEPostgres() {
        assertEquals("INSERT INTO `t` (`a`, `b`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `b` = VALUES(`b`)",
            MySQLDialect.INSTANCE.upsert("t", List.of("a", "b"), List.of(), List.of("b")));
        assertEquals("INSERT INTO \"t\" (\"a\", \"b\") VALUES (?, ?) ON CONFLICT (\"a\") DO UPDATE SET \"b\" = EXCLUDED.\"b\"",
            PostgreSQLDialect.INSTANCE.upsert("t", List.of("a", "b"), List.of("a"), List.of("b")));
        assertThrows(IllegalArgumentException.class,
            () -> PostgreSQLDialect.INSTANCE.upsert("t", List.of("a", "b"), List.of(), List.of("b")));
    }
}
