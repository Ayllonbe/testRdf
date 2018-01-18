package springboot.annotation;

import java.util.HashMap;
import java.util.Map;

public class Annotation {
	private String proteinId;
	private String termId;
	private String geneId;
	private String qualifier;
	private String reference;
	private String evidence;
	private String withOrFrom;
	private String annotationExtension;
	private String version;
	
	public Annotation(String pI, String tI,String gI, String qualifier, String ref, 
			String ec,String v) {
		this.proteinId = pI;
		this.termId = tI;
		this.geneId =gI;
		this.qualifier = qualifier;
		this.reference = ref;
		this.evidence = ec;
		this.version = v;
	}
	public Map<String,Object> exportProtein(){
		Map<String, Object> map = new HashMap<>();
		map.put("term", this.termId);
		map.put("gene", this.geneId);
		map.put("protein", this.proteinId);
		map.put("qualifier", this.qualifier);
		map.put("reference", this.reference);
		map.put("evidence_code", this.evidence);
		map.put("version",this.version);
		return map;
	}
}
