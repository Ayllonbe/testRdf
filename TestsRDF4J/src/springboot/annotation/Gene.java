package springboot.annotation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Gene  {

	public final String id; // symbol_taxon
	private String symbol;
	private String taxon;
	private List<String> synonym;
	private String version;
	
	public Gene(String symbol, String taxon, String synonym, String version) {
		this.id = symbol+"_"+taxon.replace("taxon:", "");
		this.symbol = symbol;
		this.taxon = taxon.replace("taxon:", "");
		this.synonym = Arrays.asList(synonym.split("\\|"));
		this.version = version;
		
	}
	public Map<String,Object> exportGene(){
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("symbol", this.symbol);
		map.put("taxon",this.taxon);
		map.put("synonym", this.synonym);
		map.put("version",this.version);
		return map;
	}
	@Override
	public int hashCode(){
        return this.id.hashCode();
    }
	@Override 
    public boolean equals(Object obj){
        if (obj instanceof Gene) {
            Gene pp = (Gene) obj;
            return (pp.id.equals(this.id));
        } else {
            return false;
        
	
}
    }
}
