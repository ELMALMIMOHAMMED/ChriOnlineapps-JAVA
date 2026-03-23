package services;

import common.Message;
import common.BaseDonnees;
import dao.UserDAO;
import models.User;
import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {

    public static Message login(Message request) {
        try {
            Connection conn = BaseDonnees.getConnection();
            UserDAO userDAO = new UserDAO(conn);
            Authentification auth = new Authentification(userDAO);

            // Assume payload is "email,password"
            String[] parts = request.getPayload().split(",");
            if (parts.length != 2) {
                conn.close();
                return new Message("LOGIN", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
            }
            String email = parts[0].trim();
            String password = parts[1].trim();

            User user = auth.loginByEmail(email, password);
            conn.close();

            if (user != null) {
                return new Message("LOGIN", request.getRequestId(), "SUCCESS", "Logged in as " + user.get_username(), "");
            } else {
                return new Message("LOGIN", request.getRequestId(), "ERROR", "", "INVALID_CREDENTIALS");
            }
        } catch (SQLException e) {
            return new Message("LOGIN", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        } catch (Exception e) {
            return new Message("LOGIN", request.getRequestId(), "ERROR", "", "SERVER_ERROR");
        }
    }

    public static Message register(Message request) {
        try {
            Connection conn = BaseDonnees.getConnection();
            UserDAO userDAO = new UserDAO(conn);
            Authentification auth = new Authentification(userDAO);

            // Assume payload is "id,username,password,email,phone,date,role"
            String[] parts = request.getPayload().split(",");
            if (parts.length != 7) {
                conn.close();
                return new Message("REGISTER", request.getRequestId(), "ERROR", "", "INVALID_PAYLOAD");
            }
            int id = Integer.parseInt(parts[0].trim());
            String username = parts[1].trim();
            String password = parts[2].trim();
            String email = parts[3].trim();
            int phone = Integer.parseInt(parts[4].trim());
            java.sql.Date date = java.sql.Date.valueOf(parts[5].trim());
            String role = parts[6].trim();

            User user = new User(id, username, password, email, phone, date, role);

            boolean success = auth.registerUser(user);
            conn.close();

            if (success) {
                return new Message("REGISTER", request.getRequestId(), "SUCCESS", "User registered", "");
            } else {
                return new Message("REGISTER", request.getRequestId(), "ERROR", "", "REGISTRATION_FAILED");
            }
        } catch (SQLException e) {
            return new Message("REGISTER", request.getRequestId(), "ERROR", "", "DATABASE_ERROR");
        } catch (Exception e) {
            return new Message("REGISTER", request.getRequestId(), "ERROR", "", "SERVER_ERROR");
        }
    }
}
