
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.hibernate.api.Connexion;
import fr.hibernate.dao.DAOGenerique;
import fr.hibernate.metier.Entreprise;
import fr.hibernate.metier.Personne;
import fr.hibernate.metier.Poste;

public class Main {

	static public void main (String[] argv) {
		//exemple1();
		//exemple2();
		//exemple3();
		//exemple4();
		exemple5();
		//exempleNbRelationNiveau2();
		
	}

	/**
	 * List<Poste> en LAZY dans Entreprise et Personne
	 * Toutes les listes en LAZY dans Personne 
	 * (BUG type de chargement quand ManyToMany sur une meme table => toujours EAGER)
	 * entreprise et personne en EAGER dans Poste
	 * Insert, Delete, Update
	 * 
	 */
	private static void exemple1(){
		//1 appel de mathode = 1 entity manager => fermeture de lentity a la fin de la methode
		Entreprise e = new Entreprise("nom","adresse");
		persister(e);// Genere 1 requete Insert (objet transient)

		e.setNom("nom_update");
		e = update(e);//Genere 1 requete Select (merge) et 1 Update
		DAOGenerique.findAll(Entreprise.class);//Genere 1 requete select (List<Poste> en mode Lazy)
		delete(e);//Genere une 1 requete Select (merge) et 1 Delete

		// Insertion dun nouveau jouet
		Personne p = new Personne("nom","prenom");
		persister(p);//Genere 1 requete Insert (objet transient)

		//Modification du jouet
		p.setPrenom("prenom_update");
		p = update(p);//Genere 1 requete Select (merge) et 1 Update 
		// BUG Hibernate: 2 select en plus pour les relationsDirect et PersonnesVus

		//Affichage des jouets
		DAOGenerique.findAll(Personne.class);//Genere 1 requete select (List<Poste> et autres listes en mode Lazy)

		Poste po = new Poste("software engineering", null, null, p, null);
		e = update(e);//Genere 1 requete select (merge) et 1 Insert
		po.setEntreprise(e);
		persister(po);//Genere 1 requete Insert (objet transient)

		po.setDateFin(Calendar.getInstance().getTime());
		po = update(po);//Genere 3 requetes Select (poste, entreprise du poste et personne du poste) (merge) et 1 Update
		DAOGenerique.findAll(Poste.class);//Genere 3 requetes select (Poste,Entreprise et Personne)
		//(Entreprise et Personne dans Poste en mode EAGER et sous select par defaut)
	}


	/**
	 * List<Poste> en EAGER dans Entreprise et Personne
	 * Entreprise et Personne en EAGER par defaut dans Poste
	 * 
	 */
	private static void exemple2(){
		//1 appel de methode = 1 entity manager => fermeture de lentity a la fin de la methode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// Genere 1 requete Insert (objet transient)
		persister(e2);// Genere 1 requete Insert (objet transient)
		DAOGenerique.findAll(Entreprise.class);//Genere 1 requetes select(from entreprise)
		// + 2 requetes select (postes des 2 entreprises) (List<Poste> en mode EAGER)

		// Insertion de nouvelles personnes
		EntityManager em = Connexion.getInstance().getEmf().createEntityManager();
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		em.getTransaction().begin();
		em.persist(j);//Genere 1 requete Insert
		em.persist(j2);//Genere 1 requete Insert
		j.addRealationDirect(j2);//Genere 2 requete Insert => j add a j2 et j2 add a j;
		em.getTransaction().commit();//Genere les requetes contenues dans la transaction
		
		//Affichage des personnes
		DAOGenerique.findAll(Personne.class);//Genere 1 requete select (from personne) 
		//+ 2 requetes select (postes des 2 personnes) (List<Poste> en mode EAGER)

		Poste c = new Poste("software engineering",null,null,j2,e);
		Poste c2 = new Poste("serveur",null,null,j,e2);
		persister(c);//Genere 1 requete Insert (objet Transient)
		persister(c2);//Genere 1 requete Insert (objet Transient)

		DAOGenerique.findAll(Poste.class);//Genere 1 requete select (from Poste) 
		//+ 1 requete select (entreprise du poste) + 1 requete select (postes de lentreprise) 
		//+1 requete select (personne du poste) + 1 requete select (postes de la personne)
	}

