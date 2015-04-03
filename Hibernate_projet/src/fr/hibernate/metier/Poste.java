package fr.hibernate.metier;


import static javax.persistence.GenerationType.IDENTITY;

import java.util.*;

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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name="Poste")
public class Poste {

	@Override
	public String toString() {
		return "Poste [idPoste=" + idPoste + ", titre=" + titre
				+ ", dateDebut=" + dateDebut + ", dateFin=" + dateFin
				+ ", personne=" + personne + ", entreprise=" + entreprise + "]";
	}

	public Poste(){}
	
	public Poste(String titre, Date dateDebut, Date dateFin, Personne personne,
			Entreprise entreprise) {
		this.titre = titre;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.personne = personne;
		this.entreprise = entreprise;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateDebut == null) ? 0 : dateDebut.hashCode());
		result = prime * result + ((dateFin == null) ? 0 : dateFin.hashCode());
		result = prime * result + ((titre == null) ? 0 : titre.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Poste other = (Poste) obj;
		if (dateDebut == null) {
			if (other.dateDebut != null)
				return false;
		} else if (!dateDebut.equals(other.dateDebut))
			return false;
		if (dateFin == null) {
			if (other.dateFin != null)
				return false;
		} else if (!dateFin.equals(other.dateFin))
			return false;
		if (titre == null) {
			if (other.titre != null)
				return false;
		} else if (!titre.equals(other.titre))
			return false;
		return true;
	}
	private int idPoste;
	private String titre;
	private Date dateDebut;
	private Date dateFin;
	private Personne personne;
	private Entreprise entreprise;
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdPoste", unique = true, nullable = false)
	public int getIdPoste() {
		return idPoste;
	}
	/**
	 * @param id the id to set
	 */
	public void setIdPoste(int id) {
		this.idPoste = id;
	}
	/**
	 * @return the dateDebut
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DateDebut", nullable = true, length = 19, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	public Date getDateDebut() {
		return dateDebut;
	}
	/**
	 * @param dateDebut the dateDebut to set
	 */
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}
	/**
	 * @return the dateFin
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DateFin", nullable = true, length = 19)
	public Date getDateFin() {
		return dateFin;
	}
	/**
	 * @param dateFin the dateFin to set
	 */
	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}
	/**
	 * @return the personne
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "IdPersonne", nullable = false)
	public Personne getPersonne() {
		return personne;
	}
	/**
	 * @param personne the personne to set
	 */
	public void setPersonne(Personne personne) {
		this.personne = personne;
	}
	/**
	 * @return the entreprise
	 */
	@ManyToOne(fetch=FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "IdEntreprise", nullable = false)
	public Entreprise getEntreprise() {
		return entreprise;
	}
	/**
	 * @param entreprise the entreprise to set
	 */
	public void setEntreprise(Entreprise entreprise) {
		this.entreprise = entreprise;
	}
	/**
	 * @return the titre
	 */
	@Column(name = "Titre", unique = false, nullable = false)
	public String getTitre() {
		return titre;
	}
	/**
	 * @param titre the titre to set
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}

}