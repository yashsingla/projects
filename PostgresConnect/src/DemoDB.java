import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class DemoDB {
	
	public static void main(String args [])
	{
		try {
			Class.forName("org.postgresql.Driver");
			Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/cachedb","postgres","postgres");
			 
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");   
		    long millis=System.currentTimeMillis();  
		    //System.out.println(formatter.format(date));  
			
				PreparedStatement stat1;
				stat1= con.prepareStatement("insert into recency_list values(?,?,?)");
				stat1.setInt(1, 11);
				stat1.setInt(2, 11);
				//stat1.setDate(3,(date);
				int i=stat1.executeUpdate();
				System.out.println(i);
				stat1= con.prepareStatement("delete from recency_list");
                i=stat1.executeUpdate();
				System.out.println(i);
				
		}
		
		catch(Exception ex){
			
		}
		
	}

}