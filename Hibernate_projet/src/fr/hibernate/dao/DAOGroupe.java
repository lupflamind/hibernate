package fr.hibernate.dao;

import java.util.List;

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


}
