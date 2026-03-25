package ui;

public class SessionContext {

    private static String token;
    private static int userId = -1;
    private static String role = "GUEST";

    // =========================
    // 🔑 TOKEN
    // =========================
    public static String getToken() {
        return token;
    }

    public static void setToken(String t) {
        token = t;
    }

    // =========================
    // 👤 USER ID
    // =========================
    public static int getUserId() {
        return userId;
    }

    public static void setUserId(int id) {
        userId = id;
    }

    // =========================
    // 🛡️ ROLE
    // =========================
    public static String getRole() {
        return role;
    }

    public static void setRole(String r) {
        role = r;
    }

    // =========================
    // ✅ AUTH CHECK
    // =========================
    public static boolean isAuthenticated() {
        return token != null && !token.isEmpty();
    }

    // =========================
    // 👑 ADMIN CHECK
    // =========================
    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    // =========================
    // 🔓 LOGOUT
    // =========================
    public static void clear() {
        token = null;
        userId = -1;
        role = "GUEST";
    }
}