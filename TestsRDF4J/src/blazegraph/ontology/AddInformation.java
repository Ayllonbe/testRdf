package blazegraph.ontology;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;


public class AddInformation {

	
	
	
	public static void main(String[] args) throws Exception {
		IRI classIRI = null;


		// We first need to obtain a copy of an
		// OWLOntologyManager, which, as the name
		// suggests, manages a set of ontologies. 
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

		//			// We load an ontology from the URI specified
		//			// on the command line

		//			// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------	
		//			// ------------------------------------------------------Charging Ontology --------------- ----------------------------------------------------------------------------------------- 
		//			time_start = System.currentTimeMillis();
		// System.out.println(args[i]);


			OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File("/home/aaronayllon/Bureau/go.owl"));
			OWLReasonerFactory reasonerFactoryin = new ElkReasonerFactory();
			 OWLReasoner reasoner = reasonerFactoryin.createReasoner(ontology);
			 classIRI = OWLRDFVocabulary.OWL_THING.getIRI();
				//classIRI.create("http://purl.obolibrary.org/obo/BSPO_0000085");
	      
			OWLClass clazz = manager.getOWLDataFactory().getOWLClass(classIRI);
			 Set<OWLClass> onto = reasoner.getSubClasses(clazz, true).getFlattened();
				//System.out.println("[GlobalOntology] Reasoner Charged");
				Set<OWLClass> GOThing = new HashSet<OWLClass>();
				
				//We reli
				for(OWLClass r:onto){
					if(reasoner.getSubClasses(r, true).getFlattened().size()>1)
					GOThing.add(r);
						
					
					
				}
				
			/*
			 * We initialize several Maps. 
			 */
			Map<OWLClass,Integer> owl2depth = new HashMap<>();
			Map<OWLClass,Double> owl2icnuno = new HashMap<>();
			Map<OWLClass,Integer> subOwl2depth = new HashMap<>();
			
			for(OWLClass sub : GOThing) {

				int flag=1;
				int level = 0;
				int numberMaxConcept = reasoner.getSubClasses(sub, false).getFlattened().size(); //I don't add +1 because that count the BottomThing. 
				TreeSet<OWLClass> list2 = new TreeSet<OWLClass>();
				Set<OWLClass> list1=new HashSet<OWLClass>();
				
				Set<Integer> depths = new HashSet<>();
				list1.add(sub);
				while(flag==1) 
				{
					for (OWLClass pere : list1) {
						if (!pere.isBottomEntity() ) { 
							owl2depth.put(pere, level);
							depths.add(level);
							double ic = 1-(Math.log10(reasoner.getSubClasses(pere,false).getFlattened().size())/Math.log10(numberMaxConcept));
							owl2icnuno.put(pere, ic);
							
							list2.addAll(reasoner.getSubClasses(pere,true).getFlattened());
									}
				}
					list1.clear();
					//if(list2.size()!=0){
					
						if(!list2.isEmpty()){
							
						list1.addAll(list2);
						list2.clear();
						level = level + 1;
						flag=1;
					}
					else
						flag=0;
					
				}
				subOwl2depth.put(sub, Collections.max(depths));
				
			
			}
			
			OWLClass o = manager.getOWLDataFactory().getOWLClass(IRI.create("http://purl.obolibrary.org/obo/GO_0051179"));
			OWLClass t = manager.getOWLDataFactory().getOWLClass(IRI.create("http://purl.obolibrary.org/obo/GO_0008150"));
			System.out.println(0.5*owl2icnuno.get(o)+0.5*(Math.log10(owl2depth.get(o))/Math.log10(subOwl2depth.get(t))));
			
			String querydel = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
					"		  		PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
					"		  		prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
					"		  		DELETE {\n" + 
					"		  		  GRAPH <http://blazegraph/ontology.GO/gsan/>{\n" + 
					"		  		      ?s owl:depth ?depth;\n" + 
					"                  			owl:IC_Nuno ?icn;\n" + 
					"                            owl:IC_Zhou ?icz.\n" + 
					"                  \n" + 
					"                  \n" + 
					"                  } } WHERE{ ?s owl:depth ?depth;"
					+ "owl:IC_Nuno ?icn;"
					+ "owl:IC_Zhou ?icz }";
	
			String queryAdd = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
					"		  		PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
					"		  		prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
					"		  		INSERT {\n" + 
					"		  		  GRAPH <http://blazegraph/ontology.GO/gsan/>{\n" + 
					"		  		      ?s owl:depth ?depth;\n" + 
					"                  			owl:IC_Nuno ?icn;\n" + 
					"                            owl:IC_Zhou ?icz.\n" + 
					"                  \n" + 
					"                  \n" + 
					"                  } } WHERE{ }";
			
			String sparqlEndpoint = new URL("http", "localhost", 8889, "/bigdata").toExternalForm();
			System.out.println(sparqlEndpoint);
			 RemoteRepositoryManager repoManager = new RemoteRepositoryManager(sparqlEndpoint);
	        RemoteRepository remoteRepo = repoManager.getRepositoryForNamespace("aaronbase"); // defautnamespace de blazegraph
	        BigdataSailRemoteRepository repo = remoteRepo.getBigdataSailRemoteRepository();
	     
				repo.initialize();
				BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
			
				Update triple = conn.prepareUpdate(org.openrdf.query.QueryLanguage.SPARQL, querydel);
				triple.execute();
				
			for(OWLClass sub : subOwl2depth.keySet()) {
				
				
					
						
						
					String query = queryAdd.replace("?s", "go:"+sub.getIRI().toString().split("/")[sub.getIRI().toString().split("/").length-1] ).
				replace("?depth", "0" ).replace("?icn", "0").replace("?icz", "0" );

				
				
				triple = conn.prepareUpdate(org.openrdf.query.QueryLanguage.SPARQL, query);
				triple.execute();
			for(OWLClass te : reasoner.getSubClasses(sub, false).getFlattened()) {
				if (!te.isBottomEntity() ) { 
					
				Double icz = 0.5*owl2icnuno.get(te) + 0.5*((Math.log10(owl2depth.get(te))/Math.log10(subOwl2depth.get(sub))));
				query = queryAdd.replace("?s", "go:"+te.getIRI().toString().split("/")[te.getIRI().toString().split("/").length-1] ).
						replace("?depth", owl2depth.get(te).toString() ).replace("?icn", owl2icnuno.get(te).toString()).replace("?icz", icz.toString() );
				triple = conn.prepareUpdate(org.openrdf.query.QueryLanguage.SPARQL, query);
				triple.execute();
				}
			}
			}
			
			conn.close();
			repoManager.close();
	}
}
