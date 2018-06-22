
import java.sql.Connection;
import java.sql.*;
import java.util.*;

public class algoClass_1 {
	
		
	public static void main(String args [])
	{
		//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,4,5,1,2,6,7,8,7,6,3,1));
				//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,4,5)); //fails for mru but efficient here
				List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,1,3,5,4,1,5,1,5,4)); // fails for mru but efficient here
				//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,1,2,3,4,5)); // same for lru and this algo but a lil efficient with mru
				//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,3,1,2,3,1,2,3,1,2,3,4,5,4,5));// fails for lfu..imp
				//List<Integer>values=new ArrayList<Integer>(Arrays.asList(1,2,1,3,1,4,1,5,2,3,4,5)); //same for lru and this algo but a lil efficient with mru		
		try {
			Class.forName("org.postgresql.Driver");
			Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/cachedb","postgres","postgres");
		cache obj= new cache(con);	
		
		obj.deleteDataFromCacheTables();
				
		for(Integer x:values)
		{
		obj.insert_cache(x,x);	
		}
		System.out.println(obj.getCacheMiss());
		
		} catch (Exception e) {
		System.out.println(e.getMessage());
		}
		
	}
}
