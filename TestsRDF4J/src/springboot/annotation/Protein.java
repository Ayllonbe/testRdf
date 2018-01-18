package springboot.annotation;

import java.util.HashMap;
import java.util.Map;

public class Protein {
	public String id;
	private String gene_id;
	private String name;
	private String version;
	
	
	public Protein(String id,String gI ,String name, String v) {
		this.id = id;
		this.gene_id = gI;
		this.name = name;
		this.version= v;
		}
	public Map<String,Object> exportProtein(){
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("gene", this.gene_id);
		map.put("name", this.name);
		map.put("version",this.version);
		return map;
	}
	@Override
	public int hashCode(){
        return id.hashCode();
    }
	@Override 
    public boolean equals(Object obj){
        if (obj instanceof Protein) {
            Protein pp = (Protein) obj;
            return (pp.id.equals(this.id) && pp.gene_id.equals(this.gene_id));
        } else {
            return false;
        
	
}
    }
	
}
