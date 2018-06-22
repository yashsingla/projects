import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseFunctions {

	Connection con;
	
	public DatabaseFunctions(Connection con) {
		this.con=con;
	}
	
	public void deletePageFromTableOnBasisOfKey(String tableName, int key) throws Exception {
		
		try {
			PreparedStatement stat1;
			stat1= con.prepareStatement("delete from "+tableName+" where key_in_kv_pair="+key);
		    stat1.executeUpdate();
			}
			
			catch(Exception e)
			{
				System.out.println("deletePageFromTableOnBasisOfKey_message: "+e.getMessage());
				throw new Exception(e);
			}
	}

	public void deletePageFromTable(String tableName) throws Exception
	{
		try {
			PreparedStatement stat1;
			stat1= con.prepareStatement("delete from "+tableName+" where date_added in( select date_added from "+tableName+" order by date_added limit 1)");
		    stat1.executeUpdate();
			}
			
		catch(Exception e)
		{
			System.out.println("deletePageFromTable_message: "+e.getMessage());
			throw new Exception(e);
		}
	   	
	}
	
	public void addPageInTable(String tableName,int key,int value) throws Exception
	{
		try {
			PreparedStatement stat1;
			long millis=System.currentTimeMillis();
			stat1= con.prepareStatement("insert into "+tableName+" values(?,?,?)");
			stat1.setInt(1, key);
			stat1.setInt(2, value);
			stat1.setLong(3, millis);
		    stat1.executeUpdate();
			}
			
		catch(Exception e)
		{
			System.out.println("addPageInTable_message: "+e.getMessage());
			throw new Exception(e);
		}
	   	
	}


	public void deleteDataFromCacheTables()  {
		
		try {
		PreparedStatement stat1;
		stat1= con.prepareStatement("delete from recency_list");
	    stat1.executeUpdate();
	    
	    stat1= con.prepareStatement("delete from recency_ghost_list");
	    stat1.executeUpdate();
	    
	    stat1= con.prepareStatement("delete from frequency_list");
	    stat1.executeUpdate();
	    
	    stat1= con.prepareStatement("delete from frequency_ghost_list");
	    stat1.executeUpdate();
		
		}
		
		catch(Exception e)
		{
			System.out.println("deleteDataFromCacheTables_message: "+e.getMessage());
		}
	}

}
