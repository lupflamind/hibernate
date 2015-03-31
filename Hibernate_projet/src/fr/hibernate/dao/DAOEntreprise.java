package fr.hibernate.dao;

import java.util.List;

import fr.hibernate.api.Connexion;
import fr.hibernate.metier.Entreprise;

public class DAOEntreprise {
	public boolean insert (Entreprise Entreprise){
		try {
			Connexion.getInstance().insert(Entreprise);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean delete (Entreprise Entreprise){
		try{
			Connexion.getInstance().delete(Entreprise);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public Entreprise update (Entreprise Entreprise){
		try{
			return Connexion.getInstance().update(Entreprise);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static List<Entreprise> findAll (){
		return Connexion.getInstance().getAll(Entreprise.class);
	}
	public static Entreprise find (int idEntreprise){
		return Connexion.getInstance().find(Entreprise.class, idEntreprise);
	}


}
