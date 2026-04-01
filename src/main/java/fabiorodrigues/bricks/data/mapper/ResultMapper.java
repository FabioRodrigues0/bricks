package fabiorodrigues.bricks.data.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Mapeia linhas de um {@link ResultSet} para instancias de Java records.
 * Suporta mapeamento flat e mapeamento agrupado pai/filho para relacoes 1:N.
 *
 * <p>O nome dos campos do record pode ser em camelCase — o mapper tenta automaticamente
 * o equivalente em snake_case (ex: {@code idUser} → {@code id_user}).</p>
 *
 * <p>Apenas Java records sao suportados como tipo destino.</p>
 */
public class ResultMapper {

    private ResultMapper() {}

    /**
     * Mapeia todas as linhas do ResultSet para uma lista de instancias do tipo dado.
     *
     * @param rs   o ResultSet a mapear
     * @param type a classe record destino
     * @param <T>  o tipo do record
     * @return lista de instancias mapeadas
     * @throws Exception se ocorrer erro de reflexao ou SQL
     */
    public static <T> List<T> mapList(ResultSet rs, Class<T> type) throws Exception {
        List<T> result = new ArrayList<>();
        while (rs.next()) {
            result.add(map(rs, type));
        }
        return result;
    }

    /**
     * Mapeia o ResultSet agrupando linhas em objetos pai com listas de filhos (relacao 1:N).
     *
     * @param rs              o ResultSet com linhas flat (pode ter duplicatas do pai)
     * @param parentClass     a classe record do pai
     * @param parentKey       o nome do campo/coluna que identifica unicamente o pai
     * @param childListField  o nome do campo {@code List<ChildClass>} no record pai
     * @param childClass      a classe record dos filhos
     * @param childKey        o nome do campo/coluna que identifica unicamente cada filho
     * @param <T>             o tipo do pai
     * @return lista de pais com as listas de filhos preenchidas
     * @throws Exception se ocorrer erro de reflexao ou SQL
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> mapGrouped(ResultSet rs,
                                          Class<T> parentClass, String parentKey,
                                          String childListField, Class<?> childClass, String childKey)
            throws Exception {

        Map<Object, T> parents = new LinkedHashMap<>();
        Map<Object, List<Object>> childrenByParent = new LinkedHashMap<>();
        Set<String> seenChildKeys = new HashSet<>();

        while (rs.next()) {
            Set<String> columns = getColumnNames(rs.getMetaData());
            Object parentKeyVal = rs.getObject(findColumn(columns, parentKey));

            if (!parents.containsKey(parentKeyVal)) {
                T parent = mapWithPlaceholder(rs, parentClass, childListField, new ArrayList<>());
                parents.put(parentKeyVal, parent);
                childrenByParent.put(parentKeyVal, new ArrayList<>());
            }

            // Deduplica filhos pelo childKey
            Object childKeyVal = rs.getObject(findColumn(columns, childKey));
            String childUniqueKey = parentKeyVal + ":" + childKeyVal;
            if (childKeyVal != null && !seenChildKeys.contains(childUniqueKey)) {
                seenChildKeys.add(childUniqueKey);
                childrenByParent.get(parentKeyVal).add(map(rs, childClass));
            }
        }

        List<T> result = new ArrayList<>();
        for (Object key : parents.keySet()) {
            result.add(setChildList(parents.get(key), parentClass, childListField,
                childrenByParent.get(key)));
        }
        return result;
    }

    /**
     * Mapeia uma unica linha do ResultSet para uma instancia do tipo dado.
     *
     * @param rs   o ResultSet posicionado na linha a mapear
     * @param type a classe record destino
     * @param <T>  o tipo do record
     * @return a instancia mapeada
     * @throws Exception se ocorrer erro de reflexao ou SQL
     */
    @SuppressWarnings("unchecked")
    public static <T> T map(ResultSet rs, Class<T> type) throws Exception {
        if (!type.isRecord()) {
            throw new UnsupportedOperationException(
                "Apenas Java records sao suportados. Recebido: " + type.getName());
        }

        RecordComponent[] components = type.getRecordComponents();
        Set<String> columns = getColumnNames(rs.getMetaData());
        Object[] args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent comp = components[i];
            if (List.class.isAssignableFrom(comp.getType())) {
                args[i] = new ArrayList<>();
            } else {
                args[i] = getValue(rs, columns, comp.getName(), comp.getType());
            }
        }

