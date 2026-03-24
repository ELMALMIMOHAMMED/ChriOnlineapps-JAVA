package common;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {

    // Serialize object to byte array (binary)
    public static byte[] toBinary(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    // Deserialize byte array to object (binary)
    @SuppressWarnings("unchecked")
    public static <T> T fromBinary(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        T obj = (T) ois.readObject();
        ois.close();
        return obj;
    }

    // Legacy JSON methods (keep for compatibility if needed)
    public static String toJson(Message m) {

        String json = "{"
                + "\"type\":\"" + escape(safe(m.getType())) + "\","
                + "\"requestId\":\"" + escape(safe(m.getRequestId())) + "\","
                + "\"status\":\"" + escape(safe(m.getStatus())) + "\","
                + "\"payload\":\"" + escape(safe(m.getPayload())) + "\","
                + "\"errorCode\":\"" + escape(safe(m.getErrorCode())) + "\""
                + "}";

        return json;
    }

    // Convertir JSON en objet Message
    public static Message fromJson(String json) {

        Message m = new Message();

        Map<String, String> map = toMap(json);

        m.setType(map.getOrDefault("type", ""));
        m.setRequestId(map.getOrDefault("requestId", ""));
        m.setStatus(map.getOrDefault("status", ""));
        m.setPayload(map.getOrDefault("payload", ""));
        m.setErrorCode(map.getOrDefault("errorCode", ""));

        return m;
    }

    // =============================
    // 🔥 NOUVEAU : JSON → Map
    // =============================
    public static Map<String, String> toMap(String json) {
        Map<String, String> map = new HashMap<>();

        if (json == null || json.isEmpty()) {
            return map;
        }

        json = json.trim();

        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }

        if (json.isEmpty()) {
            return map;
        }

        // Pattern for string key/value pairs, handles escaped quotes and values with ':' or ','
        Pattern pairPattern = Pattern.compile("\\\"([^\\\\\"]*)\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\\\"])*?)\\\"");
        Matcher matcher = pairPattern.matcher(json);

        while (matcher.find()) {
            String key = unescape(matcher.group(1));
            String value = unescape(matcher.group(2));
            map.put(key, value);
        }

        return map;
    }

    // Évite les valeurs null
    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String unescape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\r", "\r")
                    .replace("\\t", "\t");
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            return "\"" + escape((String) obj) + "\"";
        }
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        if (obj instanceof Message) {
            return toJson((Message) obj);
        }
        if (obj instanceof Map<?, ?>) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (java.util.Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(entry.getKey() == null ? "" : entry.getKey().toString()));
                sb.append(":");
                sb.append(toJson(entry.getValue()));
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Iterable) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            boolean first = true;
            for (Object item : (Iterable<?>) obj) {
                if (!first) sb.append(",");
                first = false;
                sb.append(toJson(item));
            }
            sb.append("]");
            return sb.toString();
        }
        // Fallback for objects
        return "\"" + escape(obj.toString()) + "\"";
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        String trimmed = json.trim();
        if (clazz == String.class) {
            return clazz.cast(unquote(trimmed));
        }
        if (clazz == Integer.class || clazz == int.class) {
            return clazz.cast(Integer.valueOf(trimmed.replaceAll("[\\\"\\s]+", "")));
        }
        if (clazz == Long.class || clazz == long.class) {
            return clazz.cast(Long.valueOf(trimmed.replaceAll("[\\\"\\s]+", "")));
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return clazz.cast(Boolean.valueOf(trimmed));
        }
        if (clazz == Message.class) {
            return clazz.cast(fromJson(trimmed));
        }

        Map<String, String> values = toMap(trimmed);
        if (values.isEmpty()) {
            return null;
        }

        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (java.util.Map.Entry<String, String> entry : values.entrySet()) {
                try {
                    java.lang.reflect.Field field = clazz.getDeclaredField(entry.getKey());
                    field.setAccessible(true);
                    String value = entry.getValue();
                    Class<?> fieldType = field.getType();
                    if (fieldType == String.class) {
                        field.set(instance, value);
                    } else if (fieldType == int.class || fieldType == Integer.class) {
                        field.set(instance, Integer.parseInt(value));
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        field.set(instance, Long.parseLong(value));
                    } else if (fieldType == double.class || fieldType == Double.class) {
                        field.set(instance, Double.parseDouble(value));
                    } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                        field.set(instance, Boolean.parseBoolean(value));
                    } else {
                        // unsupported nested object types in this simple parser
                    }
                } catch (NoSuchFieldException ignored) {
                }
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    private static String unquote(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return unescape(value);
    }
}
