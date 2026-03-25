package services;

import com.google.gson.*;
import common.Message;
import database.DBConnection;

import java.sql.*;

public class AuthService {

    public static Message login(Message request) {
        try {
            JsonObject data = JsonParser.parseString(request.getJson()).getAsJsonObject();

            String email = data.get("email").getAsString();
            String password = data.get("password").getAsString();

            Connection c = DBConnection.getConnection();

            PreparedStatement ps = c.prepareStatement(
                    "SELECT * FROM users WHERE email=? AND password=?"
            );
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JsonObject res = new JsonObject();
                res.addProperty("userId", rs.getInt("id"));

                return new Message("LOGIN_RES", "", "SUCCESS", res.toString(), "");
            }

            return new Message("LOGIN_RES", "", "FAIL", "", "INVALID");

        } catch (Exception e) {
            return new Message("LOGIN_RES", "", "FAIL", "", "ERROR");
        }
    }

    public static Message register(Message request) {
        try {
            JsonObject data = JsonParser.parseString(request.getJson()).getAsJsonObject();

            String name = data.get("name").getAsString();
            String email = data.get("email").getAsString();
            String phone = data.get("phone").getAsString();
            String password = data.get("password").getAsString();

            Connection c = DBConnection.getConnection();

            PreparedStatement check = c.prepareStatement(
                    "SELECT * FROM users WHERE email=?"
            );
            check.setString(1, email);

            if (check.executeQuery().next()) {
                return new Message("REGISTER_RES", "", "FAIL", "", "EXISTS");
            }

            PreparedStatement insert = c.prepareStatement(
                    "INSERT INTO users(name,email,phone,password) VALUES (?,?,?,?)"
            );
            insert.setString(1, name);
            insert.setString(2, email);
            insert.setString(3, phone);
            insert.setString(4, password);

            insert.executeUpdate();

            return new Message("REGISTER_RES", "", "SUCCESS", "", "");

        } catch (Exception e) {
            return new Message("REGISTER_RES", "", "FAIL", "", "ERROR");
        }
    }
}