package fr.hibernate.dao;

import java.util.List;

import fr.hibernate.api.Connexion;
import fr.hibernate.metier.Commande;

public class DAOCommande {
	public boolean insert (Commande commande){
		try {
			Connexion.getInstance().insert(commande);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean delete (Commande commande){
		try{
			Connexion.getInstance().delete(commande);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public Commande update (Commande commande){
		try{
			return Connexion.getInstance().update(commande);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static List<Commande> findAll (){
		return Connexion.getInstance().getAll(Commande.class);
	}
	public static Commande find (int idCommande){
		return Connexion.getInstance().find(Commande.class, idCommande);
	}


}
