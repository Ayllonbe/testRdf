package springboot.annotation;

import java.util.HashMap;
import java.util.Map;

public class Term {
	public final String id;
	public final String uri;
	private String subOntology;
	private String version;
	
	public Term(String id, String sub, String v) {
		this.id = id;
		this.uri = "<http://purl.obolibrary.org/obo/"+ id.replace(":", "_") + ">";
		this.subOntology = sub;
		this.version= v;
		}
	public Map<String,Object> exportTerm(){
		Map<String, Object> map = new HashMap<>();
		map.put("id", this.id);
		map.put("uri", this.uri);
		map.put("subOntology", this.subOntology);
		map.put("version",this.version);
		return map;
	}
	@Override
	public int hashCode(){
        return id.hashCode();
    }
	@Override 
    public boolean equals(Object obj){
        if (obj instanceof Term) {
            Term pp = (Term) obj;
            return (pp.id.equals(this.id));
        } else {
            return false;
        
	
}
    }

}