	/**
	 * List<Poste> en LAZY dans Entreprise et Personne
	 * Entreprise et Personne en LAZY dans Poste
	 * Utilisation des proxy (Parcourt des collections et affichage des objets)
	 * 
	 */
	private static void exemple3(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de methode = 1 entity manager => fermeture de lentity a la fin de la methode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// Genere 1 requete Insert (objet transient)
		persister(e2);// Genere 1 requete Insert (objet transient)

		// Insertion de nouvelles personnes
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//Genere 1 requete Insert
		persister(j2);//Genere 1 requete Insert

		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//Genere 1 requete Insert (objet Transient)
		persister(c2);//Genere 1 requete Insert (objet Transient)

		EntityManager em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Entreprise> typedquery = em.createQuery("FROM " + Entreprise.class.getSimpleName(),Entreprise.class);
		List<Entreprise> entreprises = typedquery.getResultList();//Genere 1 requete select (List<Poste> et  en mode LAZY)

		for (Entreprise ent : entreprises){
			System.out.println(ent);
			System.out.println(ent.getPostes().size());//1 requete select (postes)
			for (Poste po : ent.getPostes())
				System.out.println(po.getPersonne());//1 requete select (personne)
		}
		em.close();
		em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Personne> typedquery2 = em.createQuery("FROM " + Personne.class.getSimpleName(),Personne.class);
		List<Personne> personnes = typedquery2.getResultList();//Genere 1 requete select (List<Poste> est en mode LAZY)
		for (Personne per : personnes){
			System.out.println(per);
			System.out.println(per.getPostes().size());//1 requete select (postes)
			for (Poste co : per.getPostes())
				System.out.println(co.getPersonne());//Pas de requete: hibernate a la personne dans son cache
		}
		em.close();
		em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Poste> typedquery3 = em.createQuery("FROM " + Poste.class.getSimpleName(),Poste.class);
		List<Poste> postes = typedquery3.getResultList();//Genere 1 requete select (List<Poste> et  en mode LAZY)
		for (Poste po : postes){
			System.out.println(po);//2 requetes (jouet et enfant) => toString affiche lenfant et le jouet de la commande
			System.out.println(po.getEntreprise());// deja charge => rien
			System.out.println(po.getPersonne());// deja charge => rien
			System.out.println(po.getEntreprise().getPostes().size());//1 select (postes dans Entreprise)
			System.out.println(po.getPersonne().getPostes().size());//1 select (postes dans Personne)
			for (Poste co1 : po.getEntreprise().getPostes())
				System.out.println(co1.getPersonne());//deja charge
			for (Poste co2 : po.getPersonne().getPostes())
				System.out.println(co2.getEntreprise());//deja charge

		}
	}

	/**
	 * List<Poste> en EAGER (mode sous-select) dans Entreprise et Personne
	 * Entreprise et Personne en EAGER (mode select) dans Poste
	 * 
	 */
	private static void exemple4(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de methode = 1 entity manager => fermeture de lentity a la fin de la methode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// Genere 1 requete Insert (objet transient)
		persister(e2);// Genere 1 requete Insert (objet transient)
		DAOGenerique.findAll(Entreprise.class);//Genere 1 requetes select(from entreprise)
		// + 1 requete select avec sous-select (postes des 2 entreprise avec un IN)

		// Insertion dun nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//Genere 1 requete Insert
		persister(j2);//Genere 1 requete Insert

		//Affichage des jouets
		DAOGenerique.findAll(Personne.class);//Genere 1 requete select (from personne)
		//+ 1 requete select avec sous select  (postes des 2 personnes avec un IN)

		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//Genere 1 requete Insert (objet Transient)
		persister(c2);//Genere 1 requete Insert (objet Transient)

		DAOGenerique.findAll(Poste.class);//Genere 1 requete select (from poste) 
		//+ 1 requete select (entreprise du poste 1) + 1 requete select (personne du poste 1) 
		//+ 1 requete select (entreprise du poste 2) + 1 requete select (personne du poste 2)
		//+ 1 requete select (postes de la personne du poste 1) + 1 requete select (postes de lentreprise du poste 1)
		//+ 1 requete select (postes de la personne du poste 2) + 1 requete select (postes de lentreprise du poste 2)
	}

