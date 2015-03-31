package fr.hibernate.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import fr.hibernate.api.Connexion;
import fr.hibernate.metier.Commande;
import fr.hibernate.metier.Enfant;
import fr.hibernate.metier.Jouet;

public class DAOJouet {

	public boolean insert (Jouet jouet){
		try {
			Connexion.getInstance().insert(jouet);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean delete (Jouet jouet){
		try{
			Connexion.getInstance().delete(jouet);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public Jouet update (Jouet jouet){
		try{
			return Connexion.getInstance().update(jouet);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public static List<Jouet> findAll (){
		return Connexion.getInstance().getAll(Jouet.class);
	}

	public static Jouet find (int idJouet){
		return Connexion.getInstance().find(Jouet.class, idJouet);
	}
	
	/**
	 * Implémentations d’une methode pur Java permettant de calculer le 
	 * nombre des enfants a avoir commandé un jouet
	 */
	public static int getNbEnfantsParJouetJava(long idJouet) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Jouet jouet = em.find(Jouet.class, idJouet);
		List<Commande> commandes = jouet.getCommandes();
		List<Enfant> enfants = new ArrayList<Enfant>();
		int result = 0;
		if(commandes!=null){
			for(Commande c: commandes){
				if(!enfants.contains(c.getEnfant()))
					enfants.add(c.getEnfant()); 
				//Etant donnée qu'on à redefini le equals pour la classe Enfant, 
				//la liste va pouvoir verifier s'il s'agit d'une valeur distincte ou pas.
			}
		} 
		result = enfants.size();
		em.close();
		return result;
	}
	/**
	 * Implémentations d’une methode avec SQL permettant de calculer le 
	 * nombre des enfants a avoir commandé un jouet
	 */
	public static int getNbEnfantsParJouetSQL(long idJouet) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery("SELECT COUNT(c.IdEnfant) as nb FROM Commande c "
				+ "WHERE c.IdJouet=? GROUP BY c.IdJouet");
		query.setParameter(1, idJouet);
		BigInteger result = (BigInteger) query.getSingleResult();
		em.close();
		return result.intValue();
	}
	/**
	 * Implémentations d’une methode avec HQL permettant de calculer le 
	 * nombre des enfants a avoir commandé un jouet
	 */
	public static long getNbEnfantsParJouetHQL(long idJouet) {
		
		String query = "SELECT COUNT(c.enfant.idEnfant) as nb FROM Commande c "
				+ "WHERE c.jouet.idJouet =? GROUP BY c.jouet.idJouet";
		long result = Connexion.getInstance().querySingleResult(query, Long.class, idJouet);
		return result;
	}


}
