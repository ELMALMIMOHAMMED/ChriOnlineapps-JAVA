package services;

public class AuthService {

    public boolean login(String username, String password) {

        // simulation login
        if(username.equals("admin") && password.equals("1234")){
            return true;
        }

        return false;
    }

}
