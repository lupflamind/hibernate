
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import fr.hibernate.api.Connexion;
import fr.hibernate.dao.DAOCommande;
import fr.hibernate.dao.DAOEnfant;
import fr.hibernate.dao.DAOGenerique;
import fr.hibernate.dao.DAOJouet;
import fr.hibernate.metier.Commande;
import fr.hibernate.metier.Enfant;
import fr.hibernate.metier.Jouet;

public class Main {

	static public void main (String[] argv) {

		//exemple1();
		//exemple2();
		//exemple3();
		//exemple4();
		//exempleNbCommandes();
		exempleNbEnfantsParJouet();
		

	}

	/**
	 * List<Commande> en LAZY dans Enfant et Jouet
	 * Enfant et Jouet en EAGER dans Commande
	 */
	private static void exemple1(){
		Calendar cal = Calendar.getInstance();
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Enfant e = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		e.persister();// G�n�re 1 requ�te Insert (objet transient)

		e.setCode_postal("56420");
		e = e.update();//G�n�re 1 requ�te Select (merge) et 1 Update
		DAOGenerique.findAll(Enfant.class);//G�n�re 1 requ�te select (List<Commande> en mode Lazy)
		e.delete();//G�n�re une 1 requ�te Select (merge) et 1 Delete

		// Insertion d'un nouveau jouet
		Jouet j = new Jouet("nom","description");
		j.persister();//G�n�re 1 requ�te Insert (objet transient)

		//Modification du jouet
		j.setDescription("test update");
		j = j.update();//G�n�re 1 requ�te Select (merge) et 1 Update

		//Affichage des jouets
		DAOGenerique.findAll(Jouet.class);//G�n�re 1 requ�te select (List<Commande> en mode Lazy)

		Commande c = new Commande();
		c.setJouet(j);
		e = e.update();//G�n�re 1 requ�te select (merge) et 1 Insert
		c.setEnfant(e);
		c.persister();//G�n�re 1 requ�te Insert (objet transient)

		cal.set(2014, 12, 19, 05, 30);
		c.setDate_fin(cal.getTime());
		c = c.update();//G�n�re 1 requ�te Select (merge) et 1 Update
		DAOGenerique.findAll(Commande.class);//G�n�re 3 requ�tes select (Commande et Jouet en mode EAGER et sous select par d�faut)
	}


	/**
	 * List<Commande> en EAGER dans Enfant et Jouet
	 * Enfant et Jouet en LAZY dans Commande
	 * 
	 */
	private static void exemple2(){
		//Date
		Calendar cal = Calendar.getInstance();
		cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Enfant e = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		Enfant e2 = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		e.persister();// G�n�re 1 requ�te Insert (objet transient)
		e2.persister();// G�n�re 1 requ�te Insert (objet transient)
		DAOGenerique.findAll(Enfant.class);//G�n�re 1 requ�tes select(from enfant) + 2 requ�tes select (commandes des 2 enfants)
		//(List<Commande> en mode EAGER)

		// Insertion d'un nouveau jouet
		Jouet j = new Jouet("nom","description");
		Jouet j2 = new Jouet("nom2","description");
		j.persister();//G�n�re 1 requ�te Insert
		j2.persister();//G�n�re 1 requ�te Insert

		//Affichage des jouets
		DAOGenerique.findAll(Jouet.class);//G�n�re 1 requ�te select (from jouet) + 2 requ�tes select (commandes des 2 jouets) (List<Commande> en mode EAGER)

		Commande c = new Commande(e,j2);
		Commande c2 = new Commande(e2,j);
		c.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c2.persister();//G�n�re 1 requ�te Insert (objet Transient)

		DAOGenerique.findAll(Commande.class);//G�n�re 1 requ�te select (from commande) + 1 requ�te select (enfant de la commande) 
		//+ 1 requ�te select (commandes de l'enfant) +1 requ�te select (jouet de la commande de l'enfant) + 1 requ�te select (jouet de la commande du d�but)
	}

