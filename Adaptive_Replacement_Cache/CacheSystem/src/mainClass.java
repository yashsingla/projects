import java.sql.Connection;
import java.sql.*;
import java.util.*;

public class mainClass {
	
		
	public static void main(String args [])
	{
		//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,4,5,1,2,6,7,8,7,6,3,1));
		//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,4,5)); 
		List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,1,3,5,4,1,5,1,5,4));
		//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,1,2,3,4,5)); 
		//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,1,2,3,1,2,3,1,2,3,4,5,4,5));
		//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,1,3,1,4,1,5,2,3,4,5)); 
		try {
			Class.forName("org.postgresql.Driver");
			Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/cachedb","postgres","postgres");
		cache obj= new cache(con);	
		
				
		for(Integer x:values)
		{
		obj.insert_cache(x,x);	
		}
		System.out.println(obj.getCacheMiss());
		
		} catch (Exception e) {
		System.out.println("main_message: "+e.getMessage());
		}
		
	}
}