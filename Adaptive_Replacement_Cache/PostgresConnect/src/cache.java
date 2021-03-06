import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class cache
	{
	    Connection con;
	 	private int size=4;
		private int cacheMiss=0;
		public int p=0; //tunable parameter
		public List<Integer>RecencyList=new LinkedList<Integer>();  //T1
		public List<Integer>RecencyGhostList=new LinkedList<Integer>();  //B1
		public List<Integer>FrequencyList=new LinkedList<Integer>();  //T2
		public List<Integer>FrequencyGhostList=new LinkedList<Integer>();  //B2
		public Map<Integer, Integer> keyVskeyCountMap = new HashMap<Integer, Integer>(); // key vs KeyCountMap
		private static final String RECENCY_LIST="recency_list";
		private static final String RECENCY_GHOST_LIST="recency_ghost_list";
		private static final String FREQUENCY_LIST="frequency_list";
		private static final String FREQUENCY_GHOST_LIST="frequency_ghost_list";
		   
		   
		
		
		public cache(Connection con) {
			this.con=con;
		}

		public Connection getConnection()
		{
			return this.con;
		}
		
		public void setConnection(Connection con)
		{
			this.con=con;
		}
		
		public int getCacheSize()
		{
			return this.size;
		}
		
		public void setCacheSize(int size)
		{
			this.size=size;
		}
		
		public int getRecencyListSize()
		{
			return this.RecencyList.size();
		}
		
		public int getFrequencyListSize()
		{
			return this.FrequencyList.size();
		}
		
		public int getFrequencyGhostListSize()
		{
			return this.FrequencyGhostList.size();
		}
		
		public int getRecencyGhostListSize()
		{
			return this.RecencyGhostList.size();
		}
		
		
		public int getValue(int key)
		{
			return key;
			
		}
		
		public int getTunableParameter()
		{
			return this.p;
			
		}
		
		public int setTunableParameter(int p)
		{
			return this.p=p;
			
		}
		
		public int getCacheMiss()
		{
			return this.cacheMiss;
		}
		
		public void setCacheMiss(int cacheMiss)
		{
			 this.cacheMiss=cacheMiss;
		}
		
		public void IncrementCacheMiss()
		{
			this.cacheMiss++;
		}
		
		public void deleteLruPageInList(List<Integer>list)
		{
			list.remove(list.size()-1);
		}
		public void moveToMruInList(List<Integer>list,int value)
		{
			list.add(0, value);
		}
		
		public void replace(int key,int value)
		{
			int localKey,localValue;
			
			if(getRecencyListSize()!=0 && (getRecencyListSize()>getTunableParameter()) ||
					FrequencyGhostList.contains(key) && getRecencyListSize()==getTunableParameter())
			{
				 localKey=RecencyList.get(RecencyList.size()-1);
				 localValue=keyVskeyCountMap.get(localKey);
				moveToMruInList(RecencyGhostList,localKey);
				addPageInTable(RECENCY_GHOST_LIST, localKey,localValue);
				deleteLruPageInList(RecencyList);
				deletePageFromTable(RECENCY_LIST);							
			}
			
			else
				
			{
				localKey=FrequencyList.get(FrequencyList.size()-1);
				 localValue=keyVskeyCountMap.get(localKey);
				moveToMruInList(FrequencyGhostList,localKey);
				addPageInTable(FREQUENCY_GHOST_LIST, localKey,localValue);
				deleteLruPageInList(FrequencyList);
				deletePageFromTable(FREQUENCY_LIST);
			}
		}
		
		public int maximum(int x,int y)
		{
			if(x>=y)
				return x;
			else
				return y;
		}
		
		public int minimum(int x,int y)
		{
			if(x<=y)
				return x;
			else
				return y;
		}
		
		public void updateTunableParameter(boolean b)
		{
			int updatedTunableParameter;
			if(b)
			{
				if(getRecencyGhostListSize()>=getFrequencyGhostListSize())
				{
					updatedTunableParameter=minimum(getTunableParameter()+1,getCacheSize());
					
				}
				else{
					int x=getFrequencyGhostListSize()/getRecencyGhostListSize();
					updatedTunableParameter=minimum(getTunableParameter()+x,getCacheSize());
				}
				
			}
			else
			{
				if(getFrequencyGhostListSize()>=getRecencyGhostListSize())
				{
					updatedTunableParameter=maximum(getTunableParameter()-1,0);
					
				}
				else{
					int x=getRecencyGhostListSize()/getFrequencyGhostListSize();
					updatedTunableParameter=maximum(getTunableParameter()-x,0);
				}				
			}
			setTunableParameter(updatedTunableParameter);
		}
		
		public void insert_cache(int key,int value)
		{ //1
			if(!keyVskeyCountMap.containsKey(key))   //case-IV cache miss
			{//1.1
				keyVskeyCountMap.put(key, value);
				IncrementCacheMiss();
				
				if(getRecencyListSize()+getRecencyGhostListSize()==getCacheSize())
				{//2
					if(getRecencyListSize()<getCacheSize())
					{
						keyVskeyCountMap.remove(RecencyGhostList.get(RecencyGhostList.size()-1));
						deleteLruPageInList(RecencyGhostList);
						deletePageFromTable(RECENCY_GHOST_LIST);
						replace(key,value);
					}
					else
					{  keyVskeyCountMap.remove(RecencyList.get(RecencyList.size()-1));
						deleteLruPageInList(RecencyList); //here RecencyGhostList is empty
						deletePageFromTable(RECENCY_LIST);
					}
					
				}//2
				else
				if(getRecencyListSize()+getRecencyGhostListSize()<getCacheSize())
				{//2.1
					int totalSize=getRecencyListSize()+getRecencyGhostListSize()+getFrequencyListSize()+getFrequencyGhostListSize();
					if(totalSize>=getCacheSize())
					{
						if(totalSize==2*getCacheSize()){
							
						keyVskeyCountMap.remove(FrequencyGhostList.get(FrequencyGhostList.size()-1));
						deleteLruPageInList(FrequencyGhostList);
						deletePageFromTable(FREQUENCY_GHOST_LIST);
						}
						replace(key,value);
					}
					
				}//2.1
				
				moveToMruInList(RecencyList,key);
				addPageInTable(RECENCY_LIST, key,value);
				
			}//1.1
			
			else
			{//1.2
				if(RecencyList.contains(key)||FrequencyList.contains(key))  //case-I :cache hit
				{
					if(RecencyList.contains(key)) {
						RecencyList.remove(RecencyList.indexOf(key));
						deletePageFromTableOnBasisOfKey(RECENCY_LIST,key);
					}
					
					if(FrequencyList.contains(key)) {
						FrequencyList.remove(FrequencyList.indexOf(key));
					deletePageFromTableOnBasisOfKey(FREQUENCY_LIST,key);
					}
						
					moveToMruInList(FrequencyList,key);
					addPageInTable(FREQUENCY_LIST, key,value);
				}
				else if(RecencyGhostList.contains(key))  //case II: cache miss
				{ 
					IncrementCacheMiss();
					updateTunableParameter(true);
					replace(key,value);
					RecencyGhostList.remove(RecencyGhostList.indexOf(key));
					deletePageFromTableOnBasisOfKey(RECENCY_GHOST_LIST,key);
					moveToMruInList(FrequencyList,key);
					addPageInTable(FREQUENCY_LIST, key, value);
					
				}
				else if(FrequencyGhostList.contains(key)) //cache III: cache miss
				{
					IncrementCacheMiss();
					updateTunableParameter(false);
					replace(key,value);
					FrequencyGhostList.remove(key);
					deletePageFromTableOnBasisOfKey(FREQUENCY_GHOST_LIST,key);
					moveToMruInList(FrequencyList,key);		
					addPageInTable(FREQUENCY_LIST, key, value);
				}
					
			}//1.2
			
		}//1	
		
		private void deletePageFromTableOnBasisOfKey(String tableName, int key) {
			
			try {
				PreparedStatement stat1;
				stat1= con.prepareStatement("delete from "+tableName+" where key_in_kv_pair="+key);
			    stat1.executeUpdate();
				}
				
				catch(Exception ex)
				{
					System.out.println("message: "+ex.getMessage());
				}
		}

		public void deletePageFromTable(String tableName)
		{
			try {
				PreparedStatement stat1;
				stat1= con.prepareStatement("delete from "+tableName+" where date_added in( select date_added from "+tableName+" order by date_added limit 1)");
			    stat1.executeUpdate();
				}
				
				catch(Exception ex)
				{
					System.out.println("message: "+ex.getMessage());
				}
		   	
		}
		
		public void addPageInTable(String tableName,int key,int value)
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
				
				catch(Exception ex)
				{
					System.out.println("message: "+ex.getMessage());
				}
		   	
		}

		public void deleteDataFromCacheTables() {
		
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
			
			catch(Exception ex)
			{
				System.out.println("message: "+ex.getMessage());
			}
		}
	}