	/**
	 * List<Poste> en EAGER (mode join) dans Entreprise et Personne
	 * Entreprise et Personne en EAGER (mode join) dans Poste
	 * Bug sous hibernate => join mode + HQL 
	 */
	private static void exemple5(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de methode = 1 entity manager => fermeture de lentity a la fin de la methode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// Genere 1 requete Insert (objet transient)
		persister(e2);// Genere 1 requete Insert (objet transient)
		DAOGenerique.find(Entreprise.class, e.getIdEntreprise());
		// 1 requete select avec jointure entre lentreprise, les postes et les personnes
		DAOGenerique.find(Entreprise.class, e2.getIdEntreprise()); 
		// 1 requete select avec jointure entre lentreprise, les postes et les personnes

		// Insertion de 2 personnes
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//Genere 1 requete Insert
		persister(j2);//Genere 1 requete Insert

		//Affichage des jouets
		DAOGenerique.find(Personne.class, j.getIdPersonne());
		// 1 requete select avec jointure entre la personne, les postes et lentreprise
		DAOGenerique.find(Personne.class, j2.getIdPersonne());
		// 1 requete select avec jointure entre la personne, les postes et lentreprise

		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//Genere 1 requete Insert (objet Transient)
		persister(c2);//Genere 1 requete Insert (objet Transient)
		DAOGenerique.find(Poste.class, c.getIdPoste());
		// 1 requete select du poste 1 avec recup de la personne et entreprise par jointure
		//+ 1 requete pour les postes de la personne avec pour chaque poste lentreprise par jointure
		//+ 1 requete pour les commandes de lentreprise avec pour chaque poste la personne
		DAOGenerique.find(Poste.class, c2.getIdPoste());
		// 1 requete select du poste 2 avec recup de la personne et entreprise par jointure
		//+ 1 requete pour les postes de la personne avec pour chaque poste lentreprise par jointure
		//+ 1 requete pour les postes de lentreprise avec pour chaque poste la personne par joiture
	}

	private static void exempleNbRelationNiveau2(){
		// Insertion des personnes
		EntityManager em = Connexion.getInstance().getEmf().createEntityManager();
		em.getTransaction().begin();
		Personne j = new Personne("nom","description");em.persist(j);
		Personne j2 = new Personne("nom2","description2");em.persist(j2);
		Personne j3 = new Personne("nom3","description3");em.persist(j3);
		Personne j4 = new Personne("nom4","description4");em.persist(j4);
		Personne j5 = new Personne("nom5","description5");em.persist(j5);
		Personne j6 = new Personne("nom6","description6");em.persist(j6);
		Personne j7 = new Personne("nom7","description7");em.persist(j7);
		Personne j8 = new Personne("nom8","description8");em.persist(j8);
		Personne j9 = new Personne("nom9","description9");em.persist(j9);
		j.addRealationDirect(j2);j.addRealationDirect(j3);
		j.addRealationDirect(j4);
		j2.addRealationDirect(j6);j2.addRealationDirect(j8);
		j3.addRealationDirect(j5);j3.addRealationDirect(j7);
		j4.addRealationDirect(j9);
		em.getTransaction().commit();
		System.out.println("(Java pur) Nombre de relation N2 de j :"+j.getRelationsParNiveau(2).size());
		System.out.println("(HQL) Nombre de relation N2 de j :"+j.getRelationsParNiveauHql(2).size());
		System.out.println("(SQL) Nombre de relation N2 de j :"+j.getRelationsParNiveauSql(2).size());

	}
	
	private static boolean persister(Object object){
		return DAOGenerique.insert(object);
	}
	
	private static boolean delete (Object object){
		return DAOGenerique.delete(object);
	}
	
	private static <T> T update (T object){
		return DAOGenerique.update(object);
	}
	
	private static void show(Class<?> classe){
		List<?> objects = DAOGenerique.findAll(classe);
		for (Object object : objects)
			System.out.println(object);
	}


}
