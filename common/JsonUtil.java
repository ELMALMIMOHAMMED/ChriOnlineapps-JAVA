package common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class JsonUtil {

    private static final Gson gson = new Gson();

    // =============================
    // 🔹 Message → JSON
    // =============================
    public static String toJson(Message m) {
        return gson.toJson(m);
    }

    // =============================
    // 🔹 JSON → Message
    // =============================
    public static Message fromJson(String json) {
        return gson.fromJson(json, Message.class);
    }

    // =============================
    // 🔥 JSON → Map (ROBUSTE)
    // =============================
    public static Map<String, String> toMap(String json) {
        return gson.fromJson(json, new TypeToken<Map<String, String>>(){}.getType());
    }
}