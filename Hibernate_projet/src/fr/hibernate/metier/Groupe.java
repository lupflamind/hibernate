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
@Table(name="Groupe")
public class Groupe {

	public Groupe(){}
	
	public Groupe(String nom) {
		this.nom = nom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Groupe other = (Groupe) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}
	private int idGroupe;
	private String nom;
	private List<Personne> personnes = new ArrayList<Personne>();
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdFormation", unique = true, nullable = false)
	public int getIdGroupe() {
		return idGroupe;
	}
	/**
	 * @param id the id to set
	 */
	public void setIdGroupe(int id) {
		this.idGroupe = id;
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
	 * @return the personnes
	 */
	@ManyToMany(fetch = FetchType.EAGER/*, mappedBy = "groupes"*/)
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