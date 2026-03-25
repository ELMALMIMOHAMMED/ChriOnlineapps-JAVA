package services;

public class SessionServices {

    private static int currentUserId = -1;
    private static String currentEmail;
    private static String currentToken;

    // =========================
    // SET USER
    // =========================
    public static void setCurrentUser(int userId, String email) {
        currentUserId = userId;
        currentEmail = email;

        System.out.println("✅ USER SET -> ID: " + userId + " | EMAIL: " + email);
    }

    // =========================
    // GET USER ID
    // =========================
    public static int getCurrentUserId() {
        return currentUserId;
    }

    // =========================
    // TOKEN
    // =========================
    public static void setCurrentToken(String token) {
        currentToken = token;
    }

    public static String getCurrentToken() {
        return currentToken;
    }

    // =========================
    // LOGIN CHECK
    // =========================
    public static boolean isLoggedIn() {
        return currentUserId > 0;
    }

    // =========================
    // LOGOUT
    // =========================
    public static void logout() {
        currentUserId = -1;
        currentEmail = null;
        currentToken = null;
    }
}
