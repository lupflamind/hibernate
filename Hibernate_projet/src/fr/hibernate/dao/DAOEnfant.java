package fr.hibernate.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;

import fr.hibernate.api.Connexion;
import fr.hibernate.metier.Commande;
import fr.hibernate.metier.Enfant;

public class DAOEnfant {

	public boolean insert (Enfant enfant){
		try {
			Connexion.getInstance().insert(enfant);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean delete (Enfant enfant){
		try{
			Connexion.getInstance().delete(enfant);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public Enfant update (Enfant enfant){
		try{
			return Connexion.getInstance().update(enfant);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public static List<Enfant> findAll (){
		return Connexion.getInstance().getAll(Enfant.class);
	}

	public static Enfant find (int idEnfant){
		return Connexion.getInstance().find(Enfant.class, idEnfant);
	}
	
	/**
	 * Implémentations d’une methode pur Java permettant de calculer le 
	 * nombre de commandes par enfants
	 */
	public static int getNbCommandeJava(long idEnfant) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Enfant enfant = em.find(Enfant.class, idEnfant);
		int result;
		if(enfant.getCommandes()!=null){
			result = enfant.getCommandes().size();
		} else result = 0;		
		em.close();
		return result;
	}
	/**
	 * Implémentations d’une methode utilisant HQL permettant de calculer le 
	 * nombre de commandes par enfants
	 */
	public static int getNbCommandeSQL(long idEnfant) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery("SELECT COUNT(c.IdEnfant) as nb FROM Commande c WHERE c.IdEnfant=? GROUP BY c.IdEnfant");
		query.setParameter(1, idEnfant);
		BigInteger result = (BigInteger) query.getSingleResult();
		em.close();
		return result.intValue();
	}
	/**
	 * Implémentations d’une methode utilisant SQL permettant de calculer le 
	 * nombre de commandes par enfants
	 */
	public static long getNbCommandeHQL(long idEnfant) {
		
		String query = "SELECT COUNT(c.enfant.idEnfant) as nb FROM Commande c WHERE c.enfant.idEnfant =? GROUP BY c.enfant.idEnfant";
		long result = Connexion.getInstance().querySingleResult(query, Long.class, idEnfant);
		return result;
	}
}
