package fabiorodrigues.bricks.data.dialect;

/**
 * Tipos de coluna portaveis entre bases de dados.
 * Cada {@link SqlDialect} traduz o tipo para a sintaxe nativa.
 *
 * <pre>{@code
 * DB.query()
 *     .createTableIfNotExists("alunos")
 *     .column("id", ColumnType.ID)
 *     .column("numero", ColumnType.STRING, "NOT NULL UNIQUE")
 *     .column("nome", ColumnType.STRING, "NOT NULL")
 *     .column("turma", ColumnType.INTEGER)
 *     .execute();
 * }</pre>
 */
public enum ColumnType {

    /** Chave primaria inteira com auto-incremento. */
    ID,

    /** Texto curto (ate 255 caracteres), comparavel com {@code =} e indexavel. */
    STRING,

    /** Texto longo. Em SQL Server da {@code NVARCHAR(MAX)}, que continua comparavel com {@code =}. */
    TEXT,

    /** Inteiro de 32 bits. */
    INTEGER,

    /** Inteiro de 64 bits. */
    LONG,

    /** Virgula flutuante de precisao dupla. */
    DOUBLE,

    /** Decimal exato (19,4) — usar para dinheiro. */
    DECIMAL,

    /** Booleano. Em SQLite fica {@code INTEGER} e em SQL Server {@code BIT}. */
    BOOLEAN,

    /** Data sem hora. */
    DATE,

    /** Data com hora. */
    DATETIME
}
