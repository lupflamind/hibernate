package fr.hibernate.dao;

import java.util.List;

import fr.hibernate.api.Connexion;

public final class DAOGenerique {
	public final static boolean insert (Object object){
		try {
			Connexion.getInstance().insert(object);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public final static boolean delete (Object object){
		try{
			Connexion.getInstance().delete(object);
			return true;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public final static <T> T update (T object){
		try{
			return Connexion.getInstance().update(object);
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public final static <T> List<T> findAll (Class<T> classe){
		return Connexion.getInstance().getAll(classe);
	}
	public final static <T> Object find (Class<T> classe, int idObject){
		return Connexion.getInstance().find(classe, idObject);
	}




}
