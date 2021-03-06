import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class cache
	{
	    // L1=T1+B1,  L1=T2+B2, where T1+T2 represents actual cache memory
	    private Connection con;
	 	private DatabaseFunctions dbObject;
	    private int size=4;
		private int cacheMiss=0;
		private int cacheHit=0;
		private int p=0; //tunable parameter
		private List<Integer>RecencyList=new LinkedList<Integer>();  //T1
		private List<Integer>RecencyGhostList=new LinkedList<Integer>();  //B1
		private List<Integer>FrequencyList=new LinkedList<Integer>();  //T2
		private List<Integer>FrequencyGhostList=new LinkedList<Integer>();  //B2
		private Map<Integer, Integer> keyVsValueMap = new HashMap<Integer, Integer>(); // key vs KeyCountMap
		private static final String RECENCY_LIST="recency_list";
		private static final String RECENCY_GHOST_LIST="recency_ghost_list";
		private static final String FREQUENCY_LIST="frequency_list";
		private static final String FREQUENCY_GHOST_LIST="frequency_ghost_list";
		   
		   
		
		
		public cache(Connection con) {
			this.con=con;
			this.dbObject=new DatabaseFunctions(con);
			dbObject.deleteDataFromCacheTables();
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
		
		public int getCacheHit()
		{
			return this.cacheHit;
		}
		
		public void setCacheHit(int cacheMiss)
		{
			 this.cacheHit=cacheHit;
		}
		
		public void IncrementCacheHit()
		{
			this.cacheHit++;
		}
		
		public void deleteLruPageInList(List<Integer>list) throws Exception
		{
			try {
			list.remove(list.size()-1);
			}catch(Exception e)
			{
				System.out.println("deleteLruPageInList: "+e.getMessage());
				throw new Exception(e);
			}
		}
		public void moveToMruInList(List<Integer>list,int value) throws Exception
		{
			try {
				list.add(0, value);
				}catch(Exception e)
				{
					System.out.println("deleteLruPageInList: "+e.getMessage());
					throw new Exception(e);
				}
		}
		
		public void replace(int key,int value) throws Exception
		{
			int localKey,localValue;
			try {
			if(getRecencyListSize()!=0 && (getRecencyListSize()>getTunableParameter()) ||
					FrequencyGhostList.contains(key) && getRecencyListSize()==getTunableParameter())
			{
				 localKey=RecencyList.get(RecencyList.size()-1);
				 localValue=keyVsValueMap.get(localKey);
				moveToMruInList(RecencyGhostList,localKey);
				dbObject.addPageInTable(RECENCY_GHOST_LIST, localKey,localValue);
				deleteLruPageInList(RecencyList);
				dbObject.deletePageFromTable(RECENCY_LIST);							
			}
			
			else
				
			{
				localKey=FrequencyList.get(FrequencyList.size()-1);
				 localValue=keyVsValueMap.get(localKey);
				moveToMruInList(FrequencyGhostList,localKey);
				dbObject.addPageInTable(FREQUENCY_GHOST_LIST, localKey,localValue);
				deleteLruPageInList(FrequencyList);
				dbObject.deletePageFromTable(FREQUENCY_LIST);
			}
		}catch(Exception e)
			{
			System.out.println("replace: "+e.getMessage());
			throw new Exception(e);
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
		
		public void updateTunableParameter(boolean b) throws Exception
		{
			int updatedTunableParameter;
	         try {
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
	         } catch (Exception e) {
	        	 System.out.println("updateTunableParameter_message: "+e.getMessage());
					throw new Exception(e);
			}
		}
		
		public void insert_cache(int key,int value) throws Exception
		{ //1
			try {
			if(!keyVsValueMap.containsKey(key))   //case-IV cache miss
			{//1.1
				keyVsValueMap.put(key, value);
				IncrementCacheMiss();
				
				if(getRecencyListSize()+getRecencyGhostListSize()==getCacheSize())
				{//2
					if(getRecencyListSize()<getCacheSize())
					{
						keyVsValueMap.remove(RecencyGhostList.get(RecencyGhostList.size()-1));
						deleteLruPageInList(RecencyGhostList);
						dbObject.deletePageFromTable(RECENCY_GHOST_LIST);
						replace(key,value);
					}
					else
					{  keyVsValueMap.remove(RecencyList.get(RecencyList.size()-1));
						deleteLruPageInList(RecencyList); //here RecencyGhostList is empty
						dbObject.deletePageFromTable(RECENCY_LIST);
					}
					
				}//2
				else
				if(getRecencyListSize()+getRecencyGhostListSize()<getCacheSize())
				{//2.1
					int totalSize=getRecencyListSize()+getRecencyGhostListSize()+getFrequencyListSize()+getFrequencyGhostListSize();
					if(totalSize>=getCacheSize())
					{
						if(totalSize==2*getCacheSize()){
							
						keyVsValueMap.remove(FrequencyGhostList.get(FrequencyGhostList.size()-1));
						deleteLruPageInList(FrequencyGhostList);
						dbObject.deletePageFromTable(FREQUENCY_GHOST_LIST);
						}
						replace(key,value);
					}
					
				}//2.1
				
				moveToMruInList(RecencyList,key);
				dbObject.addPageInTable(RECENCY_LIST, key,value);
				
			}//1.1
			
			else
			{//1.2
				if(RecencyList.contains(key)||FrequencyList.contains(key))  //case-I :cache hit
				{  IncrementCacheHit();
					
					if(RecencyList.contains(key)) {
						RecencyList.remove(RecencyList.indexOf(key));
						dbObject.deletePageFromTableOnBasisOfKey(RECENCY_LIST,key);
					}
					
					if(FrequencyList.contains(key)) {
						FrequencyList.remove(FrequencyList.indexOf(key));
						dbObject.deletePageFromTableOnBasisOfKey(FREQUENCY_LIST,key);
					}
						
					moveToMruInList(FrequencyList,key);
					dbObject.addPageInTable(FREQUENCY_LIST, key,value);
				}
				else if(RecencyGhostList.contains(key))  //case II: cache miss
				{ 
					IncrementCacheMiss();
					updateTunableParameter(true);
					replace(key,value);
					RecencyGhostList.remove(RecencyGhostList.indexOf(key));
					dbObject.deletePageFromTableOnBasisOfKey(RECENCY_GHOST_LIST,key);
					moveToMruInList(FrequencyList,key);
					dbObject.addPageInTable(FREQUENCY_LIST, key, value);
					
				}
				else if(FrequencyGhostList.contains(key)) //cache III: cache miss
				{
					IncrementCacheMiss();
					updateTunableParameter(false);
					replace(key,value);
					FrequencyGhostList.remove(key);
					dbObject.deletePageFromTableOnBasisOfKey(FREQUENCY_GHOST_LIST,key);
					moveToMruInList(FrequencyList,key);		
					dbObject.addPageInTable(FREQUENCY_LIST, key, value);
				}
					
			}//1.2
		}catch (Exception e) {
			System.out.println("insert_cache_message: "+e.getMessage());
			throw new Exception(e);
		}
			
	}//1	
		
	}