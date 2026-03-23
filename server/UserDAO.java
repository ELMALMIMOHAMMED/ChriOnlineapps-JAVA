package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import models.User;

public class UserDAO {

	private Connection connexion;
	
	// Constructeur : permet d'injecter la connexion à la base de données
	public UserDAO(Connection connexion) {this.connexion = connexion;}

	// Créer un nouvel utilisateur dans la base de données
	public void createUser(User user) throws SQLException {
		
		// Requête SQL pour insérer un utilisateur
		String query = "INSERT INTO user (id_user, username, email, phone_number, hash_password, date_creation, role) VALUES (?,?,?,?,?,?,?)";
		
		// Préparation de la requête
		PreparedStatement statement = connexion.prepareStatement(query);
		
		// Association des paramètres SQL avec les valeurs de l'objet User
		statement.setInt(1, user.get_id_user());
		statement.setString(2, user.get_username());
		statement.setString(3, user.get_email());
		statement.setInt(4, user.get_phone_number());
		statement.setString(5, user.get_hash_password());
		statement.setDate(6, user.get_date_creation());
		statement.setString(7, user.get_role());

		// Exécution de la requête d'insertion
		statement.executeUpdate();
	}

	// Trouver un utilisateur à partir de son email (utile pour le login)
	public User findByEmail(String email) throws SQLException {
		
		String query = "SELECT * FROM user WHERE email = ?";
		
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setString(1, email);
		
		ResultSet rs = statement.executeQuery();

		// Si un utilisateur est trouvé
		if (rs.next()) {
			
			// Création d'un objet User à partir des données de la base
			return new User(
				rs.getInt("id_user"),
				rs.getString("username"),
				rs.getString("hash_password"),
				rs.getString("email"),
				rs.getInt("phone_number"),
				rs.getDate("date_creation"),
				rs.getString("role")
			);
		}

		// Si aucun utilisateur n'est trouvé
		return null;
	}

	// Vérifier si un email existe déjà dans la base (utile pour l'inscription)
	public boolean emailExists(String email) throws SQLException {

		String query = "SELECT COUNT(*) FROM user WHERE email = ?";

		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setString(1, email);

		ResultSet rs = statement.executeQuery();

		// Si le compteur est > 0, l'email existe déjà
		if (rs.next()) {
			return rs.getInt(1) > 0;
		}

		return false;
	}

	// Trouver un utilisateur à partir de son numéro de téléphone
	public User findByPhoneNumber(Integer phone_number) throws SQLException {
		
		String query = "SELECT * FROM user WHERE phone_number = ?";
		
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setInt(1, phone_number);
		
		ResultSet rs = statement.executeQuery();

		// Si un utilisateur est trouvé
		if (rs.next()) {
			
			// Création d'un objet User à partir des données de la base
			return new User(
				rs.getInt("id_user"),
				rs.getString("username"),
				rs.getString("hash_password"),
				rs.getString("email"),
				rs.getInt("phone_number"),
				rs.getDate("date_creation"),
				rs.getString("role")
			);
		}

		// Si aucun utilisateur n'est trouvé
		return null;
	}
	
	// Vérifier si le nombre de téléphone déjà existe
	public boolean phoneNumberExists(Integer phone_number) throws SQLException {
		String query = "SELECT COUNT(*) FROM user WHERE phone_number = ?";
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setInt(1, phone_number);
		ResultSet rs = statement.executeQuery();

		if (rs.next()) {
			return rs.getInt(1) > 0;
		}

		return false;
	}
	
	// Mise à jour des données 
	public void updateUsername(Integer id_user, String username) throws SQLException {
		String query = "UPDATE user SET username = ? WHERE id_user = ?";
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setString(1, username);
		statement.setInt(2, id_user);
		statement.executeUpdate();
	}

	public void updatePassword(Integer id_user, String hash_password) throws SQLException {
		String query = "UPDATE user SET hash_password = ? WHERE id_user = ?";
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setString(1, hash_password);
		statement.setInt(2, id_user);
		statement.executeUpdate();
	}

	public void updateEmail(Integer id_user, String email) throws SQLException {

		String query = "UPDATE user SET email = ? WHERE id_user = ?";

		PreparedStatement statement = connexion.prepareStatement(query);

		statement.setString(1, email);
		statement.setInt(2, id_user);

		statement.executeUpdate();
	}
	public void updatePhoneNumber(Integer id_user, Integer phone_number) throws SQLException {
		String query = "UPDATE user SET phone_number = ? WHERE id_user = ?";
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setInt(1, phone_number);
		statement.setInt(2, id_user);
		statement.executeUpdate();
	}
	// Supprimer la compte
	public void deleteUser(Integer id_user) throws SQLException {
		String query = "DELETE FROM user WHERE id_user = ?";
		PreparedStatement statement = connexion.prepareStatement(query);
		statement.setInt(1, id_user);
		statement.executeUpdate();
	}
}