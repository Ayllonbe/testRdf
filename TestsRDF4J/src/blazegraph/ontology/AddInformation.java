package blazegraph.ontology;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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


public class AddInformation {

	
	
	
	public static void main(String[] args) throws OWLOntologyCreationException {
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

				for(OWLClass r:onto){
					if(reasoner.getSubClasses(r, true).getFlattened().size()>1)
					GOThing.add(r);
						
					
					
				}
			Map<OWLClass,Integer> owl2depth = new HashMap<>();
			for(OWLClass sub : GOThing) {

				int flag=1;
				int level = 0;
				
				TreeSet<OWLClass> list2 = new TreeSet<OWLClass>();
				Set<OWLClass> list1=new HashSet<OWLClass>();
				list1.add(sub);
				while(flag==1) 
				{
					for (OWLClass pere : list1) {
						if (!pere.isBottomEntity() ) { 
							owl2depth.put(pere, level);
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
				
				
			
			}
			
		
	}
}
