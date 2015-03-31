package fr.hibernate.metier;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import fr.hibernate.api.Connexion;
import fr.hibernate.dao.DAOEnfant;
import fr.hibernate.dao.DAOGenerique;

@Entity
@Table(name="Enfant",schema="etrade_hibernate")
public class Enfant {

	private long idEnfant;
	private String nom;
	private String prenom;
	private Date ddn;
	private String adresse;
	private String ville;
	private String code_postal;
	private String tel;
	private String email;
	private List<Commande> commandes = new ArrayList<Commande>();

	public Enfant(){}
	
	public Enfant(String nom, String prenom, Date ddn, String adresse,
			String ville, String code_postal, String tel, String email) {
		this.nom = nom;
		this.prenom = prenom;
		this.ddn = ddn;
		this.adresse = adresse;
		this.ville = ville;
		this.code_postal = code_postal;
		this.tel = tel;
		this.email = email;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdEnfant", unique = true, nullable = false)
	public long getIdEnfant() {
		return idEnfant;
	}
	/**
	 * @param id the id to set
	 */
	public void setIdEnfant(long idEnfant) {
		this.idEnfant = idEnfant;
	}
	/**
	 * @return the nom
	 */
	@Column(name = "Nom", unique = false, nullable = false)
	public String getNom() {
		return nom;
	}
	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	/**
	 * @return the prenom
	 */
	@Column(name = "Prenom", unique = false, nullable = false)
	public String getPrenom() {
		return prenom;
	}
	/**
	 * @param prenom the prenom to set
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	/**
	 * @return the ddn
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Date_Naissance", nullable = false, length = 19)
	public Date getDdn() {
		return ddn;
	}
	/**
	 * @param ddn the ddn to set
	 */
	public void setDdn(Date ddn) {
		this.ddn = ddn;
	}
	/**
	 * @return the adresse
	 */
	@Column(name = "Adresse", unique = false, nullable = false)
	public String getAdresse() {
		return adresse;
	}
	/**
	 * @param adresse the adresse to set
	 */
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	/**
	 * @return the ville
	 */
	@Column(name = "Ville", unique = false, nullable = false)
	public String getVille() {
		return ville;
	}
	/**
	 * @param ville the ville to set
	 */
	public void setVille(String ville) {
		this.ville = ville;
	}
	/**
	 * @return the code_postal
	 */
	@Column(name = "Code_Postal", unique = false, nullable = false)
	public String getCode_postal() {
		return code_postal;
	}
	/**
	 * @param code_postal the code_postal to set
	 */
	public void setCode_postal(String code_postal) {
		this.code_postal = code_postal;
	}
	/**
	 * @return the tel
	 */
	@Column(name = "Telephone", unique = false, nullable = false)
	public String getTel() {
		return tel;
	}
	/**
	 * @param tel the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}
	/**
	 * @return the email
	 */
	@Column(name = "Email", unique = false, nullable = false)
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the commandes
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "enfant")
	@Fetch(FetchMode.JOIN)
	public List<Commande> getCommandes() {
		return commandes;
	}

	/**
	 * @param commandes the commandes to set
	 */
	public void setCommandes(List<Commande> commandes) {
		this.commandes = commandes;
	}

	@Transient
	public boolean persister(){
		return DAOGenerique.insert(this);
	}
	@Transient
	public boolean delete (){
		return DAOGenerique.delete(this);
	}
	@Transient
	public Enfant update (){
		return DAOGenerique.update(this);
	}

	@Override
	public boolean equals(Object other) { 
		if (this == other) return true; 
		if ( !(other instanceof Enfant) ) return false;  
		final Enfant obj = (Enfant) other; 
		if ( obj.getIdEnfant()!=getIdEnfant() ) 
			return false;           
		return true; 
	}
	@Override
	public String toString() {
		return "id : "+idEnfant+", nom : "+nom+", prenom : "+prenom+", adresse : "+adresse+", code postal : "+code_postal
				+", ville :"+ville+", date de naissance : "+ddn+", tel : "+tel+", email : "+email;
	}
	@Transient
	public int getNbCommandeJava() {
		return DAOEnfant.getNbCommandeJava(this.idEnfant);
	}
	@Transient
	public long getNbCommandeHQL() {
		return DAOEnfant.getNbCommandeHQL(this.idEnfant);
	}
	@Transient
	public long getNbCommandeSQL() {
		return DAOEnfant.getNbCommandeSQL(this.idEnfant);
	}

}
