import java.sql.*;
import java.util.Scanner;
import java.io.*;

public class Sqlas {
	protected Connection con;

    public Sqlas(){
		Driveris();
		Prisijungimas();
        System.out.println("Sekmingai prisijungta prie Postgres Database");	
	}
	public void Driveris(){
		try {
		  //koreguotina eilute jei kitoks driveris
          Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException cnfe) {
          System.out.println("Neimanoma rasti driver class!");
          cnfe.printStackTrace();
          System.exit(1);
        }
	}

	public void Prisijungimas(){
		try {
			Console console = System.console();
		    if (console == null) {
		        System.out.println("Couldn't get Console instance");
		        System.exit(0);
		    }
			//vedant passworda consoleje, jis paslepiamas
		    char[] passwordArray = console.readPassword("Iveskite slaptazodi: ");
			String slaptazodis = new String(passwordArray);
			
			//koreguotina eilute priklausomai nuo driverio ir vartotojo vardo (slaptazodis ivedamas rankiniu budu)
			con = DriverManager.getConnection("jdbc:postgresql://pgsql3.mif/studentu", "vartotojovardas", slaptazodis) ;
        }
        catch (SQLException sqle) {
          System.out.println("Neimanoma prisiconnectint prie database! Ivestas neteisingas slaptazodis");
		  System.exit(1);
        }
	}

	public Connection getMyConnection(){
		return con;	
	}

	public void exit(Connection temp){
		System.out.println("Programa uzdaroma");
		try {
            temp.close(); 
        } catch (SQLException e) {
            System.out.println("VIDINĖ KLAIDA: Neįmanoma nutraukti ryšio su DB!");
        }
        System.exit(1);
    }
}