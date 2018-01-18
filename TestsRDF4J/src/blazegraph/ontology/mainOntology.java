package blazegraph.ontology;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class mainOntology {

	public static RemoteRepositoryManager repoManager;
	public static void main(String[] args) throws MalformedURLException, RepositoryException, InterruptedException {
		
		// url access. 
		String sparqlEndpoint = new URL("http", "localhost", 8889, "/bigdata").toExternalForm();
		
		repoManager = new RemoteRepositoryManager(sparqlEndpoint);
        RemoteRepository remoteRepo = repoManager.getRepositoryForNamespace("aaronbase"); // defautnamespace de blazegraph
        BigdataSailRemoteRepository repo = remoteRepo.getBigdataSailRemoteRepository();
        
		repo.initialize();
		
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		try {
			long s = System.currentTimeMillis();
			ExecutorService es = Executors.newCachedThreadPool();
			Thread2 infot = infoQuery(conn);
			es.execute(infot);
			es.shutdown();
			
			boolean finshed = es.awaitTermination(1, TimeUnit.MINUTES);
			Set<Thread1> threadset = new HashSet<Thread1>();
			Thread1 partOfDirect = partOfQuery(conn,true);
			Thread1 isADirect = isAQuery(conn, true);
			threadset.add(partOfQuery(conn,true));
			threadset.add(isAQuery(conn, true));
			threadset.add(partOfQuery(conn,false));
			threadset.add(isAQuery(conn, false));
			
			es = Executors.newCachedThreadPool();
			
			for(Thread1 t : threadset)
			    es.execute(t);
		
			es.shutdown();
			
			finshed = es.awaitTermination(1, TimeUnit.MINUTES);
			long e = System.currentTimeMillis();
			System.out.println((e-s) * 0.001);
			System.out.println("hola");
		} catch (QueryEvaluationException | MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// ADD ONTOLOGY
//		try {
//			conn.add(new File("/home/aaronayllon/Bureau/go.owl"), "", RDFFormat.RDFXML, null);
//			System.out.println("Charged");
//		} catch (RDFParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
	}
	
	public static Thread1 partOfQuery(BigdataSailRemoteRepositoryConnection conn, boolean direct) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		String pof = "go:partOf*";
		if(direct) {
			pof = "go:partOf";
		}
		
		// that query recover the part of using transitive close for each terms in GO. Warning!! that recover 
		// the term itself as part of itself. 
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
				"prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>"+
				"SELECT ?s1 ?s2\n" + 
				" WHERE{\n" + 
				"?2eGO oboInOwl:id  ?s1.\n"+
				"?2eGO a owl:Class.\n" + 
				"?_restriction oboInOwl:id  ?s2.\n"+
				"?_restriction a owl:Class.\n" + 
				"           ?2eGO "+ pof +" ?_restriction . \n" + 
				" \n" + 
				"  }" ;
		
		Thread1 t = new Thread1(conn,query,pof);
		return t;
		
	}
	
	public static Thread2 infoQuery(BigdataSailRemoteRepositoryConnection conn) throws QueryEvaluationException, RepositoryException, MalformedQueryException {

		// that query recover the part of using transitive close for each terms in GO. Warning!! that recover 
		// the term itself as part of itself. 
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"			PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
				"			prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"			PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>\n" + 
				"			SELECT ?id ?label ?deprecated\n" + 
				"WHERE{\n" + 
				"			?uri  oboInOwl:id ?id.\n" + 
				"            ?uri  rdfs:label ?label.\n" + 
				"			?uri a owl:Class. \n" + 
				" 			OPTIONAL{?uri owl:deprecated ?deprecated\n" + 
				"              }\n" + 
				"            }" ;
		
		Thread2 t = new Thread2(conn,query);
		return t;
		
	}
	
public static Thread1 isAQuery(BigdataSailRemoteRepositoryConnection conn, boolean direct) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		
	String scl = "rdfs:subClassOf*";
	if(direct) {
		scl = "rdfs:subClassOf";
	}
		// that query recover the is a using transitive close for each terms in GO. Warning!! that recover 
		// the term itself as is_a itself. 
	String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
			"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
			"prefix owl: <http://www.w3.org/2002/07/owl#> \n" + 
			"PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>\n"+
			"SELECT ?s1 ?s2\n" + 
			"WHERE{\n" + 
			"?2eGO  oboInOwl:id ?s1.\n"+
			"  ?2eGO a owl:Class.\n" + 
			"?_restriction  oboInOwl:id ?s2.\n"+
			"  ?_restriction a owl:Class.\n" + 
			"  ?2eGO "+scl+" ?_restriction . \n" + 
			
			" \n" + 
			"  }" ;
		
	Thread1 t = new Thread1(conn,query,scl);
	
	return t;
		
	}
	
	
}
 class Thread1 extends Thread {
	 private TupleQueryResult t;
	 private String rel;
	  public Thread1(BigdataSailRemoteRepositoryConnection conn, String query, String type){
		  try {
			this.t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
			this.rel = type;
		  } catch (QueryEvaluationException | RepositoryException | MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	  }
	  public void run(){
			try {
				Set<String> test = new HashSet<String>();
				while (this.t.hasNext()) {
					BindingSet bs = t.next();
					Value t1 = bs.getValue("s1");
					Value t2 = bs.getValue("s2");
					test.add(t1.stringValue());
				//System.out.println(t1.stringValue() + " " +this.rel + " "+  t2.stringValue() );

				}
				System.out.println(this.getName() + " " + test.size());
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }       
	}
 
 class Thread2 extends Thread {
	 private TupleQueryResult t;
	 public Map<String, String> hm = new  HashMap<String,String>();
	  public Thread2(BigdataSailRemoteRepositoryConnection conn, String query){
		  try {
			this.t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		  } catch (QueryEvaluationException | RepositoryException | MalformedQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	  }
	  public void run(){
			try {
				
				
				while (this.t.hasNext()) {
					BindingSet bs = t.next();
					Value t1 = bs.getValue("id");
					Value t2 = bs.getValue("label");
					Value t3 = bs.getValue("deprecated");
				if(t3== null) {
					hm.put(t1.stringValue(), t2.stringValue());
				}
				//System.out.println(t1.stringValue() + " " +this.rel + " "+  t2.stringValue() );

				}
				System.out.println(this.getName() + " " + hm.keySet().size());
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }       
	}
