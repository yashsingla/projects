import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class practice {

	Connection con;
	
	public practice(Connection con){
		this.con=con;
	}
	
	public void enterInDB()
	{
		try {	
		PreparedStatement stat1= con.prepareStatement("insert into emp values(?,?,?)");
		stat1.setInt(1,4);
		stat1.setString(2,"pre-sales");
		stat1.setInt(3,789);
		int i=stat1.executeUpdate();
		System.out.println("i: "+i);
		PreparedStatement stat= con.prepareStatement("select * from emp");
		ResultSet rs=stat.executeQuery();
		
		
		while(rs.next())
		{
			System.out.println(rs.getInt(3));
		}
	}

		catch(Exception ex)
		{
			
		}
	}
}
