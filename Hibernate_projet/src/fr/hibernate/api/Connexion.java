package fr.hibernate.api;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public final class Connexion {

	private static volatile Connexion cnx;
	private static final Logger LOG = Logger.getLogger(Connexion.class.getName());
	// == persistence-unit name du fichier persitence.xml
	public static final String ENTITY_MANAGER_FACTORY = "mysql_tp";

	private final EntityManagerFactory emf;

	/**
	 * @return the emf
	 */
	public EntityManagerFactory getEmf() {
		return emf;
	}

	private Connexion() {
		emf = Persistence.createEntityManagerFactory(ENTITY_MANAGER_FACTORY);
	}

	/**
	 * 
	 * @return the connexion instance
	 */
	public static Connexion getInstance() {
		if (cnx == null) {
			cnx = new Connexion();
		}
		return cnx;
	}

	/**
	 * Implements the insertion in database
	 * 
	 * @param <T>
	 * 
	 * @param objects
	 *          objects to be persisted
	 * @return objects
	 */
	public <T> T[] insert(T... objects) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for (Object object : objects) {
			em.persist(object);
		}
		em.getTransaction().commit();
		em.close();
		for (Object object : objects) {
			LOG.info("persist : " + object);
		}
		return objects;
	}

	public <T> T insert(T object) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(object);
		em.getTransaction().commit();
		em.close();
		LOG.info("persist : " + object);
		return object;
	}
	/**
	 * This method implements a generic querySingleResult for our class
	 * 
	 * @param classe
	 *          the type of object which is returned
	 * @param key
	 *          the database id of the object to return
	 * 
	 * @return result
	 */
	public <T> T find(Class<T> classe, Object key) {
		EntityManager em = emf.createEntityManager();
		T result = em.find(classe, key);
		em.close();
		return result;
	}
	/**
	 * Implements the update in database
	 * 
	 * @param object
	 *          object to be updated
	 * @return object the object updated
	 */
	public <T> T update(T object) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		object = em.merge(object);
		em.getTransaction().commit();
		em.close();
		LOG.info("merge : " + object);
		return object;
	}

	/**
	 * Implements the update in database
	 * 
	 * @param objects
	 *          objects to be updated
	 * @return objects updated
	 */
	public <T> T[] update(T... objects) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for (Object object : objects) {
			em.merge(object);
		}
		em.getTransaction().commit();
		em.close();
		for (Object object : objects) {
			LOG.info("merge : " + object);
		}
		return objects;
	}

	

	/**
	 * Implements the delete in database
	 * 
	 * @param objects
	 *          objects to be deleted
	 * @return objects the objects deleted
	 */
	public <T> T[] delete(T... objects) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		for (Object object : objects) {
			object = em.merge(object);
			em.remove(object);
		}
		em.getTransaction().commit();
		em.close();
		for (Object object : objects) {
			LOG.info("remove : " + object);
		}
		return objects;
	}

	/**
	 * Implements the delete in database
	 * 
	 * @param object
	 *          object to be deleted
	 * @return object the object deleted
	 */
	public <T> T delete(T object) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		object = em.merge(object);
		em.remove(object);
		em.getTransaction().commit();
		em.close();
		LOG.info("remove : " + object);
		return object;
	}

	/**
	 * This method implements a generic querySingleResult for our class
	 * 
	 * @param query
	 *          the HQL query to be executed
	 * @param classe
	 *          the type of object which is returned
	 * @param params
	 *          a set of parameters for our request
	 * @return result
	 */
	public <T> T querySingleResult(String query, Class<T> classe, Object... params) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> typedquery = em.createQuery(query, classe);
		int i = 0;
		for (Object object : params) {
			i++;
			typedquery.setParameter(i, object);
		}
		T result = typedquery.getSingleResult();
		em.close();
		return result;
	}

	/**
	 * This method implements a generic queryListResult for our class
	 * 
	 * @param query
	 *          the HQL query to be executed
	 * @param classe
	 *          the type of object which is returned
	 * @param params
	 *          a set of parameters for our request
	 * @return result
	 */
	public <T> List<T> queryListResult(String query, Class<T> classe, Object... params) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> typedquery = em.createQuery(query, classe);
		int i = 0;
		for (Object object : params) {
			i++;
			typedquery.setParameter(i, object);
		}
		List<T> results = typedquery.getResultList();
		em.close();
		return results;
	}

	

	/**
	 * This method implements a generic namedQueryListResult
	 * 
	 * @param name
	 *          the name of our query
	 * @param classe
	 *          the type of object which is returned
	 * @param params
	 *          a set of parameters for our request
	 * @return results
	 */
	public <T> List<T> namedQueryListResult(String name, Class<T> classe, Object... params) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> typedquery = em.createNamedQuery(name, classe);
		int i = 0;
		for (Object object : params) {
			i++;
			typedquery.setParameter(i, object);
		}
		List<T> results = typedquery.getResultList();
		em.close();
		return results;
	}

	/**
	 * This method implements a SELECT * FROM Table query
	 * 
	 * @param classe
	 *          the type of object which is returned
	 * @return result
	 */
	public <T> List<T> getAll(Class<T> classe) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> typedquery = em.createQuery("FROM " + classe.getSimpleName(), classe);
		List<T> result = typedquery.getResultList();
		em.close();
		return result;
	}

	/**
	 * This method implements a SELECT * FROM Table query
	 * 
	 * @param classe
	 *          the type of object which is returned
	 * @return result
	 */
	public <T> List<T> getAll(Class<T> classe, String orderBy) {
		EntityManager em = emf.createEntityManager();
		TypedQuery<T> typedquery = em.createQuery("FROM " + classe.getSimpleName() + " ORDER BY "
				+ orderBy, classe);
		List<T> result = typedquery.getResultList();
		em.close();
		return result;
	}

	/**
	 * Creates a native query and return a list of its results
	 * 
	 * @param query
	 *          the HQL query
	 * @param params
	 *          the list of parameters
	 * @return result
	 */

	public <T> List<T> createNativeQueryAndGetResult(String query,Class<T> classe, Long... params) {
		EntityManager em = emf.createEntityManager();
		Query q = em.createNativeQuery(query);
		int i = 0;
		for (Long param : params) {
			i++;
			q.setParameter(i, param);
		}
		return q.getResultList();
	}
}
