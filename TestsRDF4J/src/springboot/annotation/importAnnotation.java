package springboot.annotation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class importAnnotation {
	private String version;
	
	public static void main(String[] args) {
		try {
			String version = "v";
			Set<Gene> set = new HashSet<>();
			Set<Term> setT = new HashSet<>();
			Set<Protein> setP = new HashSet<>();
			Set<Annotation> setA = new HashSet<>();
 			Iterator<List<String>> goa = ReadAnnotation("/home/aaron/Documents/Thesis_Project/Data/Genoma/goa_human-30-08-2017.gaf").iterator();
			while(goa.hasNext()) {
				List<String> line = goa.next();
				
				Gene g = new Gene(line.get(2),line.get(12),line.get(10),version);
				Protein p= new Protein (line.get(1),g.id,line.get(9),version);
				Term t = new Term(line.get(4),line.get(8),version);
				Annotation a = new Annotation(p.id,t.id,g.id,line.get(3),line.get(5),line.get(6),version);
				set.add(g);
				setT.add(t);
				setP.add(p);
				setA.add(a);
			}
		
			System.out.println(set.size());
			System.out.println(setT.size());
			System.out.println(setP.size());
			System.out.println(setA.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
public static List<List<String>> ReadAnnotation(String goa) throws IOException{
		
		Charset charset = Charset.forName("UTF-8");
		Pattern pat = Pattern.compile("^!");
		
		
		
	    List<String> lines = Files.readAllLines(Paths.get(goa),charset);
	    
	    
	    List<List<String>> list = new ArrayList<List<String>>();
	 
	    while(pat.matcher(lines.get(0)).find()){
	    	lines.remove(0);
	    	
	    }
	    for(String l : lines){
	    	list.add(Arrays.asList(l.split("\t"))); // !! 3.6 sec. le procesus pour human
	    }
	 
	   
		
		
		return list;
		}
}
