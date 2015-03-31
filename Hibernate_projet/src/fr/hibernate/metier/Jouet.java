package fr.hibernate.metier;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import fr.hibernate.dao.DAOEnfant;
import fr.hibernate.dao.DAOGenerique;
import fr.hibernate.dao.DAOJouet;


@Entity
@Table(name="Jouet",schema="etrade_hibernate")
public class Jouet {

	private long idJouet;
	private String nom;
	private String description;
	private List<Commande> commandes = new ArrayList<Commande>();

	public Jouet(){}
	
	public Jouet(String nom, String description) {
		this.nom = nom;
		this.description = description;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdJouet", unique = true, nullable = false)
	public long getIdJouet() {
		return idJouet;
	}
	/**
	 * @param id the id to set
	 */
	public void setIdJouet(long idJouet) {
		this.idJouet = idJouet;
	}
	/**
	 * @return the nom
	 */
	@Column(name = "Nom", unique = true, nullable = false)
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
	 * @return the description
	 */
	@Column(name = "Description", nullable = false)
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 
	 * @return the commandes
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "jouet")
	public List<Commande> getCommandes() {
		return commandes;
	}
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
	public Jouet update (){
		return DAOGenerique.update(this);
	}

	@Override
	public boolean equals(Object other) { 
		if (this == other) return true; 
		if ( !(other instanceof Jouet) ) return false;  
		final Jouet obj = (Jouet) other; 
		if ( obj.getIdJouet()!=getIdJouet() ) 
			return false;           
		return true; 
	}
	@Override
	public String toString() {
		return " id : "+idJouet+", nom : "+nom+", description : "+description;
	}
	
	@Transient
	public int getNbEnfantsParJouetJava() {
		return DAOJouet.getNbEnfantsParJouetJava(this.idJouet);
	}
	@Transient
	public long getNbEnfantsParJouetHQL() {
		return DAOJouet.getNbEnfantsParJouetHQL(this.idJouet);
	}
	@Transient
	public long getNbEnfantsParJouetSQL() {
		return DAOJouet.getNbEnfantsParJouetSQL(this.idJouet);
	}


}
