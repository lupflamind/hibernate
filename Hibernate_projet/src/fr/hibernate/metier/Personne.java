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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity
@Table(name="Personne")
public class Personne {

	public Personne(){}
	
	public Personne(String nom, String prenom) {
		this.nom = nom;
		this.prenom = prenom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((prenom == null) ? 0 : prenom.hashCode());
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
		Personne other = (Personne) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (prenom == null) {
			if (other.prenom != null)
				return false;
		} else if (!prenom.equals(other.prenom))
			return false;
		return true;
	}


	private int idPersonne;
	private String nom;
	private String prenom;

	private List<Personne> relationsDirectes;
	private List<Formation> formations;
	private List<Poste> postes;
	private List<Groupe> groupes;
	private List<Personne> personnesVus;

	/**
	 * @return the idPersonne
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "IdPersonne", unique = true, nullable = false)
	public int getIdPersonne() {
		return idPersonne;
	}

	/**
	 * @param idPersonne the idPersonne to set
	 */
	public void setIdPersonne(int idPersonne) {
		this.idPersonne = idPersonne;
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
	 * @return the formations
	 */
	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "personnes")
	@Fetch(FetchMode.JOIN)
	public List<Formation> getFormations() {
		return formations;
	}

	/**
	 * @param formations the formations to set
	 */
	public void setFormations(List<Formation> formations) {
		this.formations = formations;
	}

	/**
	 * @return the postes
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "personne")
	@Fetch(FetchMode.JOIN)
	public List<Poste> getPostes() {
		return postes;
	}

	/**
	 * @param postes the postes to set
	 */
	public void setPostes(List<Poste> postes) {
		this.postes = postes;
	}

	/**
	 * @return the personnesVus
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	public List<Personne> getPersonnesVus() {
		return personnesVus;
	}

	/**
	 * @param personnesVus the personnesVus to set
	 */
	public void setPersonnesVus(List<Personne> personnesVus) {
		this.personnesVus = personnesVus;
	}

	/**
	 * @param relationsDirectes the relationsDirectes to set
	 */
	public void setRelationsDirectes(List<Personne> relationsDirectes) {
		this.relationsDirectes = relationsDirectes;
	}

	/**
	 * @param groupes the groupes to set
	 */
	public void setGroupes(List<Groupe> groupes) {
		this.groupes = groupes;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.JOIN)
	public List<Personne> getRelationsDirectes() {
		return relationsDirectes;
	}

	/**
	 * Relations d'un niveau
	 * 
	 */
	@Transient
	public List<Personne> getRelationsParNiveau(int niveau) {
		if (relationsDirectes==null)
			return null;
		if (niveau==1)
			return relationsDirectes;
		List<Personne> current = new ArrayList<Personne>(relationsDirectes);
		List<Personne> neww = new ArrayList<Personne>();
		for (int i = 2; i<=niveau;i++){
			for (Personne p : current)
				neww.addAll(p.getRelationsDirectes());
			current.clear();current.addAll(neww);neww.clear();
		}
		return current;
	}

	@Transient
	public Poste getPosteActuel() {
		for (Poste p : postes){
			if (p.getDateFin()==null)
				return p;
		}
		return null;
	}

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "personnes")
	@Fetch(FetchMode.JOIN)
	public List<Groupe> getGroupes() {
		return groupes;
	}

	@Transient
	public List<Personne> getRelationsCommunes(Personne personne){
		if (personne==null||relationsDirectes==null||relationsDirectes.isEmpty()
				||personne.getRelationsDirectes()==null||personne.getRelationsDirectes().isEmpty())
			return null;

		List<Personne> personnes = new ArrayList<Personne>();
		for (Personne p : relationsDirectes){
			if (personne.getRelationsDirectes().contains(p))
				personnes.add(p);
		}
		return personnes;
	}

}