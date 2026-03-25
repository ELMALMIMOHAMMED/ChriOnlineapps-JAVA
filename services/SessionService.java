package services;

public class SessionService {

    private static int currentUserId = -1;

    public static void setCurrentUserId(int id) {
        currentUserId = id;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    public static boolean isLoggedIn() {
        return currentUserId != -1;
    }
}