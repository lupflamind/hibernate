package fr.hibernate.metier;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import fr.hibernate.api.Connexion;


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

	private List<Personne> relationsDirectes = new ArrayList<Personne>();
	private List<Formation> formations = new ArrayList<Formation>();
	private List<Poste> postes = new ArrayList<Poste>();
	private List<Groupe> groupes = new ArrayList<Groupe>();
	private List<Personne> personnesVus = new ArrayList<Personne>();

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
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "personnes")
	//@Fetch(FetchMode.JOIN)
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
	 * @param personnesVus the personnesVus to set
	 */
	public void setPersonnesVus(List<Personne> personnesVus) {
		this.personnesVus = personnesVus;
	}


	/**
	 * @return the personnesVus
	 */
	@ManyToMany(fetch = FetchType.LAZY)
	//@Fetch(FetchMode.JOIN)
	@JoinTable(name ="Personne_PersonneVu", joinColumns = { 
			@JoinColumn(name = "IdPersonne", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "IdPersonneVu", 
			nullable = false, updatable = false) })
	public List<Personne> getPersonnesVus() {
		return personnesVus;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	//@Fetch(FetchMode.JOIN)
	@JoinTable(name ="Personne_PersonneRelation", joinColumns = { 
			@JoinColumn(name = "IdPersonne", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "IdPersonneRelation", 
			nullable = false, updatable = false) })
	public List<Personne> getRelationsDirectes() {
		return relationsDirectes;
	}


	/**
	 * @param relationsDirectes the relationsDirectes to set
	 */
	public void setRelationsDirectes(List<Personne> relationsDirectes) {
		this.relationsDirectes = relationsDirectes;
	}

	@Transient
	public void addRealationDirect(Personne personne){
		relationsDirectes.add(personne);
		personne.getRelationsDirectes().add(personne);
	}

	/**
	 * Relations d'un niveau en java pur
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
			neww.removeAll(current);current.clear();current.addAll(neww);neww.clear();
		}
		return current;
	}

	/**
	 * Relations d'un niveau en Hql
	 * 
	 */
	@Transient
	public List<Personne> getRelationsParNiveauHql(int niveau) {

		StringBuilder sb = new StringBuilder("Select DISTINCT(p"+niveau+") FROM Personne p0");

		for (int i = 1; i<=niveau;i++){
			sb.append(" INNER JOIN p"+(i-1)+".relationsDirectes as p"+i);
		}
		sb.append(" WHERE p0.idPersonne=?");
		for (int i = 1; i<niveau;i++){
			sb.append(" AND p"+niveau+".idPersonne<>p"+i+".idPersonne");
		}
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		TypedQuery<Personne> query = em.createQuery(sb.toString(),Personne.class);
		query.setParameter(1, idPersonne);
		List<Personne> personnes = query.getResultList();
		em.close();
		return personnes;
	}

	/**
	 * Relations d'un niveau en Sql
	 * 
	 */
	public List<Personne> getRelationsParNiveauSql(int niveau) {

		StringBuilder sb = new StringBuilder("Select DISTINCT(p"+(niveau+1)+".IdPersonne),p"+(niveau+1)
				+".Nom,p"+(niveau+1)+".Prenom");
		sb.append(" FROM Personne p0 INNER JOIN Personne_PersonneRelation p1 ON p0.IdPersonne=p1.IdPersonne");
		for (int i = 2; i<=niveau;i++){
			sb.append(" INNER JOIN Personne_PersonneRelation p"+i+" ON p"+(i-1)+".IdPersonneRelation=p"+i+".IdPersonne");
		}
		sb.append(" INNER JOIN Personne p"+(niveau+1)+" ON p"+niveau+".IdPersonneRelation=p"+(niveau+1)+".IdPersonne");
		sb.append(" WHERE p0.IdPersonne=?");
		for (int i = 1; i<niveau;i++){
			sb.append(" AND p"+(niveau+1)+".IdPersonne <>p"+i+".IdPersonneRelation ");
		}
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery(sb.toString(),Personne.class);
		query.setParameter(1, idPersonne);
		List<Personne> personnes = query.getResultList();
		em.close();
		return personnes;
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
	@Fetch(FetchMode.SUBSELECT)
	public List<Groupe> getGroupes() {
		return groupes;
	}

	/**
	 * @param groupes the groupes to set
	 */
	public void setGroupes(List<Groupe> groupes) {
		this.groupes = groupes;
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