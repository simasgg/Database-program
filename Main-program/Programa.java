import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class Programa{
	protected Connection con;
	protected BufferedReader in;
	protected PreparedStatement s;
	Sqlas sqlas = new Sqlas();
	
	public Programa(){
		this.in = new BufferedReader(new InputStreamReader(System.in)); 
		con = sqlas.getMyConnection();  
	}
	
	public void start(){
		showMenu();
		int choice;
		choice = inputInt("Iveskite skaiciu tarp 0 ir 9:");
		while(true){
			switch(choice){
				case 0: 
					sqlas.exit(con);
					break;
				case 1:
					System.out.println("1 <-- rodyti visus albumus");
					System.out.println("2 <-- rodyti visas dainas");
					System.out.println("3 <-- rodyti visus atlikejus");
					System.out.println("4 <-- rodyti visas irasu bendroves");
					System.out.println("5 <-- rodyti visu atlikeju visus albumus");
					int enter = inputInt("Iveskite skaiciu tarp 1 ir 4:");
					switch(enter){
						case 1:
							PrintData("Album");
							break;
						case 2:
							PrintData("Song");
							break;
						case 3:
							PrintData("Artist");
							break;
						case 4:
							PrintData("Label");
							break;
						case 5:
							PrintData("Sukure");
							break;
						default:
							System.out.println("Klaida ivedant skaiciu");
							break;
					}
					break;
				case 2:
					addLabel();
					break;
				case 3:
					changeYearsActive();
					break;
				case 4:
					removeArtist();
					break;
				case 5:
					AlbumToArtist();
					break;
				case 6:
					changeGenre();
					break;
				case 7:
					removeAlbum();
					break;
				case 8:
					EditSongInfo();
					break;
				case 9:
					showMenu();
					break;
				default:
					System.out.println("Klaida ivedant skaiciu");
					break;
			}
			choice = inputInt("Iveskite skaiciu tarp 0 ir 9 (meniu punktui rodyti spausti 9):");
		}		
    }
	
	public void showMenu(){
		System.out.println("----------------------------------------------------");
		System.out.println("0 <-- Uzbagti darba");
		System.out.println("1 <-- Spausdinti turimus duomenis (pasirinktinai)");
		System.out.println("2 <-- Prideti irasu bendrove");
		System.out.println("3 <-- Pakeisti irasu bendroves veiklos metu pabaiga");
		System.out.println("4 <-- Pasalinti atlikeja");
		System.out.println("5 <-- Sukeisti 2 atlikeju albumus");
		System.out.println("6 <-- Pakeisti albumo zanra");
		System.out.println("7 <-- Pasalinti albuma");
		System.out.println("8 <-- Pakoreguoti/pakeisti daina");
		System.out.println("9 <-- Rodyti meniu pasirinkimus");
		System.out.println("----------------------------------------------------");
	}
	
	public void PrintData(String myTable){
		try
        {
            Statement statement = con.createStatement();
			System.out.print("\033\143");
            ResultSet rs = statement.executeQuery("SELECT * FROM siga5828."+ myTable);
			if(myTable == "Album"){
				System.out.println("id_album  trukme  zanras	    pavadinimas 	  	 metai");
				while (rs.next()) {
		        	int id = rs.getInt("id_album");
		        	String ln = rs.getString("trukme");
		        	String genre = rs.getString("zanras");
					String name = rs.getString("pavadinimas");
					String year = rs.getString("metai");
		        	System.out.println(id+"   "+ln+"    "+genre+"   "+name+"    "+year);
		     	}
			}else if(myTable == "Artist"){
				System.out.println("id_artist  pavadinimas   	 salis	   veiklos_pradzia veiklos_pabaiga");
				while (rs.next()) {
		        	int id = rs.getInt("id_artist");
		        	String name = rs.getString("pavadinimas");
		        	String country = rs.getString("salis");
					String year1 = rs.getString("veiklos_metai_pradzia");
					String year2 = rs.getString("veiklos_metai_pabaiga");
		        	System.out.println(id+"   "+name+"    "+country+"   "+year1+"    "+year2);
		     	}
			}else if(myTable == "Label"){
				System.out.println("id_label  pavadinimas      veiklos_pradzia veiklos_pabaiga");
				while (rs.next()) {
		        	int id = rs.getInt("id_label");
		        	String name = rs.getString("pavadinimas");
					String year1 = rs.getString("veiklos_metai_pradzia");
					String year2 = rs.getString("veiklos_metai_pabaiga");
		        	System.out.println(id+"   "+name+"    "+year1+"    "+year2);
		     	}
			}else if(myTable == "Song"){
				System.out.println("id_song  trukme    pavadinimas  					 BPM");
				while (rs.next()) {
		        	int id = rs.getInt("id_song");
		        	String ln = rs.getString("trukme");
					String name = rs.getString("pavadinimas");
					int bpm = rs.getInt("BPM");
		        	System.out.println(id+"   "+ln+"    "+name+"    "+bpm);
		     	}
			}else if(myTable == "Sukure"){
				ResultSet rs2 = statement.executeQuery("SELECT B.id_artist, A.id_album, A.pavadinimas AS albumas, B.pavadinimas AS atlikejas FROM Album A, Artist B, Sukure C WHERE A.id_album = C.id_album AND B.id_artist = C.id_artist");
				System.out.println("id_artist  id_album     artist_name      	    album_name");
				while (rs2.next()) {
		        	int id_artist = rs2.getInt("id_artist");
					int id_album = rs2.getInt("id_album");
					String artist_name = rs2.getString("atlikejas");
					String album_name = rs2.getString("albumas");
		        	System.out.println(id_artist+"          "+id_album+"          "+artist_name+"   "+album_name);
		     	}
			}

			try {
                if(statement != null) statement.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }
        }
        catch (SQLException e)
        {
            System.out.println("SPAUSDINIMO KLAIDA: " + e);
        } 
	}
	
	public void addLabel(){
		try
        {
			System.out.print("\033\143");
			PrintData("Label");
			int nr = inputInt("Irasu bendroves ID: ");
			String name = inputString("Pavadinimas: ");
			String v_m_pradz = inputString("Veiklos pradzia: ");
			String v_m_pab = inputString("Veiklos pabaiga: ");
			
            s = con.prepareStatement("INSERT INTO siga5828.Label VALUES(?, ?, ?, ?)");
			s.setInt(1, nr);
			s.setString(2, name);
			s.setString(3, v_m_pradz);
			s.setString(4, v_m_pab);

			System.out.println("Irasu bendrove sekminai prideta");
			int af = s.executeUpdate();
			
			try {
                if(s != null) s.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }
        }
        catch (SQLException e)
        {
            System.out.println("IVEDIMO KLAIDA: " + e);
        }
	}
	
	public void changeYearsActive(){
        try
        {	
			System.out.print("\033\143");
			PrintData("Label");
			int nr = inputInt("Irasu bendroves ID: ");
			String v_m_pab = inputString("Veiklos pabaiga: ");

			s = con.prepareStatement("UPDATE siga5828.Label SET veiklos_metai_pabaiga = ? WHERE id_label = ?");
			s.setString(1, v_m_pab);
			s.setInt(2, nr);

			System.out.println("Metai pakeisti");
			int af = s.executeUpdate();
            
            if (s.getUpdateCount() == 0)
            {
                System.out.println("Irasu bendrove su tokiu ID neegzistuoja!");
				return;
            }
			try {
                if(s != null) s.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }
        }
        catch (SQLException e)
        {
            System.out.println("ATNAUJINIMO KLAIDA: " + e);
        }
	}
	
	public void removeArtist(){	
        try
        {
			System.out.print("\033\143");
			PrintData("Artist");
			int nr = inputInt("Atlikejo ID: ");
            
			s = con.prepareStatement("DELETE FROM siga5828.Artist WHERE id_artist = ?");
			s.setInt(1, nr);

			int af = s.executeUpdate();            
			System.out.println("Atlikejas pasalintas");

            if (s.getUpdateCount() == 0)
            {
                System.out.println("Atlikejas su tokiu ID neegzistuoja!");
				return;
            }
			try {
                if(s != null) s.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }
        }
        catch (SQLException e)
        {
            System.out.println("TRYNIMO KLAIDA: " + e);
        } 
	}
	
	public void AlbumToArtist(){
			Statement statement=null;
			System.out.print("\033\143");
			PrintData("Sukure");

			int artist_pirmas = inputInt("Pirmo Atlikejo ID: ");
			int album_pirmas = inputInt("Pirmo Albumo ID: ");
			int artist_antras = inputInt("Antro Atlikejo ID: ");
			int album_antras = inputInt("Antro Albumo ID: ");

        	String query1 = "UPDATE siga5828.Sukure SET id_artist = '" + artist_antras + "' WHERE id_album = '" + album_pirmas + "'";
			String query2 = "UPDATE siga5828.Sukure SET id_artist = '" + artist_pirmas + "' WHERE id_album = '" + album_antras + "'";
			Statement stmt=null;
			try {
		        con.setAutoCommit(false);
		        stmt = con.createStatement();
		        stmt.executeUpdate(query1);
				stmt.executeUpdate(query2);
		        con.commit();
		        con.setAutoCommit(true);
				System.out.println("Atnaujinimas atliktas");
		    } catch (SQLException e) {
		        try {
		            con.rollback();
		            con.setAutoCommit(true);
		            throw e;
		        } catch (SQLException e1) {
		            System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida rollback!");
		     	}
		    }
		try {
                if(stmt != null) 
					stmt.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }
	}
	
	public void changeGenre(){
		try
        {	
			System.out.print("\033\143");
			PrintData("Album");

			int nr = inputInt("Albumo ID: ");
			String genre = inputString("Zanras: ");

            s = con.prepareStatement("UPDATE siga5828.Album SET zanras = ? WHERE id_album = ?");
			s.setString(1, genre);
			s.setInt(2, nr);

			int af = s.executeUpdate();
			System.out.println("Zanras pakeistas");

            if (s.getUpdateCount() == 0)
            {
                System.out.println("Albumas su tokiu ID neegzistuoja!");
				return;
            }
			try {
                if(s != null) s.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }
        }
        catch (SQLException e)
        {
            System.out.println("ATNAUJINIMO KLAIDA: " + e);
        }
	}
	
	public void removeAlbum(){
		try
        {
			System.out.print("\033\143");
			PrintData("Album");
			int nr = inputInt("Albumo ID: ");
            
			s = con.prepareStatement("DELETE FROM siga5828.Album WHERE id_album = ?");
			s.setInt(1, nr);

			int af = s.executeUpdate();            
			System.out.println("Albumas pasalintas");
            
            if (s.getUpdateCount() == 0)
            {
                System.out.println("Albumo su tokiu ID neegzistuoja!");
				return;
            }	
			try {
                if(s != null) s.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }		
        }
        catch (SQLException e)
        {
            System.out.println("TRYNIMO KLAIDA: " + e);
        } 
	}
	
	public void EditSongInfo(){
		try
        {	
			System.out.print("\033\143");
			PrintData("Song");
			int nr = inputInt("ID dainos kuria norite pakoreguoti: ");
			String length = inputString("Daino trukme(hh:mm:ss formatu): ");
			String name = inputString("Pavadinimas: ");
			int bpm = inputInt("BPM: ");

			s = con.prepareStatement("UPDATE siga5828.Song SET trukme = ?, pavadinimas = ?, bpm = ? WHERE id_song = ?");
			s.setString(1, length);
			s.setString(2, name);
			s.setInt(3, bpm);
			s.setInt(4, nr);

			int af = s.executeUpdate();            
			System.out.println("Dainos info pakoreguota");
            
            if (s.getUpdateCount() == 0)
            {
                System.out.println("Daina su tokiu ID neegzistuoja!");
				return;
            }
			try {
                if(s != null) s.close();
            } catch (SQLException e) {
                System.out.println("VIDINĖ KLAIDA: Nenumatyta SQL klaida!");
            }

        }
        catch (SQLException e)
        {
            System.out.println("ATNAUJINIMO KLAIDA: " + e);
        }
	}
	
	private String inputString(String request)
    {
        System.out.print(request);
        
        String input = "";
        
        try
        {
            input = in.readLine();
        }
        catch (IOException e)
        {
            System.out.println("SISTEMOS KLAIDA: " + e);
        }
        
        return input;
    }
	
	private int inputInt(String request)
    {
        System.out.print(request);
        
        String input = "";
        int ret = 0;
        
        try
        {
            input = in.readLine();
            ret = Integer.parseInt(input);
        }
        catch (IOException e)
        {
            System.out.println("SISTEMOS KLAIDA: " + e);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Butina ivesti sveikaji skaiciu!");
        }
        
        return ret;
    }
	
}