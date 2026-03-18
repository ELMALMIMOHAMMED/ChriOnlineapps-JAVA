package common;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    // =============================
    // 🔹 Message → JSON
    // =============================
    public static String toJson(Message m) {

        String json = "{"
                + "\"type\":\"" + safe(m.getType()) + "\","
                + "\"requestId\":\"" + safe(m.getRequestId()) + "\","
                + "\"status\":\"" + safe(m.getStatus()) + "\","
                + "\"payload\":\"" + escape(safe(m.getPayload())) + "\","
                + "\"errorCode\":\"" + safe(m.getErrorCode()) + "\""
                + "}";

        return json;
    }

    // =============================
    // 🔹 JSON → Message
    // =============================
    public static Message fromJson(String json) {

        Message m = new Message();

        m.setType(getValue(json, "type"));
        m.setRequestId(getValue(json, "requestId"));
        m.setStatus(getValue(json, "status"));
        m.setPayload(unescape(getValue(json, "payload"))); // 🔥 important
        m.setErrorCode(getValue(json, "errorCode"));

        return m;
    }

    // =============================
    // 🔥 NOUVEAU : JSON → Map
    // =============================
    public static Map<String, String> toMap(String json) {

        Map<String, String> map = new HashMap<>();

        if (json == null || json.isEmpty()) return map;

        json = json.trim();

        // enlever { }
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
        }

        String[] pairs = json.split(",");

        for (String pair : pairs) {

            String[] kv = pair.split(":");

            if (kv.length == 2) {

                String key = kv[0].replace("\"", "").trim();
                String value = kv[1].replace("\"", "").trim();

                map.put(key, value);
            }
        }

        return map;
    }

    // =============================
    // 🔹 Extraction simple
    // =============================
    private static String getValue(String json, String key) {

        String search = "\"" + key + "\":\"";

        int start = json.indexOf(search);

        if (start == -1) {
            return "";
        }

        start = start + search.length();

        int end = json.indexOf("\"", start);

        if (end == -1) {
            return "";
        }

        return json.substring(start, end);
    }

    // =============================
    // 🔥 Escape JSON
    // =============================
    private static String escape(String value) {
        return value.replace("\"", "\\\"");
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"");
    }

    // =============================
    // 🔹 Null safe
    // =============================
    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
