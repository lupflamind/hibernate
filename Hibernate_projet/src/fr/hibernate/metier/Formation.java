package fr.hibernate.metier;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name="Formation")
public class Formation {

	public Formation(){}
	
	public Formation(String nom, String lieu) {
		this.nom = nom;
		this.lieu = lieu;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lieu == null) ? 0 : lieu.hashCode());
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
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
		Formation other = (Formation) obj;
		if (lieu == null) {
			if (other.lieu != null)
				return false;
		} else if (!lieu.equals(other.lieu))
			return false;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
	private int idFormation;
	private String nom;
	private String lieu;
	private List<Personne> personnes = new ArrayList<Personne>();
	/**
	 * @return the idFormation
	 */

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdFormation", unique = true, nullable = false)
	public int getIdFormation() {
		return idFormation;
	}
	/**
	 * @param idFormation the idFormation to set
	 */
	public void setIdFormation(int idFormation) {
		this.idFormation = idFormation;
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
	 * @return the lieu
	 */
	@Column(name = "Lieu", unique = false, nullable = false)
	public String getLieu() {
		return lieu;
	}
	/**
	 * @param lieu the lieu to set
	 */
	public void setLieu(String lieu) {
		this.lieu = lieu;
	}
	/**
	 * @return the personnes
	 */
	@ManyToMany(fetch = FetchType.EAGER/*, mappedBy = "formations"*/)
	@Fetch(FetchMode.JOIN)
	public List<Personne> getPersonnes() {
		return personnes;
	}
	/**
	 * @param personnes the personnes to set
	 */
	public void setPersonnes(List<Personne> personnes) {
		this.personnes = personnes;
	}

}