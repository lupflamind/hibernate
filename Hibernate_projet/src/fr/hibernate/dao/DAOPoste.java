package fr.hibernate.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import fr.hibernate.api.Connexion;
import fr.hibernate.metier.Poste;

public class DAOPoste {

	public boolean insert (Poste poste){
		try {
			Connexion.getInstance().insert(poste);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public boolean delete (Poste poste){
		try{
			Connexion.getInstance().delete(poste);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	public Poste update (Poste poste){
		try{
			return Connexion.getInstance().update(poste);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}

	}

	public static List<Poste> findAll (){
		return Connexion.getInstance().getAll(Poste.class);
	}

	public static Poste find (int idPoste){
		return Connexion.getInstance().find(Poste.class, idPoste);
	}
	
	/**
	 * Implémentations d’une methode pur Java permettant de calculer le 
	 * nombre des enfants a avoir commandé un poste
	 */
	public static int getNbEnfantsParPosteJava(long idPoste) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Poste poste = em.find(Poste.class, idPoste);
		//List<Poste> commandes = poste.getPostes();
		//List<Enfant> enfants = new ArrayList<Enfant>();
		int result = 0;
		/*if(commandes!=null){
			for(Poste c: commandes){
				if(!enfants.contains(c.getEnfant()))
					enfants.add(c.getEnfant()); 
				//Etant donnée qu'on à redefini le equals pour la classe Enfant, 
				//la liste va pouvoir verifier s'il s'agit d'une valeur distincte ou pas.
			}
		} */
		//result = enfants.size();
		em.close();
		return result;
	}
	/**
	 * Impl�mentations d'une methode avec SQL permettant de calculer le 
	 * nombre des enfants a avoir commandé un poste
	 */
	public static int getNbEnfantsParPosteSQL(long idPoste) {
		EntityManagerFactory emf = Connexion.getInstance().getEmf();
		EntityManager em = emf.createEntityManager();
		Query query = em.createNativeQuery("SELECT COUNT(c.IdEnfant) as nb FROM Poste c "
				+ "WHERE c.IdPoste=? GROUP BY c.IdPoste");
		query.setParameter(1, idPoste);
		BigInteger result = (BigInteger) query.getSingleResult();
		em.close();
		return result.intValue();
	}
	/**
	 * Implémentations d’une methode avec HQL permettant de calculer le 
	 * nombre des enfants a avoir commandé un poste
	 */
	public static long getNbEnfantsParPosteHQL(long idPoste) {
		
		String query = "SELECT COUNT(c.enfant.idEnfant) as nb FROM Poste c "
				+ "WHERE c.poste.idPoste =? GROUP BY c.poste.idPoste";
		long result = Connexion.getInstance().querySingleResult(query, Long.class, idPoste);
		return result;
	}


}
