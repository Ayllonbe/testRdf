package blazegraph.ontology;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

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

import springboot.annotation.Annotation;
import springboot.annotation.Gene;
import springboot.annotation.Protein;
import springboot.annotation.Term;

	
/**
 * Class to add relevant information to triple store. 
 * @author Aaron Ayllon-Benitez
 * 
 */

public class AddInformation {

	OWLOntology ontology;
	OWLReasoner reasoner;
	OWLOntologyManager manager ;
	Set<OWLClass> GOThing;
	String prefixGO;
	
	public static void main(String[] args) {
		File go = new File("/home/aaronayllon/Bureau/go.owl");
		File goa = new File("/home/aaronayllon/Documents/goa_human-30-08-2017.gaf");
		File mapping = new File("/home/aaronayllon/Bureau/idmapping.dat");
		try {
			Runtime.getRuntime().exec("sudo docker start e");
			AddInformation ai = new AddInformation(go);
//			ai.additionalInfo();
			ai.addAnnotation(goa);
//			ai.addGene(mapping);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * Initialize the class unsing OWLAPI and ontology file (owl). 
	 * 
	 * @param go
	 * @throws OWLOntologyCreationException
	 */
	public AddInformation(File go) throws OWLOntologyCreationException {



		// We first need to obtain a copy of an
		// OWLOntologyManager, which, as the name
		// suggests, manages a set of ontologies. 
		this.manager = OWLManager.createOWLOntologyManager();

		//			// We load an ontology from the URI specified
		//			// on the command line



		this.ontology = manager.loadOntologyFromOntologyDocument(go);
		OWLReasonerFactory reasonerFactoryin = new ElkReasonerFactory();
		this.reasoner = reasonerFactoryin.createReasoner(ontology);

		IRI classIRI = OWLRDFVocabulary.OWL_THING.getIRI();
		OWLClass clazz = this.manager.getOWLDataFactory().getOWLClass(classIRI);
		Set<OWLClass> onto = this.reasoner.getSubClasses(clazz, true).getFlattened();
		this.GOThing = new HashSet<OWLClass>();
				
				//We remove the obsolete terms presented only in the top of ontology.
				for(OWLClass r:onto){
					if(this.reasoner.getSubClasses(r, true).getFlattened().size()>1)
						this.GOThing.add(r);
						
					
					
				}
				
		for(OWLClass o : GOThing) {
			String[] array = o.getIRI().toString().split("/");
			StringBuilder sb = new StringBuilder();
			for(int i =0 ; i<array.length-2;i++) {
				sb.append(array[i]+"/");
			}
		 this.prefixGO = sb.toString();
		 System.out.println(sb);
		 break;
		}
	}
	
	/**
	 * This method allow to add several information of OWLClass. Using the OWLAPI to read the go file, 
	 * the depth, IC_nuno and IC_zhou are posted in the triple store blazegraph using the update query
	 * in SPARQL.
	 * @param go
	 * @throws MalformedURLException 
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 * @throws UpdateExecutionException 
	 * @throws Exception
	 */
	
	public void additionalInfo() throws MalformedURLException, RepositoryException, MalformedQueryException, UpdateExecutionException  {
		 
				
			/*
			 * We initialize several Maps. 
			 */
			Map<OWLClass,Integer> owl2depth = new HashMap<>();
			Map<OWLClass,Double> owl2icnuno = new HashMap<>();
			Map<OWLClass,Integer> subOwl2depth = new HashMap<>();
			
			/*
			 * Init the process to get depth and IC_nuno information.
			 * For that we apply an adapted algorithm from the provided
			 * by Ignacio in OWLAPI. 
			 * 
			 */
			
			for(OWLClass sub : GOThing) {

				int flag=1; // variable to use in the while condition. 
				int level = 0;
				int numberMaxConcept = reasoner.getSubClasses(sub, false).getFlattened().size(); //I don't add +1 because that count the BottomThing. 
				TreeSet<OWLClass> list2 = new TreeSet<OWLClass>(); // This list is used as pivote to list1. 
				Set<OWLClass> list1=new HashSet<OWLClass>();
				
				Set<Integer> depths = new HashSet<>();
				list1.add(sub);
				while(flag==1) 
				{
					for (OWLClass pere : list1) {
						/*
						 * The follow condition allow to remove the entity botom 
						 */
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
			
			/*
			 * Query to delete the possible relation with depth and IC
			 */
			
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
	
			/*
			 * Query to insert the relation with depth and IC
			 */
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
				
				// Begin of Update query to delete the relations
				Update triple = conn.prepareUpdate(org.openrdf.query.QueryLanguage.SPARQL, querydel);
				triple.execute();
				//In this loop, we add for each GO Term these relations. 
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
			try {
				repoManager.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void addAnnotation(File goa) throws IOException {
		
		Map<String,Set<String>> gen2prot = new HashMap<String,Set<String>>();
		Map<String, Set<String>> prot2termBP = new HashMap<String,Set<String>>();
		Iterator<List<String>> iter = ReadAnnotation(goa).iterator();
		while(iter.hasNext()) {
			List<String> line = iter.next();
			if(line.get(8).equals("P")) {
				if(!line.get(3).equals("NOT") && !line.get(6).equals("ND") ) {
//				System.out.println(line.get(1) +" " +line.get(2)+ " " +line.get(12)+ " " +line.get(10) + " " + line.get(4)
//				+ " " + line.get(8) + " "+ line.get(3)+" "+line.get(6));
				if(gen2prot.containsKey(line.get(2))) {
					gen2prot.get(line.get(2)).add(line.get(1));
				}else {
					gen2prot.put(line.get(2), new HashSet<String>());
					gen2prot.get(line.get(2)).add(line.get(1));
				}
				
				if(prot2termBP.containsKey(line.get(1))) {
					prot2termBP.get(line.get(1)).add(line.get(4));
				}else {
					
					prot2termBP.put(line.get(1),new HashSet<String>());
					prot2termBP.get(line.get(1)).add(line.get(4));
				}
				}
				}
			
		}
		
		
		for(String g : gen2prot.keySet()) {
			if(gen2prot.get(g).size()>1)
			System.out.println(g + " " + gen2prot.get(g).size());
		}
		
		
	}
	public void addGene(File mapping) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		 Map<String,String> acceptsDB = new HashMap<String,String>();
		 acceptsDB.put("UniProtKB-ID", "uniprot");
		 acceptsDB.put("Gene_ORFName", "symbol");
//		 acceptsDB.put("EMBL", "embl");
		 acceptsDB.put("GeneID", "rdf:type");
		 acceptsDB.put("NCBI_TaxID", "taxon");
		 acceptsDB.put("Ensembl", "ensembl");
		 acceptsDB.put("KEGG", "kegg");
		 acceptsDB.put("Gene_Name", "gn");
		 
		 
		 
		int count =0;
		String prot  = ""; 
		 Map<String,String> obj =new HashMap<>();
		
		FileInputStream inputStream = new FileInputStream(mapping);
	    Scanner sc = new Scanner(inputStream, "UTF-8");
	    while (sc.hasNextLine()) {
	        String line = sc.nextLine();
	        String[] l = line.split("\t");
		    if(l[0].equals(prot)) {
				if(acceptsDB.containsKey(l[1])){
					obj.put(l[1], l[2]);
					System.out.println("condition_3 " +l[0] + " " + l[1]+" "+ l[2]);
				}
			}else if(prot.equals("")) {
				prot = l[0];
				if(acceptsDB.containsKey(l[1])) {
					obj.put(l[1], l[2]);
					System.out.println("condition_2 " +l[0] + " " + l[1]+" "+ l[2]);
				}
				
			}
			else {
				obj = new HashMap<>();
				prot = l[0];
				if(acceptsDB.containsKey(l[1])) {
					obj.put(l[1], l[2]);
					System.out.println("condition_1 " +l[0] + " " + l[1]+" "+ l[2]);
				}
			}
	    }
		sc.close();
		
	}
public static List<List<String>> ReadAnnotation(File goa) throws IOException{
		
		Charset charset = Charset.forName("UTF-8");
		Pattern pat = Pattern.compile("^!");
		
		
		
	    List<String> lines = Files.readAllLines(goa.toPath(),charset);
	    
	    
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
