
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
		exemple1();
		//exemple2();
		//exemple3();
		//exemple4();
		//exemple5();
		//exempleNbPostes();
		//exempleNbEntreprisesParPersonne();
	}

	/**
	 * List<Poste> en LAZY dans Entreprise et Personne
	 * entreprise et personne en EAGER dans Poste
	 * Insert, Delete, Update
	 */
	private static void exemple1(){
		
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)

		e.setNom("nom_update");
		e = update(e);//G�n�re 1 requ�te Select (merge) et 1 Update
		DAOGenerique.findAll(Entreprise.class);//G�n�re 1 requ�te select (List<Poste> en mode Lazy)
		delete(e);//G�n�re une 1 requ�te Select (merge) et 1 Delete

		// Insertion d'un nouveau jouet
		Personne p = new Personne("nom","prenom");
		persister(p);//G�n�re 1 requ�te Insert (objet transient)

		//Modification du jouet
		p.setPrenom("prenom_update");
		p = update(p);//G�n�re 1 requ�te Select (merge) et 1 Update

		//Affichage des jouets
		DAOGenerique.findAll(Personne.class);//G�n�re 1 requ�te select (List<Poste> en mode Lazy)

		Poste po = new Poste("software engineering", null, null, p, null);
		e = update(e);//G�n�re 1 requ�te select (merge) et 1 Insert
		po.setEntreprise(e);
		persister(po);//G�n�re 1 requ�te Insert (objet transient)

		po.setDateFin(Calendar.getInstance().getTime());
		po = update(po);//G�n�re 1 requ�te Select (merge) et 1 Update
		DAOGenerique.findAll(Poste.class);//G�n�re 3 requ�tes select (Poste et Personne en mode EAGER et sous select par d�faut)
	}


	/**
	 * List<Poste> en EAGER dans Entreprise et Personne
	 * Entreprise et Personne en LAZY dans Poste
	 * 
	 */
	private static void exemple2(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)
		persister(e2);// G�n�re 1 requ�te Insert (objet transient)
		DAOGenerique.findAll(Entreprise.class);//G�n�re 1 requ�tes select(from enfant) + 2 requ�tes select (commandes des 2 enfants)
		//(List<Poste> en mode EAGER)

		// Insertion d'un nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//G�n�re 1 requ�te Insert
		persister(j2);//G�n�re 1 requ�te Insert

		//Affichage des jouets
		DAOGenerique.findAll(Personne.class);//G�n�re 1 requ�te select (from jouet) + 2 requ�tes select (commandes des 2 jouets) (List<Poste> en mode EAGER)

		Poste c = new Poste("software engineering",null,null,j2,e);
		Poste c2 = new Poste("serveur",null,null,j,e2);
		persister(c);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c2);//G�n�re 1 requ�te Insert (objet Transient)

		DAOGenerique.findAll(Poste.class);//G�n�re 1 requ�te select (from commande) + 1 requ�te select (enfant de la commande) 
		//+ 1 requ�te select (commandes de l'enfant) +1 requ�te select (jouet de la commande de l'enfant) + 1 requ�te select (jouet de la commande du d�but)
	}

	/**
	 * List<Poste> en LAZY dans Entreprise et Personne
	 * Entreprise et Personne en LAZY dans Poste
	 * Utilisation des proxy (Parcourt des collections et affichage des objets)
	 * 
	 */
	private static void exemple3(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)
		persister(e2);// G�n�re 1 requ�te Insert (objet transient)

		// Insertion d'un nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//G�n�re 1 requ�te Insert
		persister(j2);//G�n�re 1 requ�te Insert

		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c2);//G�n�re 1 requ�te Insert (objet Transient)

		EntityManager em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Entreprise> typedquery = em.createQuery("FROM " + Entreprise.class.getSimpleName(),Entreprise.class);
		List<Entreprise> enfants = typedquery.getResultList();//G�n�re 1 requ�te select (List<Poste> et  en mode LAZY)

		for (Entreprise etu : enfants){
			System.out.println(etu);
			System.out.println(etu.getPostes().size());//1 requete select (commandes)
			for (Poste co : etu.getPostes())
				System.out.println(co.getPersonne());//1 requ�te select (jouet)
		}
		em.close();
		em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Personne> typedquery2 = em.createQuery("FROM " + Personne.class.getSimpleName(),Personne.class);
		List<Personne> jouets = typedquery2.getResultList();//G�n�re 1 requ�te select (List<Poste> et en mode LAZY)
		for (Personne jou : jouets){
			System.out.println(jou);
			System.out.println(jou.getPostes().size());//1 requete select (commandes)
			for (Poste co : jou.getPostes())
				System.out.println(co.getPersonne());//Pas de requ�te: hibernate a le jouet dans son cache
		}
		em.close();
		em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Poste> typedquery3 = em.createQuery("FROM " + Poste.class.getSimpleName(),Poste.class);
		List<Poste> commandes = typedquery3.getResultList();//G�n�re 1 requ�te select (List<Poste> et  en mode LAZY)
		for (Poste co : commandes){
			System.out.println(co);//2 requetes (jouet et enfant) car la methode toString affiche l'enfant et le jouet de la commande
			System.out.println(co.getEntreprise());// deja charg� => rien
			System.out.println(co.getPersonne());// deja charg� => rien
			System.out.println(co.getEntreprise().getPostes().size());//1 select (commandes dans Entreprise)
			System.out.println(co.getPersonne().getPostes().size());//1 select (commandes dans Personne)
			for (Poste co1 : co.getEntreprise().getPostes())
				System.out.println(co1.getPersonne());//deja charg�
			for (Poste co2 : co.getPersonne().getPostes())
				System.out.println(co2.getEntreprise());//deja charg�

		}
	}

	/**
	 * List<Poste> en EAGER (mode sous-select) dans Entreprise et Personne
	 * Entreprise et Personne en EAGER (mode select) dans Poste
	 * 
	 */
	private static void exemple4(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)
		persister(e2);// G�n�re 1 requ�te Insert (objet transient)
		DAOGenerique.findAll(Entreprise.class);//G�n�re 1 requ�tes select(from enfant) + 1 requ�te select avec sous-select
		//(commandes des 2 enfants gr�ce � un IN)

		// Insertion d'un nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//G�n�re 1 requ�te Insert
		persister(j2);//G�n�re 1 requ�te Insert

		//Affichage des jouets
		DAOGenerique.findAll(Personne.class);//G�n�re 1 requ�te select (from jouet) + 1 requ�te select avec sous select 
		//(commandes des 2 jouets grace � un IN)

		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c2);//G�n�re 1 requ�te Insert (objet Transient)

		DAOGenerique.findAll(Poste.class);//G�n�re 1 requ�te select (from commande) + 1 requ�te select (enfant de la commande 1) 
		//+ 1 requ�te select (jouet de la commande 1) +1 requ�te select (enfant de la commande 2) + 1 requ�te select (jouet de la commande 2)
		//+ 1 requ�te select (commandes du jouet de la commande 1) + 1 requ�te select (commandes de l'enfant de la commande 1) 
		//+ 1 requ�te select (commandes du jouet de la commande 1) + 1 requ�te select (commandes de l'enfant de la commande 2)
	}

	/**
	 * List<Poste> en EAGER (mode join) dans Entreprise et Personne
	 * Entreprise et Personne en EAGER (mode join) dans Poste
	 * Bug sous hibernate => join mode + HQL 
	 */
	private static void exemple5(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom","adresse");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)
		persister(e2);// G�n�re 1 requ�te Insert (objet transient)
		DAOGenerique.find(Personne.class, e.getIdEntreprise()); // 1 requ�te select avec jointure entre l'enfant, les commandes et les jouets
		DAOGenerique.find(Personne.class, e2.getIdEntreprise()); // 1 requ�te select avec jointure entre l'enfant, les commandes et les jouets

		// Insertion d'un nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description");
		persister(j);//G�n�re 1 requ�te Insert
		persister(j2);//G�n�re 1 requ�te Insert

		//Affichage des jouets
		DAOGenerique.find(Personne.class, j.getIdPersonne());// 1 requ�te select avec jointure entre le jouet, les commandes et l'enfant
		DAOGenerique.find(Personne.class, j2.getIdPersonne());// 1 requ�te select avec jointure entre le jouet, les commandes et l'enfant
		//G�n�re 1 requ�te select (from jouet) + 1 requ�te select avec sous select (commandes des 2 jouets grace � un IN)
		//(List<Poste> en mode sous select EAGER)

		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c2);//G�n�re 1 requ�te Insert (objet Transient)
		DAOGenerique.find(Poste.class, c.getIdPoste());// 1 requ�te select de la commande 1 avec recup du jouet et enfant par jointure
		//+ 1 requ�te pour les commandes du jouet avec pour chaque commande l'enfant par jointure
		//+ 1 requ�te pour les commandes de l'enfant avec pour chaque commande le jouet
		DAOGenerique.find(Poste.class, c2.getIdPoste());// 1 requ�te select de la commande 2 avec recup du jouet et enfant par jointure
		//+ 1 requ�te pour les commandes du jouet avec pour chaque commande l'enfant par jointure
		//+ 1 requ�te pour les commandes de l'enfant avec pour chaque commande le jouet
	}

	private static void exempleNbEntreprisesParPersonne(){
		Calendar cal = Calendar.getInstance();
		cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom2","adresse2");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)
		persister(e2);// G�n�re 1 requ�te Insert (objet transient)

		// Insertion d'un nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description2");
		persister(j);//G�n�re 1 requ�te Insert
		persister(j2);//G�n�re 1 requ�te Insert

		//Creation des commandes
		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e);
		Poste c3 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c2);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c3);//G�n�re 1 requ�te Insert (objet Transient)

		/*System.out.println("Methode Java: Le nombre d'enfants a avoir commandé le jouet " + 
				j.getIdPersonne() + " est " + j.getNbEntreprisesParPersonneJava());
		System.out.println("Methode HQL: Le nombre d'enfants a avoir commandé le jouet " + 
				j.getIdPersonne() + " est " + j.getNbEntreprisesParPersonneHQL());
		System.out.println("Methode SQL: Le nombre d'enfants a avoir commandé le jouet " + 
				j.getIdPersonne() + " est " + j.getNbEntreprisesParPersonneSQL());*/

	}

	private static void exempleNbPostes(){
		Calendar cal = Calendar.getInstance();
		cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Entreprise e = new Entreprise("nom","adresse");
		Entreprise e2 = new Entreprise("nom2","adresse2");
		persister(e);// G�n�re 1 requ�te Insert (objet transient)
		persister(e2);// G�n�re 1 requ�te Insert (objet transient)

		// Insertion d'un nouveau jouet
		Personne j = new Personne("nom","description");
		Personne j2 = new Personne("nom2","description2");
		persister(j);//G�n�re 1 requ�te Insert
		persister(j2);//G�n�re 1 requ�te Insert

		//Creation des commandes
		Poste c = new Poste("poste_name",null,null,j2,e);
		Poste c2 = new Poste("ingenieur",null,null,j,e);
		Poste c3 = new Poste("ingenieur",null,null,j,e2);
		persister(c);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c2);//G�n�re 1 requ�te Insert (objet Transient)
		persister(c3);//G�n�re 1 requ�te Insert (objet Transient)

		/*System.out.println("Methode Java: Le nombre de commandes faites par l'enfant " + 
				e.getIdEntreprise() + " est " + e.getNbPosteJava());
		System.out.println("Methode HQL: Le nombre de commandes faites par l'enfant " + 
				e.getIdEntreprise() + " est " + e.getNbPosteHQL());
		System.out.println("Methode SQL: Le nombre de commandes faites par l'enfant " + 
				e.getIdEntreprise() + " est " + e.getNbPosteSQL());*/

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
