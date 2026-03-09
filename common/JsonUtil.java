package common;

public class JsonUtil {

    // Convertir un objet Message en texte JSON
    public static String toJson(Message m) {

        String json = "{"
                + "\"type\":\"" + safe(m.getType()) + "\","
                + "\"requestId\":\"" + safe(m.getRequestId()) + "\","
                + "\"status\":\"" + safe(m.getStatus()) + "\","
                + "\"payload\":\"" + safe(m.getPayload()) + "\","
                + "\"errorCode\":\"" + safe(m.getErrorCode()) + "\""
                + "}";

        return json;
    }

    // Convertir JSON en objet Message
    public static Message fromJson(String json) {

        Message m = new Message();

        m.setType(getValue(json, "type"));
        m.setRequestId(getValue(json, "requestId"));
        m.setStatus(getValue(json, "status"));
        m.setPayload(getValue(json, "payload"));
        m.setErrorCode(getValue(json, "errorCode"));

        return m;
    }

    // Fonction pour extraire une valeur JSON
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

    // Évite les valeurs null
    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
