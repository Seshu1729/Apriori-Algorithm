import java.io.*;
import java.util.*;


public class Apriori 
{   
	
    public List<Set<String>> get_content()
    {
        List<Set<String>> content = new ArrayList<Set<String>>();
        try 
        {
			File file = new File("/D:/Eclipse/workspace/Apriori/src/assert/transation_details.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while ((line = bufferedReader.readLine()) != null) 
			{
				Set<String> sub_content = new HashSet<String>();
		        for (String retval: line.toString().split(","))
		        {
		        	sub_content.add(retval);
		         }
		        content.add(sub_content);
			}
	        bufferedReader.close();
	        fileReader.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Some IOException...check transation_details.txt file");
			System.exit(0);
		}
        return content;
    }
    
    public List<Float> get_additional_info()
    {
    	List<Float> content = new ArrayList<Float>();
        try 
        {
			File file = new File("/D:/Eclipse/workspace/Apriori/src/assert/additional_info.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			
			while ((line = bufferedReader.readLine()) != null)
		        content.add(Float.parseFloat(line));
	        bufferedReader.close();
	        fileReader.close();
		} 
		catch (IOException e) 
		{
			System.out.println("Some IOException...check additional_info.txt file");
			System.exit(0);
		}
        return content;
    }
    
    public int get_count(List<Set<String>> content,Set<String> item)
    {
    	int count = 0;
    	for(int index=0;index<content.size();index++)
    	{
    		if(content.get(index).containsAll(item))
    			count++;
    	}
		return count;
    	
    }
    
    public List<Set<String>> convect_set_string_to_list_set_string(Set<String> input)
    {
    	List<Set<String>> result = new ArrayList<Set<String>>();
    	for(String item : input)
    	{
    		Set<String> temparary = new HashSet<String>();
    		temparary.add(item);
    		result.add(temparary);
    	}
    	return result;
    }
    
    public List<Set<String>> Combinations(List<Set<String>> input,int k)
	{
		List<Set<String>> subsets = new ArrayList<Set<String>>();
		int[] s = new int[k];              
		Apriori apriori = new Apriori();
		                                  

		if (k <= input.size()) 
		{
		    for (int i= 0; (s[i] = i) < k - 1; i++);  
		    	subsets.add(apriori.getSubset(input, s));
		    for(;;) 
		    {
		        int i;
		        for (i=k-1; i>=0 && s[i]==input.size()-k+i ;i--);
		        if (i < 0) 
		            break;
		        else 
		        {
		            s[i]++;                   
		            for (++i; i < k; i++) 
		                s[i] = s[i-1] + 1; 
		            subsets.add(apriori.getSubset(input, s));
		        }
		    }
		}
		return subsets;
	}
    
    public Set<String> getSubset(List<Set<String>> input, int[] subset) 
	{
	    Set<String> result = new HashSet<String>();
	    for (int i = 0; i < subset.length; i++) 
	        result.addAll(input.get(subset[i]));
	    return result;
	}
    
    public Set<Set<String>> get_generate_candidates(Set<Set<String>> item_set,Set<Set<String>> final_set,int k_index)
    {
    	Set<Set<String>> next_item_set = new HashSet<Set<String>>();
    	List<Set<String>> item_set_as_list = new ArrayList<Set<String>>();
    	item_set_as_list.addAll(item_set);
    	
    	for(Set<String> item : Combinations(item_set_as_list,2))
    	{
    		if(item.size()==k_index+1)
    		{
    			boolean is_valid = true;
	    		List<Set<String>> item_as_list = convect_set_string_to_list_set_string(item);
	    		for(Set<String> subset : Combinations(item_as_list,k_index))
	    		{
	    			if(final_set.containsAll(subset))
	    			{
	    				is_valid = false;
	    				break;
	    			}
	    		}
	    		if(is_valid)
	    			next_item_set.add(item);
    		}	
    	}
    	return next_item_set;
    }
    
	public Set<Set<String>> get_frequent_item_set(List<Set<String>> content,Set<Set<String>> item_set,Float MST)
    {
		Apriori apriori = new Apriori();
		Set<Set<String>> final_set = new HashSet<Set<String>>();
		Set<Set<String>> valid_item_set = new HashSet<Set<String>>();
		Set<Set<String>> temp_valid_item_set = new HashSet<Set<String>>();
		int k_index = 1;

    	while (item_set.size()!=0)
    	{
    		valid_item_set = new HashSet<Set<String>>();
    		
    		for(Set<String> item: item_set)
    		{
	    	    if(apriori.get_count(content, item)>=(MST/100)*content.size())
	    	    {
	    	    	final_set.add(item);
	    	        valid_item_set.add(item);
	    	    }
    		}
    		if(valid_item_set.size()!=0)
    			temp_valid_item_set = new HashSet<Set<String>>();
    			temp_valid_item_set.addAll(valid_item_set);
    		item_set = get_generate_candidates(valid_item_set,final_set,k_index);
    		k_index++;
    	}
    	return temp_valid_item_set;
    }
    
    public Set<String> deriving_association(List<Set<String>> content,Set<Set<String>> final_set,float MCT)
    {
    	Set<String> hide_part = new HashSet<String>();
		File file = new File("/D:/Eclipse/workspace/Apriori/src/output/deriving_association.txt");
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(file);
	    	float count_first_set,count_second_set;
	        for(Set<String> item : final_set)
	        {
	        	List<Set<String>> item_as_list = convect_set_string_to_list_set_string(item);
	        	for(int index=1;index<item_as_list.size();index++)
	        	{
	        		for(Set<String> first_part : Combinations(item_as_list,index))
	        		{
	        			Set<String> second_part = new HashSet<String>();
	        			second_part.addAll(item);
	        			second_part.removeAll(first_part);
	        			count_first_set = 0;
	        			count_second_set = 0;
	        			for(Set<String> transaction : content)
	        			{
	        				if(transaction.containsAll(first_part))
	        				{
	        					count_first_set++;
	        					if(transaction.containsAll(second_part))
	        						count_second_set++;
	        				}
	        			}
	        			try
	        			{
	        				float confidence = (count_second_set/count_first_set)*100;
	        				if(confidence>=MCT)
	        				{
	        					hide_part.addAll(second_part);
	        					writer.println(first_part+" ---> "+second_part+" with confidence "+confidence+"%");
	        				}
	        			}
	        			catch(Exception e){}
	        		}
	        	}
	        }
	        writer.close();
		}

		 catch (FileNotFoundException e)
		 {
			System.out.println("Some IOException...check deriving_association.txt file");
		 }
		return hide_part;
		
    }


	public static void replaceSelected(Set<String> hide_part) 
	{
		try 
		{
			File file = new File("/D:/Eclipse/workspace/Apriori/src/assert/transation_details.txt");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer("");
			String line;
			while ((line = bufferedReader.readLine()) != null) 
			{
				for(String str : hide_part)
					if (line.contains(str+","))
		                line = line.replace(str+",","");
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
	        PrintWriter writer = new PrintWriter(file);
	        writer.println(stringBuffer.toString());
	        writer.close();
		} 
		catch (IOException e)
		{
			System.out.println("Some IOException...check transation_details.txt file");
		}
	}

	public static void main(String[] args) 
	{
		
		long startTime = System.currentTimeMillis();
		
        //create main object
		Apriori apriori = new Apriori();
        
        //get content of file as list of sets
        List<Set<String>> content = new ArrayList<Set<String>>();
        content = apriori.get_content();
        int length_of_content = content.size();
        
        //get mst and mct from file
        List<Float> additional_info = apriori.get_additional_info();
        
        //create item set from sets in content
        Set<Set<String>> item_set = new HashSet<Set<String>>();
        for(int index1=0;index1<length_of_content;index1++)
        {
        	String[] set_to_array = content.get(index1).toArray(new String[content.get(index1).size()]);
        	for(int index2=0;index2<content.get(index1).size();index2++)
        	{
        		Set<String> element = new HashSet<String>();
        		element.add(set_to_array[index2]);
        		item_set.add(element);
        	}
        }

        Set<Set<String>> final_set = new HashSet<Set<String>>();
        final_set = apriori.get_frequent_item_set(content,item_set,additional_info.get(0));

        Set<String> hide_part = new HashSet<String>();
        hide_part = apriori.deriving_association(content,final_set,additional_info.get(1));
        
        System.out.println("Succussfully executed");
        replaceSelected(hide_part);
        
        Runtime runtime = Runtime.getRuntime();
	    runtime.gc();
	    long memory = runtime.totalMemory() - runtime.freeMemory();
	    
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        System.out.println("Execution Time:: "+elapsedTime+" MS");
        System.out.println("Memory Usage:: "+memory+" Bytes");

    }
}
