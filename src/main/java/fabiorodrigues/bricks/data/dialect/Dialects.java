package fabiorodrigues.bricks.data.dialect;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helpers partilhados pelos dialetos.
 */
final class Dialects {

    private Dialects() {}

    /** Coloca cada parte de um nome com ponto entre {@code open}/{@code close}, duplicando {@code close} no interior. */
    static String quote(String name, String open, String close) {
        return Arrays.stream(name.split("\\."))
            .map(part -> open + part.replace(close, close + close) + close)
            .collect(Collectors.joining("."));
    }

    static void validatePagination(Integer limit, Integer offset) {
        if (limit != null && limit < 1) {
            throw new IllegalArgumentException("limit tem de ser >= 1 (recebido: " + limit + ")");
        }
        if (offset != null && offset < 0) {
            throw new IllegalArgumentException("offset tem de ser >= 0 (recebido: " + offset + ")");
        }
    }

    /** Paginacao {@code LIMIT n OFFSET m}; {@code noLimit} e o valor usado quando so ha offset. */
    static String limitOffset(String sql, Integer limit, Integer offset, String noLimit) {
        validatePagination(limit, offset);
        if (limit == null && offset == null) return sql;
        StringBuilder sb = new StringBuilder(sql).append(" LIMIT ").append(limit != null ? limit.toString() : noLimit);
        if (offset != null) sb.append(" OFFSET ").append(offset);
        return sb.toString();
    }

    static String join(List<String> items, Function<String, String> fn) {
        return items.stream().map(fn).collect(Collectors.joining(", "));
    }

    static String placeholders(int n) {
        return String.join(", ", java.util.Collections.nCopies(n, "?"));
    }

    static void requireColumns(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Upsert requer pelo menos uma coluna.");
        }
    }
}
