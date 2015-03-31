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
import fr.hibernate.metier.Groupe;

public class DAOGroupe {

	public boolean insert (Groupe groupe){
		try {
			Connexion.getInstance().insert(groupe);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean delete (Groupe groupe){
		try{
			Connexion.getInstance().delete(groupe);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public Groupe update (Groupe groupe){
		try{
			return Connexion.getInstance().update(groupe);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public static List<Groupe> findAll (){
		return Connexion.getInstance().getAll(Groupe.class);
	}

	public static Groupe find (int idGroupe){
		return Connexion.getInstance().find(Groupe.class, idGroupe);
	}
	
	/**
	 * Implémentations d’une methode pur Java permettant de calculer le 
	 * nombre de commandes par groupes
	 */
	public static int getNbCommandeJava(long idGroupe) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Groupe groupe = em.find(Groupe.class, idGroupe);
		int result=0;
		/*if(groupe.getCommandes()!=null){
			result = groupe.getCommandes().size();
		} else result = 0;*/		
		em.close();
		return result;
	}
	/**
	 * Implémentations d’une methode utilisant HQL permettant de calculer le 
	 * nombre de commandes par groupes
	 */
	public static int getNbCommandeSQL(long idGroupe) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery("SELECT COUNT(c.IdGroupe) as nb FROM Commande c WHERE c.IdGroupe=? GROUP BY c.IdGroupe");
		query.setParameter(1, idGroupe);
		BigInteger result = (BigInteger) query.getSingleResult();
		em.close();
		return result.intValue();
	}
	/**
	 * Implémentations d’une methode utilisant SQL permettant de calculer le 
	 * nombre de commandes par groupes
	 */
	public static long getNbCommandeHQL(long idGroupe) {
		
		String query = "SELECT COUNT(c.groupe.idGroupe) as nb FROM Commande c WHERE c.groupe.idGroupe =? GROUP BY c.groupe.idGroupe";
		long result = Connexion.getInstance().querySingleResult(query, Long.class, idGroupe);
		return result;
	}
}
