package models;

import java.sql.Date;

public class User {
	private Integer id_user;
	private String username;
	private String hash_password;
	private String email;
	private Integer phone_number;
	private Date date_creation;
	private String role;
	// Getters : Lire les données
	public Integer get_id_user() {return id_user;}
	public String get_username() {return username;}
	public String get_hash_password() {return hash_password;}
	public String get_email() {return email;}
	public Integer get_phone_number() {return phone_number;}
	public Date get_date_creation() {return date_creation;}
	public String get_role() {return role;}
	// Setters : Modifier les données (avec une condition)
	public void set_id_user(Integer id_user) 
	{ if (id_user == null) 
	{ throw new IllegalArgumentException("ID Utilisateur ne peut pas être null.");} this.id_user = id_user;}
	public void set_username(String username) 
	{ if (username == null) 
	{ throw new IllegalArgumentException("Nom de l'utilisateur ne peut pas être null.");} this.username = username;}
	public void set_hash_password(String hash_password) 
	{ if (hash_password == null) 
	{ throw new IllegalArgumentException("Le mot de passe ne peut pas être null.");} this.hash_password = hash_password;}
	public void set_email(String email) 
	{ if (email == null) 
	{ throw new IllegalArgumentException("L'email ne peut pas être null.");} this.email = email;}
	public void set_phone_number(Integer phone_number) 
	{ if (phone_number == null) 
	{ throw new IllegalArgumentException("Le numéro de téléphone ne peut pas être null.");} this.phone_number = phone_number;}
	public void set_date_creation(Date date_creation) 
	{ if (date_creation == null) 
	{ throw new IllegalArgumentException("La date de création ne peut pas être null.");} this.date_creation = date_creation;}
	public void set_role(String role) 
	{ if (role == null) 
	{ throw new IllegalArgumentException("Le rôle ne peut pas être null.");} this.role = role;}
	
	// Constructor pour faciliter la creation des nouveaux utilisateurs
	public User(Integer id_user, String username, String hash_password, String email, Integer phone_number, Date date_creation, String role)
	{
	    this.set_id_user(id_user);
	    this.set_username(username);
	    this.set_hash_password(hash_password);
	    this.set_email(email);
	    this.set_phone_number(phone_number);
	    this.set_date_creation(date_creation);
	    this.set_role(role);
	}
	
}