        Constructor<T> ctor = (Constructor<T>) type.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    @SuppressWarnings("unchecked")
    private static <T> T mapWithPlaceholder(ResultSet rs, Class<T> type,
                                             String skipField, Object placeholder) throws Exception {
        RecordComponent[] components = type.getRecordComponents();
        Set<String> columns = getColumnNames(rs.getMetaData());
        Object[] args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent comp = components[i];
            if (comp.getName().equals(skipField)) {
                args[i] = placeholder;
            } else if (List.class.isAssignableFrom(comp.getType())) {
                args[i] = new ArrayList<>();
            } else {
                args[i] = getValue(rs, columns, comp.getName(), comp.getType());
            }
        }

        Constructor<T> ctor = (Constructor<T>) type.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    @SuppressWarnings("unchecked")
    private static <T> T setChildList(T parent, Class<T> cls,
                                       String listFieldName, List<?> children) throws Exception {
        RecordComponent[] components = cls.getRecordComponents();
        Object[] args = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent comp = components[i];
            if (comp.getName().equals(listFieldName)) {
                args[i] = children;
            } else {
                args[i] = comp.getAccessor().invoke(parent);
            }
        }

        Constructor<T> ctor = (Constructor<T>) cls.getDeclaredConstructors()[0];
        ctor.setAccessible(true);
        return ctor.newInstance(args);
    }

    private static Set<String> getColumnNames(ResultSetMetaData meta) throws SQLException {
        Set<String> cols = new LinkedHashSet<>();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            cols.add(meta.getColumnLabel(i).toLowerCase());
        }
        return cols;
    }

    private static String findColumn(Set<String> columns, String fieldName) {
        if (columns.contains(fieldName.toLowerCase())) return fieldName;
        String snake = camelToSnake(fieldName);
        if (columns.contains(snake)) return snake;
        return fieldName; // fallback — deixa o JDBC lancar erro se nao existir
    }

    private static Object getValue(ResultSet rs, Set<String> columns,
                                    String fieldName, Class<?> type) throws SQLException {
        String col = findColumn(columns, fieldName);
        if (!columns.contains(col.toLowerCase())) {
            return getDefault(type);
        }

        if (type == int.class || type == Integer.class)         return rs.getInt(col);
        if (type == long.class || type == Long.class)           return rs.getLong(col);
        if (type == double.class || type == Double.class)       return rs.getDouble(col);
        if (type == float.class || type == Float.class)         return rs.getFloat(col);
        if (type == boolean.class || type == Boolean.class)     return rs.getBoolean(col);
        if (type == String.class)                               return rs.getString(col);
        if (type == LocalDate.class) {
            java.sql.Date d = rs.getDate(col);
            return d != null ? d.toLocalDate() : null;
        }
        if (type == LocalDateTime.class) {
            java.sql.Timestamp ts = rs.getTimestamp(col);
            return ts != null ? ts.toLocalDateTime() : null;
        }
        return rs.getObject(col);
    }

    private static Object getDefault(Class<?> type) {
        if (type == int.class)     return 0;
        if (type == long.class)    return 0L;
        if (type == double.class)  return 0.0;
        if (type == float.class)   return 0f;
        if (type == boolean.class) return false;
        return null;
    }

    /** Converte camelCase para snake_case (ex: {@code idUser} → {@code id_user}). */
    static String camelToSnake(String camel) {
        StringBuilder sb = new StringBuilder();
        for (char c : camel.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sb.append('_').append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
