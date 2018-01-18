package blazegraph.ontology;

import java.io.Serializable;

public class InfoTerm implements Comparable<InfoTerm>, Serializable {

	
 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5356804658328772065L;
	public String id = new String();
    public final double h_scores;
    public String top;
	public String name= new String();
	public Link is_a;
	public Link part_of;


	public InfoTerm(String id, String label){
		this.id = new String();
		this.name=new String();
		this.is_a = new Link();
		this.part_of = new Link();
		this.h_scores = 0.;
		
	}


	@Override
	public int compareTo(InfoTerm arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}