	/**
	 * List<Commande> en LAZY dans Enfant et Jouet
	 * Enfant et Jouet en LAZY dans Commande
	 * Utilisation des proxy (Parcourt des collections et affichage des objets)
	 * 
	 */
	private static void exemple3(){
		Calendar cal = Calendar.getInstance();cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Enfant e = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		Enfant e2 = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		e.persister();// G�n�re 1 requ�te Insert (objet transient)
		e2.persister();// G�n�re 1 requ�te Insert (objet transient)

		// Insertion d'un nouveau jouet
		Jouet j = new Jouet("nom","description");
		Jouet j2 = new Jouet("nom2","description");
		j.persister();//G�n�re 1 requ�te Insert
		j2.persister();//G�n�re 1 requ�te Insert

		Commande c = new Commande(e,j2);
		Commande c2 = new Commande(e2,j);
		c.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c2.persister();//G�n�re 1 requ�te Insert (objet Transient)

		EntityManager em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Enfant> typedquery = em.createQuery("FROM " + Enfant.class.getSimpleName(),Enfant.class);
		List<Enfant> enfants = typedquery.getResultList();//G�n�re 1 requ�te select (List<Commande> et  en mode LAZY)
		
		for (Enfant etu : enfants){
			System.out.println(etu);
			System.out.println(etu.getCommandes().size());//1 requete select (commandes)
			for (Commande co : etu.getCommandes())
				System.out.println(co.getJouet());//1 requ�te select (jouet)
		}
		em.close();
		em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Jouet> typedquery2 = em.createQuery("FROM " + Jouet.class.getSimpleName(),Jouet.class);
		List<Jouet> jouets = typedquery2.getResultList();//G�n�re 1 requ�te select (List<Commande> et en mode LAZY)
		for (Jouet jou : jouets){
			System.out.println(jou);
			System.out.println(jou.getCommandes().size());//1 requete select (commandes)
			for (Commande co : jou.getCommandes())
				System.out.println(co.getJouet());//Pas de requ�te hibernate a le jouet dans son cache
		}
		em.close();
		em = Connexion.getInstance().getEmf().createEntityManager();
		TypedQuery<Commande> typedquery3 = em.createQuery("FROM " + Commande.class.getSimpleName(),Commande.class);
		List<Commande> commandes = typedquery3.getResultList();//G�n�re 1 requ�te select (List<Commande> et  en mode LAZY)
		for (Commande co : commandes){
			System.out.println(co);//2 requetes (jouet et enfant) car la methode toString affiche l'enfant et le jouet de la commande
			System.out.println(co.getEnfant());// deja charg� => rien
			System.out.println(co.getJouet());// deja charg� => rien
			System.out.println(co.getEnfant().getCommandes().size());//1 select (commandes dans Enfant)
			System.out.println(co.getJouet().getCommandes().size());//1 select (commandes dans Jouet)
			for (Commande co1 : co.getEnfant().getCommandes())
				System.out.println(co1.getJouet());//deja charg�
			for (Commande co2 : co.getJouet().getCommandes())
				System.out.println(co2.getEnfant());//deja charg�
			
		}
	}

	/**
	 * List<Commande> en EAGER (join) dans Enfant et Jouet
	 * Enfant et Jouet en EAGER dans Commande
	 * 
	 */
	private static void exemple4(){
		//Date
		Calendar cal = Calendar.getInstance();
		cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Enfant e = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		Enfant e2 = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		e.persister();// G�n�re 1 requ�te Insert (objet transient)
		e2.persister();// G�n�re 1 requ�te Insert (objet transient)
		DAOGenerique.findAll(Enfant.class);//G�n�re 1 requ�te select (List<Commande> en mode EAGER)

		// Insertion d'un nouveau jouet
		Jouet j = new Jouet("nom","description");
		Jouet j2 = new Jouet("nom2","description");
		j.persister();//G�n�re 1 requ�te Insert
		j2.persister();//G�n�re 1 requ�te Insert

		//Affichage des jouets
		DAOGenerique.findAll(Jouet.class);//G�n�re 1 requ�te select (List<Commande> en mode Lazy)

		Commande c = new Commande(e,j2);
		Commande c2 = new Commande(e2,j);
		c.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c2.persister();//G�n�re 1 requ�te Insert (objet Transient)

		DAOGenerique.findAll(Commande.class);//G�n�re 3 requ�te select (Commande et Jouet en mode EAGER)
	}
	
