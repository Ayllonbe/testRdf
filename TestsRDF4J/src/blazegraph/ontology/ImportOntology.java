package blazegraph.ontology;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import org.apache.log4j.Logger;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class ImportOntology {
	
	
	
	private static Logger logger = Logger.getLogger(ImportOntology.class);
	private static String uriID;
	public static RemoteRepositoryManager repoManager;
	
	public static void main(String[] args) {
		
		try {
		String sparqlEndpoint = new URL("http", "localhost", 8889, "/bigdata").toExternalForm();
		System.out.println(sparqlEndpoint);
		repoManager = new RemoteRepositoryManager(sparqlEndpoint);
        RemoteRepository remoteRepo = repoManager.getRepositoryForNamespace("aaronbase"); // defautnamespace de blazegraph
        BigdataSailRemoteRepository repo = remoteRepo.getBigdataSailRemoteRepository();
     
			repo.initialize();
			BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
			importVersion(conn, "/home/aaronayllon/Bureau/go.owl");
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	  public static void importVersion(BigdataSailRemoteRepositoryConnection connexion,String file){
		  logger.info("## Import Ontology##");
		  try { 
			  long d = new Random().longs().findAny().getAsLong();
			  uriID = "http://blazegraph.geneontology/"+Long.toHexString(d);
			  connexion.add(new File(file), "", RDFFormat.RDFXML, connexion.getValueFactory().createURI(uriID));
			  updateGraph(connexion,uriID);
			  updatePartOfEdge(connexion);
			  System.out.println(uriID);
			 logger.info("Ontology changed");
			
			  } catch (Exception e) {
			 logger.error(e.fillInStackTrace());
			  }
	  }
	  
	  public static void updateGraph(BigdataSailRemoteRepositoryConnection connexion , String url) {
		  String updateGraph = "INSERT {\n" + 
			 		"  GRAPH <http://gsan.metadata> {\n" + 
			 		"  	<http://geneontology.repository> gsan:currentVersion "+ url +".  \n" + 
			 		"  }\n" + 
			 		"}\n" + 
			 		"WHERE {\n" + 
			 		"  GRAPH <http://gsan.metadata> {\n" + 
			 		"  	<http://geneontology> aaron:curVersion ?version .  \n" + 
			 		"  }\n" + 
			 		"}";
			 try {
				connexion.prepareUpdate(org.openrdf.query.QueryLanguage.SPARQL, updateGraph);
			} catch (Exception e) {
				logger.error(e.fillInStackTrace());
			}
	  }
	  public static void updatePartOfEdge(BigdataSailRemoteRepositoryConnection connexion ) {
		  String updateGraph = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
		  		"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
		  		"prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
		  		"INSERT {\n" + 
		  		"  GRAPH<http://creation.owl.partof>{\n" + 
		  		"      ?2eGO go:partOf ?MACHIN} } WHERE { "
		  		+ 	"  GRAPH <http://gsan.metadata> {\n" + 
		 		"  	<http://geneontology> aaron:curVersion ?version .  \n" + 
		 		"  }\n" + 
		  		"  GRAPH ?toto "
		  		+ "{       \n" + 
		  		"  ?2eGO rdfs:subClassOf* ?_restriction . \n" + 
		  		"   ?_restriction owl:onProperty go:BFO_0000050 . \n" + 
		  		"   ?_restriction owl:someValuesFrom  ?term1.\n" + 
		  		"  ?term1 rdfs:subClassOf* ?MACHIN.\n" + 
		  		"         FILTER  (?2eGO != ?MACHIN)\n" + 
		  		"\n" + 
		  		"}\n" + 
		  		"}";
			 try {
				connexion.prepareUpdate(org.openrdf.query.QueryLanguage.SPARQL, updateGraph);
			} catch (Exception e)  {
				logger.error(e.fillInStackTrace());
			}
	  }
}
