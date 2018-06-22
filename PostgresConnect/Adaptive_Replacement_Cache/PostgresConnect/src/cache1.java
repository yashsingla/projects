import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class cache1
	{
		private int size=4;
		private int cacheMiss=0;
		public int p=0; //tunable parameter
		public List<Integer>RecencyList=new LinkedList<Integer>();  //T1
		public List<Integer>RecencyGhostList=new LinkedList<Integer>();  //B1
		public List<Integer>FrequencyList=new LinkedList<Integer>();  //T2
		public List<Integer>FrequencyGhostList=new LinkedList<Integer>();  //B2
		public Map<Integer, Integer> keyVskeyCountMap = new HashMap<Integer, Integer>(); // key vs KeyCountMap
	   
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
		
		public void replace(int value)
		{
			if(getRecencyListSize()!=0 && (getRecencyListSize()>getTunableParameter()) ||
					FrequencyGhostList.contains(value) && getRecencyListSize()==getTunableParameter())
			{
				moveToMruInList(RecencyGhostList,RecencyList.get(RecencyList.size()-1));
				deleteLruPageInList(RecencyList);
				
				
			}
			
			else
				
			{
				moveToMruInList(FrequencyGhostList,FrequencyList.get(FrequencyList.size()-1));
				deleteLruPageInList(FrequencyList);
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
				keyVskeyCountMap.put(key, 1);
				IncrementCacheMiss();
				
				if(getRecencyListSize()+getRecencyGhostListSize()==getCacheSize())
				{//2
					if(getRecencyListSize()<getCacheSize())
					{
						keyVskeyCountMap.remove(RecencyGhostList.get(RecencyGhostList.size()-1));
						deleteLruPageInList(RecencyGhostList);
						replace(key);
					}
					else
					{  keyVskeyCountMap.remove(RecencyList.get(RecencyList.size()-1));
						deleteLruPageInList(RecencyList); //here RecencyGhostList is empty
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
						
						}
						replace(key);
					}
					
				}//2.1
				
				moveToMruInList(RecencyList,key);
				
			}//1.1
			
			else
			{//1.2
				if(RecencyList.contains(key)||FrequencyList.contains(key))  //case-I :cache hit
				{
					if(RecencyList.contains(key))
						RecencyList.remove(RecencyList.indexOf(key));
					
					if(FrequencyList.contains(key))
						FrequencyList.remove(FrequencyList.indexOf(key));
					
					moveToMruInList(FrequencyList,key);
					
				}
				else if(RecencyGhostList.contains(key))  //case II: cache miss
				{ 
					IncrementCacheMiss();
					updateTunableParameter(true);
					replace(key);
					RecencyGhostList.remove(RecencyGhostList.indexOf(key));
					moveToMruInList(FrequencyList,key);
					
				}
				else if(FrequencyGhostList.contains(key)) //cache III: cache miss
				{
					IncrementCacheMiss();
					updateTunableParameter(false);
					replace(key);
					FrequencyGhostList.remove(key);
					moveToMruInList(FrequencyList,key);
					
				}
				
				
			}//1.2
			
		}//1
		
	}