	private static void exempleNbCommandes(){
		//Date
		Calendar cal = Calendar.getInstance();
		cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Enfant e = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		Enfant e2 = new Enfant("nom2","prenom2",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		e.persister();// G�n�re 1 requ�te Insert (objet transient)
		e2.persister();// G�n�re 1 requ�te Insert (objet transient)

		// Insertion d'un nouveau jouet
		Jouet j = new Jouet("nom","description");
		Jouet j2 = new Jouet("nom2","description2");
		j.persister();//G�n�re 1 requ�te Insert
		j2.persister();//G�n�re 1 requ�te Insert

		//Creation des commandes
		Commande c = new Commande(e,j2);
		Commande c2 = new Commande(e,j);
		Commande c3 = new Commande(e2,j);
		c.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c2.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c3.persister();//G�n�re 1 requ�te Insert (objet Transient)
		
		System.out.println("Methode Java: Le nombre de commandes faites par l'enfant " + 
				e.getIdEnfant() + " est " + e.getNbCommandeJava());
		System.out.println("Methode HQL: Le nombre de commandes faites par l'enfant " + 
				e.getIdEnfant() + " est " + e.getNbCommandeHQL());
		System.out.println("Methode SQL: Le nombre de commandes faites par l'enfant " + 
				e.getIdEnfant() + " est " + e.getNbCommandeSQL());

	}
	
	private static void exempleNbEnfantsParJouet(){
		//Date
		Calendar cal = Calendar.getInstance();
		cal.set(1991, 11, 11,0,0,0);
		//1 appel de m�thode = 1 entity manager => fermeture de l'entity � la fin de la m�thode
		Enfant e = new Enfant("nom","prenom",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		Enfant e2 = new Enfant("nom2","prenom2",cal.getTime(),"adresse","ville","26000","0295824102","test@test.fr");
		e.persister();// G�n�re 1 requ�te Insert (objet transient)
		e2.persister();// G�n�re 1 requ�te Insert (objet transient)

		// Insertion d'un nouveau jouet
		Jouet j = new Jouet("nom","description");
		Jouet j2 = new Jouet("nom2","description2");
		j.persister();//G�n�re 1 requ�te Insert
		j2.persister();//G�n�re 1 requ�te Insert

		//Creation des commandes
		Commande c = new Commande(e,j2);
		Commande c2 = new Commande(e,j);
		Commande c3 = new Commande(e2,j);
		c.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c2.persister();//G�n�re 1 requ�te Insert (objet Transient)
		c3.persister();//G�n�re 1 requ�te Insert (objet Transient)

		System.out.println("Methode Java: Le nombre d'enfants a avoir commandé le jouet " + 
				j.getIdJouet() + " est " + j.getNbEnfantsParJouetJava());
		System.out.println("Methode HQL: Le nombre d'enfants a avoir commandé le jouet " + 
				j.getIdJouet() + " est " + j.getNbEnfantsParJouetHQL());
		System.out.println("Methode SQL: Le nombre d'enfants a avoir commandé le jouet " + 
				j.getIdJouet() + " est " + j.getNbEnfantsParJouetSQL());

	}	
	private static void showJouets(){
		List<Jouet> jouets = DAOJouet.findAll();
		for (Jouet jou : jouets)
			System.out.println(jou);
	}

	private static void showEnfants(){
		List<Enfant> enfants = DAOEnfant.findAll();
		for (Enfant etu : enfants)
			System.out.println(etu);
	}

	private static void showCommandes(){
		List<Commande> commandes = DAOCommande.findAll();
		for (Commande co : commandes)
			System.out.println(co);
	}


}
