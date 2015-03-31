package fr.hibernate.metier;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import fr.hibernate.dao.DAOGenerique;

@Entity
@Table(name="Commande",schema="etrade_hibernate")
public class Commande {

	private int idCommande;
	private Enfant enfant;
	private Jouet jouet;
	private Date date_debut;
	private Date date_fin;

	public Commande(){}
	
	public Commande(Enfant enfant, Jouet jouet) {
		this.enfant = enfant;
		this.jouet = jouet;
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdCommande", unique = true, nullable = false)
	public int getIdCommande() {
		return idCommande;
	}
	/**
	 * @param id the id to set
	 */

	public void setIdCommande(int idCommande) {
		this.idCommande = idCommande;
	}
	/**
	 * @return the enfant
	 */
	@ManyToOne/*(fetch=FetchType.LAZY)*/
	@JoinColumn(name = "IdEnfant", nullable = false)
	public Enfant getEnfant() {
		return enfant;
	}
	/**
	 * @param enfant the enfant to set
	 */
	public void setEnfant(Enfant enfant) {
		this.enfant = enfant;
	}
	/**
	 * @return the jouet
	 */
	@ManyToOne/*(fetch=FetchType.LAZY)*/
	@JoinColumn(name = "IdJouet", nullable = false)
	public Jouet getJouet() {
		return jouet;
	}
	/**
	 * @param jouet the jouet to set
	 */
	public void setJouet(Jouet jouet) {
		this.jouet = jouet;
	}
	/**
	 * @return the date_debut
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Date_Debut", nullable = true, length = 19, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	public Date getDate_debut() {
		return date_debut;
	}
	/**
	 * @param date_debut the date_debut to set
	 */
	public void setDate_debut(Date date_debut) {
		this.date_debut = date_debut;
	}
	/**
	 * @return the date_fin
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "Date_Fin", nullable = true, length = 19)
	public Date getDate_fin() {
		return date_fin;
	}
	/**
	 * @param date_fin the date_fin to set
	 */
	public void setDate_fin(Date date_fin) {
		this.date_fin = date_fin;
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
	public Commande update (){
		return DAOGenerique.update(this);
	}

	@Override
	public boolean equals(Object other) { 
		if (this == other) return true; 
		if ( !(other instanceof Enfant) ) return false;  
		final Commande obj = (Commande) other; 
		if ( obj.getEnfant().getIdEnfant()!=getEnfant().getIdEnfant()||obj.getJouet().getIdJouet()!=getJouet().getIdJouet() ) 
			return false;           
		return true; 
	}
	@Override
	public String toString() {
		return "[Enfant]= "+enfant+" [Jouet]= "+jouet+" // date d�but : "+date_debut+", date fin : "+date_fin;
	}


}
