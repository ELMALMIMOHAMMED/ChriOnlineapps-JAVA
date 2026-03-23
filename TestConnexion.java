import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import common.BaseDonnees;
import dao.UserDAO;
import services.Authentification;
import models.User;

public class TestConnexion {

	public static void main(String[] args) {

		try {

			// 1️⃣ Connexion à la base
			Connection connexion = BaseDonnees.getConnection();
			System.out.println("Connexion réussie.");

			// 2️⃣ Création du DAO
			UserDAO userDAO = new UserDAO(connexion);

			// 3️⃣ Création du service d'authentification
			Authentification auth = new Authentification(userDAO);

			// 4️⃣ Création d'un utilisateur test
			User user = new User(
				2,
				"Louay2",
				"louay1234",
				"louayhamouchi3@gmail.com",
				704113619,
				new Date(System.currentTimeMillis()),
				"Client"
			);

			// 5️⃣ Test inscription
			auth.registerUser(user);

			// 6️⃣ Test login avec email
			User loggedUser = auth.loginByEmail("louayhamouchi3@email.com", "louay1234");

			if (loggedUser != null) {
				System.out.println("Utilisateur connecté : " + loggedUser.get_username());
			}

			// 7️⃣ Test login avec téléphone
			User phoneLogin = auth.loginByPhoneNumber(704113618, "louay123");

			if (phoneLogin != null) {
				System.out.println("Connexion par téléphone réussie.");
			}

			connexion.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}