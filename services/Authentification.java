package services;

import java.sql.SQLException;
import models.User;
import dao.UserDAO;

public class Authentification {

	private UserDAO userDAO;

	// Constructor
	public Authentification(UserDAO userDAO) {this.userDAO = userDAO;}

	// Inscription (0 : Opération échoué. 1 : Opération réussie)
	public boolean registerUser(User user) throws SQLException {
		
		// Vérifier si l'email existe 
		if (userDAO.emailExists(user.get_email())) {
			System.out.println("Cet email existe déjà.");
			return false;
		}

		// Enregister l'utilisateur dans la BD
		userDAO.createUser(user);
		System.out.println("Utilisateur créé.");
		return true;
	}

	// Connexion (avec email)
	public User loginByEmail(String email, String hash_password) throws SQLException {
		
		User user = userDAO.findByEmail(email);

		if (user == null) {
			System.out.println("Aucun utilisateur trouvé avec cet email.");
			return null;
		}

		if (!user.get_hash_password().equals(hash_password)) {
			System.out.println("Mot de passe incorrect.");
			return null;
		}

		System.out.println("Connexion réussie.");
		return user;
	}

	// Connexion (avec numéro de téléphone)
	public User loginByPhoneNumber(Integer phone_number, String hash_password) throws SQLException {
		
		User user = userDAO.findByPhoneNumber(phone_number);

		if (user == null) {
			System.out.println("Aucun utilisateur trouvé avec ce numéro de téléphone.");
			return null;
		}

		if (!user.get_hash_password().equals(hash_password)) {
			System.out.println("Mot de passe incorrect.");
			return null;
		}

		System.out.println("Connexion réussie.");
		return user;
	}
	
	// Déconnexion
	public void logout(User user) {
		System.out.println("Déconnexion réussie pour l'utilisateur : " + user.get_username());
	}
	// Modifier le nom d'utilisateur
	public boolean modifyUsername(Integer id_user, String username) throws SQLException {
		userDAO.updateUsername(id_user, username);
		System.out.println("Nom d'utilisateur modifié avec succès.");
		return true;
	}
	// Modifier le mot de passe
	public boolean modifyPassword(Integer id_user, String hash_password) throws SQLException {
		userDAO.updatePassword(id_user, hash_password);
		System.out.println("Mot de passe modifié avec succès.");
		return true;
	}
	
	// Modifier l'email 
	public boolean modifyEmail(Integer id_user, String email) throws SQLException {

		if (userDAO.emailExists(email)) {
			System.out.println("Cet email est déjà utilisé.");
			return false;
		}

		userDAO.updateEmail(id_user, email);

		System.out.println("Email modifié avec succès.");

		return true;
	}
	// Modifier nombre de téléphone
	public boolean modifyPhoneNumber(Integer id_user, Integer phone_number) throws SQLException {
		if (userDAO.phoneNumberExists(phone_number)) {
			System.out.println("Ce numéro de téléphone existe déjà.");
			return false;
		}

		userDAO.updatePhoneNumber(id_user, phone_number);
		System.out.println("Numéro de téléphone modifié avec succès.");
		return true;
	}
	
	// Supprimer la compte
	public boolean deleteAccount(Integer id_user) throws SQLException {
		userDAO.deleteUser(id_user);
		System.out.println("Compte supprimé avec succès.");
		return true;
	}
	
	// Mot de passe oublié par email
		public boolean forgotPasswordByEmail(String email) throws SQLException {

			User user = userDAO.findByEmail(email);

			if (user == null) {
				System.out.println("Aucun utilisateur trouvé avec cet email.");
				return false;
			}

			String maskedEmail = maskEmail(user.get_email());
			String resetCode = generateResetCode();

			System.out.println("Code de réinitialisation envoyé à : " + maskedEmail);
			System.out.println("Code généré (simulation) : " + resetCode);

			return true;
		}

		// Mot de passe oublié par téléphone
		public boolean forgotPasswordByPhone(Integer phone_number) throws SQLException {

			User user = userDAO.findByPhoneNumber(phone_number);

			if (user == null) {
				System.out.println("Aucun utilisateur trouvé avec ce numéro de téléphone.");
				return false;
			}

			String maskedPhone = maskPhoneNumber(user.get_phone_number());
			String resetCode = generateResetCode();

			System.out.println("Code de réinitialisation envoyé par SMS à : " + maskedPhone);
			System.out.println("Code généré (simulation) : " + resetCode);

			return true;
		}

		// Réinitialiser le mot de passe
		public boolean resetPassword(Integer id_user, String new_hash_password) throws SQLException {
			userDAO.updatePassword(id_user, new_hash_password);
			System.out.println("Mot de passe réinitialisé avec succès.");
			return true;
		}

		// Masquer l'email
		private String maskEmail(String email) {

			int atIndex = email.indexOf("@");

			if (atIndex <= 1) {
				return "***" + email.substring(atIndex);
			}

			String firstLetter = email.substring(0, 1);
			String domain = email.substring(atIndex);

			return firstLetter + "***" + domain;
		}

		// Masquer le numéro de téléphone
		private String maskPhoneNumber(Integer phone_number) {

			String phone = phone_number.toString();
			int length = phone.length();

			if (length <= 2) {
				return phone;
			}

			String lastTwoDigits = phone.substring(length - 2);
			String maskedPart = "";

			for (int i = 0; i < length - 2; i++) {
				maskedPart += "*";
			}

			return maskedPart + lastTwoDigits;
		}

		// Générer un code de réinitialisation
		private String generateResetCode() {
			int code = (int)(Math.random() * 900000) + 100000;
			return String.valueOf(code);
		}
}