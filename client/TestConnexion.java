package client;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import models.User;
import server.BaseDonnees;
import server.UserDAO;
import services.Authentification;

public class TestConnexion {

	public static void main(String[] args) {

		try {
			// 1. Connexion à la base de données
			Connection connexion = BaseDonnees.getConnection();
			System.out.println("Connexion réussie à la base de données.");

			// 2. Création des objets principaux
			UserDAO userDAO = new UserDAO(connexion);
			Authentification auth = new Authentification(userDAO);

			// 3. Création d'un utilisateur test
			User user = new User(
				7,
				"louay_test",
				"kakarot312",
				"louayhamouchi5@gmail.com",
				704113628,
				new Date(System.currentTimeMillis()),
				"Client"
			);

			// 4. Test inscription
			System.out.println("\nTest d'inscription");
			auth.registerUser(user);

			// 5. Test connexion par email
			System.out.println("\nTest de connexion par email");
			User userByEmail = auth.loginByEmail("louayhamouchi5@gmail.com", "kakarot312");
			if (userByEmail != null) {
				System.out.println("Connexion par email réussie : " + userByEmail.get_username());
			}

			// 6. Test connexion par téléphone
			System.out.println("\nTest de connexion par téléphone");
			User userByPhone = auth.loginByPhoneNumber(612345678, "kakarot312");
			if (userByPhone != null) {
				System.out.println("Connexion par téléphone réussie : " + userByPhone.get_username());
			}

			// 7. Test modification username
			System.out.println("\nTest de modification username");
			auth.modifyUsername(1, "louay_modifie");

			// 8. Test modification email
			System.out.println("\nTest de modification email");
			auth.modifyEmail(1, "louaynew@gmail.com");

			// 9. Test modification mot de passe
			System.out.println("\nTest de modification mot de passe");
			auth.modifyPassword(1, "KAKAROT132");

			// 10. Test modification téléphone
			System.out.println("\nTest modification téléphone");
			auth.modifyPhoneNumber(1, 699887766);

			// 11. Test mot de passe oublié par email
			System.out.println("\nTest mot de passe oublié par email");
			auth.forgotPasswordByEmail("louaynew@gmail.com");

			// 12. Test mot de passe oublié par téléphone
			System.out.println("\nTest mot de passe oublié par téléphone");
			auth.forgotPasswordByPhone(699887766);

			// 13. Test reset password
			System.out.println("\nTest réinitialisation mot de passe");
			auth.resetPassword(1, "kakarotto1432");

			// 14. Test déconnexion
			System.out.println("\nTest déconnexion");
			User userToLogout = userDAO.findByEmail("louayhamouchi6@gmail.com");
			if (userToLogout != null) {
				auth.logout(userToLogout);
			}

			// 15. Test suppression du compte
			System.out.println("\nTest suppression compte");
			auth.deleteAccount(1);

			// 16. Fermeture de la connexion
			connexion.close();
			System.out.println("\nFin des tests.");

		} catch (SQLException e) {
			System.out.println("Erreur SQL détectée :");
			e.printStackTrace();
		}
	}
}