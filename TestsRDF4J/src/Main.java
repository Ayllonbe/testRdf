import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class Main {

	public static final Map<String, RDFFormat> transcodeMime2RDFFormat;
	private static RemoteRepositoryManager repoManager;
	static {
		Map<String, RDFFormat> tmp = new HashMap<String, RDFFormat>();
		tmp.put("application/rdf+xml", RDFFormat.RDFXML);
		tmp.put("application/xml", RDFFormat.RDFXML);
		tmp.put("text/plain", RDFFormat.NTRIPLES);

		tmp.put("text/turtle", RDFFormat.TURTLE);
		tmp.put("application/x-turtle", RDFFormat.TURTLE);

		tmp.put("text/n3", RDFFormat.N3);
		tmp.put("text/rdf+n3", RDFFormat.N3);

		tmp.put("application/trix", RDFFormat.TRIX);
		tmp.put("application/x-trig", RDFFormat.TRIG);

		tmp.put("application/x-binary-rdf", RDFFormat.BINARY);

		tmp.put("text/x-nquads", RDFFormat.NQUADS);

		tmp.put("application/ld+json", RDFFormat.JSONLD);
		tmp.put("application/rdf+json", RDFFormat.RDFJSON);
		tmp.put("application.xhtml+xml", RDFFormat.RDFA);

		transcodeMime2RDFFormat = Collections.unmodifiableMap(tmp);
	}
	
	public static void selectQuery(BigdataSailRemoteRepository repo, String query) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		TupleQueryResult t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		//prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		try {
			while (t.hasNext()) {
				BindingSet bs = t.next();

				Value s = bs.getValue("s");
				Value p = bs.getValue("p");
				Value o = bs.getValue("o");
				
				if (s == null) 
					System.out.println(p.stringValue()+";"+o.stringValue());
				else
					System.out.println(s.stringValue()+";"+p.stringValue()+";"+o.stringValue());

			}
		} finally {
			t.close();
			conn.close();
		}
	}
	
	public static void Label(BigdataSailRemoteRepository repo, String query) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		TupleQueryResult t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		//prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		try {
			while (t.hasNext()) {
				BindingSet bs = t.next();

				Value sup = bs.getValue("super");
				Value sub= bs.getValue("sub");
				Value o = bs.getValue("distance");
				
				System.out.println(sup.stringValue() + " " + sub.stringValue()+" "+o.stringValue());

			}
		} finally {
			t.close();
			conn.close();
		}
	}

	public static void LCS(BigdataSailRemoteRepository repo, String query) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		// ADD ONTOLOGY
//		try {
//			conn.add(new File("/home/aaron/Documents/Thesis_Project/Data/ontology/go-10-2017.owl"), "", RDFFormat.RDFXML, null);
//		} catch (RDFParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		TupleQueryResult t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		//prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		try {
			while (t.hasNext()) {
				BindingSet bs = t.next();

				Value t1 = bs.getValue("term1");
				
				
//				System.out.println(t1.stringValue() );

			}
		} finally {
			t.close();
			conn.close();
		}
	}

	public static void insertQuery(Repository repo, String query) throws RepositoryException, UpdateExecutionException, MalformedQueryException {
		RepositoryConnection conn = repo.getConnection();
		conn.prepareUpdate(QueryLanguage.SPARQL, query).execute();
	}

	public static void getGraphByURI(Repository repo, String context, File f) throws FileNotFoundException, QueryEvaluationException, RepositoryException, MalformedQueryException, RDFHandlerException {
		RepositoryConnection conn = repo.getConnection();
		String query = /* get all prefixes */
				"CONSTRUCT {?s ?p ?o}"
				+ "WHERE {"
				+ "		GRAPH <"+context+"> {"
				+ "			?s ?p ?o"
				+ "		}"
				+ "}";

		GraphQueryResult gq = conn.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
		OutputStream out = null;			

		try {
			out = new FileOutputStream(f);

			// URLConnection connection;
			// OutputStream out = new OutputStreamWriter(connection.getOutputStream());
			
			RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);	
			Map<String, String> namespaces = gq.getNamespaces();
			
			/*
			 "application/rdf+xml", RDFFormat.RDFXML
			 "application/xml", RDFFormat.RDFXML
			 "text/plain", RDFFormat.NTRIPLES

			 "text/turtle", RDFFormat.TURTLE
			 "application/x-turtle", RDFFormat.TURTLE

			 "text/n3", RDFFormat.N3
			 "text/rdf+n3", RDFFormat.N3

			 "application/trix", RDFFormat.TRIX
			 "application/x-trig", RDFFormat.TRIG

			 "application/x-binary-rdf", RDFFormat.BINARY

			 "text/x-nquads", RDFFormat.NQUADS
			 "application/ld+json", RDFFormat.JSONLD
			 "application/rdf+json", RDFFormat.RDFJSON
			 "application.xhtml+xml", RDFFormat.RDFA
			 */
			
			for(Entry<String, String> e: namespaces.entrySet()) {
				writer.handleNamespace(e.getKey(), e.getValue());
			}
			
			
			writer.startRDF();
			while(gq.hasNext()) {
				Statement s = gq.next();
				writer.handleStatement(s);
				System.out.println(s);
			}
			writer.endRDF();
			System.out.println("endRDF");
		} finally {
			conn.close();
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

//	public static void importFILErdf(Repository repo, String namespace, File f, String mimeType, IRI...iris) throws RDFParseException, RepositoryException, IOException {
//		RepositoryConnection conn = repo.getConnection();
//		
//		try {
//			conn.add(f, namespace, transcodeMime2RDFFormat.get(mimeType), iris);
//		} finally {
//			if (conn != null) {
//				try {
//					conn.close(); 
//				} catch (final RepositoryException ignore) { 
//					
//				}
//			}
//		}
//	}
//	
//	public static void importURLrdf(Repository repo, String namespace, URL u, String mimeType, IRI...iris) throws RDFParseException, RepositoryException, IOException {
//		RepositoryConnection conn = repo.getConnection();
//
//		try {
//			conn.add(u, namespace, transcodeMime2RDFFormat.get(mimeType), iris);
//		} finally {
//			if (conn != null) {
//				try {
//					conn.close(); 
//				} catch (final RepositoryException ignore) { 
//					
//				}
//			}
//		}
//	}
	
	public static void importISrdf(BigdataSailRemoteRepository repo,
			String baseURI,
			InputStream is,
			String mimeType,
			URI uri) throws IOException, org.openrdf.rio.RDFParseException, org.openrdf.repository.RepositoryException {
		
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		
		try {
			conn.add(is, baseURI, transcodeMime2RDFFormat.get(mimeType) , uri);
		} finally {
			if (conn != null) {
				try {
					conn.close(); 
				} catch (final RepositoryException ignore) { 
					
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
//		Repository repo = new SPARQLRepository(sparqlEndpoint);
		
		String sparqlEndpoint = new URL("http", "localhost", 8889, "/bigdata").toExternalForm();
		
		repoManager = new RemoteRepositoryManager(sparqlEndpoint);
        RemoteRepository remoteRepo = repoManager.getRepositoryForNamespace("aaronbase"); // defautnamespace de blazegraph
        BigdataSailRemoteRepository repo = remoteRepo.getBigdataSailRemoteRepository();
        
		repo.initialize();
		
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
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
//		
		String[] terms = {"GO:0043653",
//				"GO:0008637","GO:0030239",
//				
//				"GO:2000483",
//				"GO:0050718",
//				"GO:0046578",
//				"GO:0045648",
//				"GO:0002761",
//				"GO:0051090",
//				"GO:0032727",
//				"GO:0006954",
//				"GO:0048489",
//				"GO:0007409",
//				"GO:0032728",
//				"GO:0006959",
//				"GO:0071375",
//				"GO:0032729",
//				"GO:0032689",
//				"GO:1902042",
//				"GO:0006691",
//			    "GO:0034341",
//				"GO:0050729",
//				"GO:0045654",
//				"GO:0051260",
//				"GO:0045416",
//				"GO:0051099",
//				"GO:0001558",
//				"GO:0043087",
//				"GO:0030890",
//				"GO:0032675",
//				"GO:0030098",
//				"GO:0030259",
//				"GO:0034976",
//				"GO:0055072",
//				"GO:0045429",
//				"GO:0060421",
//				"GO:1905049",
//				"GO:0097190",
//				"GO:0002460",
//				"GO:0007596",
//				"GO:0045669",
//				"GO:0045821",
//				"GO:0043524",
//				"GO:0001525",
//				"GO:2001242",
//				"GO:0090322",
//				"GO:0036065",
//				"GO:1904646",
//				"GO:0048545",
//				"GO:0007249",
//				"GO:0006953",
//				"GO:0001541",
//				"GO:0002224",
//				"GO:0071260",
//				"GO:0001932",
//				"GO:0030198",
//				"GO:0032496",
//				"GO:0001816",
//				"GO:0050919",
//				"GO:0120036",
//				"GO:0050918",
//				"GO:0006486",
//				"GO:0046777",
//				"GO:0045444",
//				"GO:0006887",
//				"GO:0045722",
//				"GO:0002322",
//				"GO:0015908",
//				"GO:0006639",
//				"GO:0043032",
//				"GO:0033574",
//				"GO:0030100",
//				"GO:0006919",
//				"GO:2000678",
//				"GO:2000279",
//				"GO:0034141",
//				"GO:0050806",
//				"GO:0007584",
//				"GO:0045730",
//				"GO:0008156",
//				"GO:0000302",
//				"GO:0007623",
//				"GO:0006898",
//				"GO:0048208",
//				"GO:0007589",
//				"GO:0098542",
//				"GO:0006888",
//				"GO:0032355",
//				"GO:0031663",
//				"GO:0070873",
//				"GO:0031664",
//				"GO:0072659",
//				"GO:0016567",
//				"GO:0008360",
//				"GO:0007275",
//				"GO:0042116",
//				"GO:0044262",
//				"GO:0070266",
//				"GO:0072602",
//				"GO:0034644",
//				"GO:0010951",
//				"GO:0045070",
//				"GO:0016311",
//				"GO:0045071",
//				"GO:0035336",
//				"GO:0015722",
//				"GO:0034122",
//				"GO:0007283",
//				"GO:0048469",
//				"GO:1901216",
//				"GO:0006876",
//				"GO:0006631",
//				"GO:0007204",
//				"GO:0032736",
//				"GO:0031647",
//				"GO:0071222",
//				"GO:0006906",
//				"GO:0019372",
//				"GO:0045087",
//				"GO:0042493",
//				"GO:0006904",
//				"GO:0006909",
//				"GO:0002309",
//				"GO:0034599",
//				"GO:0071345",
//				"GO:0050702",
//				"GO:0071621",
//"GO:0008637","GO:0030239",
//				
//				"GO:2000483",
//				"GO:0050718",
//				"GO:0046578",
//				"GO:0045648",
//				"GO:0002761",
//				"GO:0051090",
//				"GO:0032727",
//				"GO:0006954",
//				"GO:0048489",
//				"GO:0007409",
//				"GO:0032728",
//				"GO:0006959",
//				"GO:0071375",
//				"GO:0032729",
//				"GO:0032689",
//				"GO:1902042",
//				"GO:0006691",
//			    "GO:0034341",
//				"GO:0050729",
//				"GO:0045654",
//				"GO:0051260",
//				"GO:0045416",
//				"GO:0051099",
//				"GO:0001558",
//				"GO:0043087",
//				"GO:0030890",
//				"GO:0032675",
//				"GO:0030098",
//				"GO:0030259",
//				"GO:0034976",
//				"GO:0055072",
//				"GO:0045429",
//				"GO:0060421",
//				"GO:1905049",
//				"GO:0097190",
//				"GO:0002460",
//				"GO:0007596",
//				"GO:0045669",
//				"GO:0045821",
//				"GO:0043524",
//				"GO:0001525",
//				"GO:2001242",
//				"GO:0090322",
//				"GO:0036065",
//				"GO:1904646",
//				"GO:0048545",
//				"GO:0007249",
//				"GO:0006953",
//				"GO:0001541",
//				"GO:0002224",
//				"GO:0071260",
//				"GO:0001932",
//				"GO:0030198",
//				"GO:0032496",
//				"GO:0001816",
//				"GO:0050919",
//				"GO:0120036",
//				"GO:0050918",
//				"GO:0006486",
//				"GO:0046777",
//				"GO:0045444",
//				"GO:0006887",
//				"GO:0045722",
//				"GO:0002322",
//				"GO:0015908",
//				"GO:0006639",
//				"GO:0043032",
//				"GO:0033574",
//				"GO:0030100",
//				"GO:0006919",
//				"GO:2000678",
//				"GO:2000279",
//				"GO:0034141",
//				"GO:0050806",
//				"GO:0007584",
//				"GO:0045730",
//				"GO:0008156",
//				"GO:0000302",
//				"GO:0007623",
//				"GO:0006898",
//				"GO:0048208",
//				"GO:0007589",
//				"GO:0098542",
//				"GO:0006888",
//				"GO:0032355",
//				"GO:0031663",
//				"GO:0070873",
//				"GO:0031664",
//				"GO:0072659",
//				"GO:0016567",
//				"GO:0008360",
//				"GO:0007275",
//				"GO:0042116",
//				"GO:0044262",
//				"GO:0070266",
//				"GO:0072602",
//				"GO:0034644",
//				"GO:0010951",
//				"GO:0045070",
//				"GO:0016311",
//				"GO:0045071",
//				"GO:0035336",
//				"GO:0015722",
//				"GO:0034122",
//				"GO:0007283",
//				"GO:0048469",
//				"GO:1901216",
//				"GO:0006876",
//				"GO:0006631",
//				"GO:0007204",
//				"GO:0032736",
//				"GO:0031647",
//				"GO:0071222",
//				"GO:0006906",
//				"GO:0019372",
//				"GO:0045087",
//				"GO:0042493",
//				"GO:0006904",
//				"GO:0006909",
//				"GO:0002309",
//				"GO:0034599",
//				"GO:0071345",
//				"GO:0050702",
//				"GO:0071621"
};
		
		Set<String> set = new HashSet<>(Arrays.asList(terms));
		System.out.println(set.size());
		long s = System.currentTimeMillis();
//		for(String te : terms) {
		//		
		//			te = te.replace(":", "_");
		//			Sparql.superClass(repo, te);
		//		}
		//		

		
//		TupleQuery toto = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
//		for(String te : terms) {
//			toto.setBinding("s", repo.getValueFactory().createURI("http://purl.obolibrary.org/obo/" +te.replace(":", "_")));
//
//			TupleQueryResult tpls = toto.evaluate();
//
//			while(tpls.hasNext()) {
//				BindingSet bs = tpls.next();
//				Value su = bs.getValue("term1");
//			}
//			tpls.close();
//		}
		
		StringBuffer sb = new StringBuffer();
		int count = 0;
		for(String t : set) {
//			sb.append("?2eGO = go:"+t.replace(":", "_") + " || ");
			sb.append("go:"+t.replace(":", "_") + ", ");
//			if(count>-1) {
//				break;
//			}
			count++;
		}
		
		sb.delete(sb.length()-2, sb.length());
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
				"prefix owl: <http://www.w3.org/2002/07/owl#> \n" + 
				"PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>\n"+
				"SELECT ?s1 ?s2\n" + 
				"WHERE{\n" + 
				"  FILTER(?2eGO IN ("+sb.toString()+"))\n" + 
				"?2eGO oboInOwl:id ?s1.\n"+
				"  ?2eGO a owl:Class.\n" + 
				"?_restriction oboInOwl:id ?s2.\n"+
				"  ?_restriction a owl:Class.\n" + 
				"  ?2eGO  rdfs:subClassOf* ?_restriction . \n" + 
				
				" \n" + 
				"  }" ;
		
		
//		query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
//		"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
//		"prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
//		"PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>"+
//		"SELECT ?s1 ?s2\n" + 
//		" WHERE{\n" + 
//		"?2eGO oboInOwl:id  ?s1.\n"+
//		"?2eGO a owl:Class.\n" + 
//		"?_restriction oboInOwl:id  ?s2.\n"+
//		"?_restriction a owl:Class.\n" + 
//		"           ?2eGO go:partOf* ?_restriction . \n" + 
//		"  FILTER(?2eGO IN ("+sb.toString()+") )\n" + 
//		" \n" + 
//		"  }" ;
//		
		
		System.out.println(sb);
		TupleQueryResult t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		Set<String> test1 = new HashSet<>();
		Set<String> test2 = new HashSet<>();
		try {
			while (t.hasNext()) {
				BindingSet bs = t.next();
				Value t1 = bs.getValue("s1");
				Value t2 = bs.getValue("s2");
				
			test1.add(t1.stringValue());
			test2.add(t2.stringValue());
			System.out.println(t1.stringValue() + " - Part Of - "+  t2.stringValue() );

			}
		} finally {
//			t.close();
		}
		System.out.println(test1.size());
		System.out.println(test2.size());
		System.out.println(count);
		long e = System.currentTimeMillis();
		
		System.out.println((e-s) * 0.001);
	System.exit(0);
		
//		importURLrdf(repo,"", new URL("https://www.w3.org/2009/08/skos-reference/skos.rdf"), 
//				"application/rdf+xml", repo.getValueFactory().createIRI("http://www.w3.org/2004/02/skos/core#"));
//		
//		File ff = new File("./test.rdf");
//		ff.createNewFile();
//		getGraphByURI(repo, "http://www.w3.org/2004/02/skos/core#", ff);
//
//		String insert = "INSERT { GRAPH <http://toto.fr> { <http://toto.fr/toto> rdfs:label \" TOTO \" } } WHERE {}";
//		insertQuery(repo, insert);
//		
//		String select = "SELECT * WHERE { ?s ?p ?o } LIMIT 10 ";
//		selectQuery(repo, select);
//		
//		// Permet de parser en une ligne un fichier
//		//Model results = Rio.parse(is, "http://www.w3.org/2002/07/owl#", RDFFormat.TURTLE);
//
//		URL documentUrl = new URL("http://www.w3.org/2002/07/owl#");
//		InputStream is = documentUrl.openStream();
//
//		RDFParser rdfParser = Rio.createParser(RDFFormat.TURTLE);
//		Model model = new LinkedHashModel();
//		rdfParser.setRDFHandler(new StatementCollector(model));
//		rdfParser.parse(is, "http://www.w3.org/2002/07/owl#");
//		
//		File f = new File("./owlCopy.ttl");
//		f.createNewFile();
//		OutputStream out = new FileOutputStream(f, false);
//		RDFWriter writer = Rio.createWriter(RDFFormat.TURTLE, out);
//			
////		for (Namespace namespace: model.getNamespaces())
////			writer.handleNamespace(namespace.getPrefix(), namespace.getName());
//		
//		writer.startRDF();
//		for (Statement statement : model) {
//			writer.handleStatement(statement);
//		}
//		writer.endRDF();		
//		is.close();
//		
//		importISrdf(repo, "", 
//				new FileInputStream(new File("/home/aaron/Documents/Thesis_Project/Data/ontology/go-10-2017.owl")),
//				"application/rdf+xml",new URIImpl("http://www.testGO#")); 	
//		System.exit(0);
//		
		
//		String q = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
//		"	prefix owl: <http://www.w3.org/2002/07/owl#>"+
//		"	prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
//			"prefix obo: <http://purl.obolibrary.org/obo/>"+
//			"prefix form: <http://www.geneontology.org/formats/oboInOwl#>"+
//
//			"select *"+
//
//			"Where{"+
//
//			"obo:GO_0008637 rdfs:subClassOf [ owl:onProperty <http://purl.obolibrary.org/obo/BFO_0000050> ;"+
//			"?s ?o] ."+
//
//			"?o rdfs:subClassOf+ ?p ."+
//
//
//			"FILTER(?s != rdf:type) ."+
//			"FILTER(?o != <http://purl.obolibrary.org/obo/BFO_0000050>)"+
//
//			"}"+
//		"	ORDER BY ?o ?p";
		
//		String q = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
//				"	prefix owl: <http://www.w3.org/2002/07/owl#>"+
//				"	prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
//					"prefix obo: <http://purl.obolibrary.org/obo/>"+
//					"prefix form: <http://www.geneontology.org/formats/oboInOwl#>"+
//
//					"select *"+
//
//					"Where{"+
//
//					"obo:GO_0008637  rdfs:subClassOf ?o}";
		
		
		
//		Label(repo, q);
//	String	q = "	prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
//			"	prefix owl: <http://www.w3.org/2002/07/owl#>"+
//			"select ?o where{  ?o  rdfs:subClassOf  <http://purl.obolibrary.org/obo/GO_0008637> . }"
//			+ "Limit 10";
		
		
		String	q = "	prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
		"	prefix owl: <http://www.w3.org/2002/07/owl#>"+
		"select ?super ?sub (count(?mid) as ?distance) { "+
			"  ?super rdfs:subClassOf* ?mid ."
+			"  ?mid rdfs:subClassOf+ ?sub."
+			"}"
			+"group by ?super ?sub "
			+"order by ?super ?sub";
		
		
		
		long time_start = System.currentTimeMillis();
//		Label(repo,q);
		long time_end = System.currentTimeMillis();
		System.out.println("Recovering all  distances has taken "+ ( time_end - time_start )*0.001 +" seconds");
		time_start = System.currentTimeMillis();
		time_start = System.currentTimeMillis();
	
	
		
//		q  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
//				"PREFIX go: <http://purl.obolibrary.org/obo/>"+
//				"	prefix owl: <http://www.w3.org/2002/07/owl#>"+
//				
//		"SELECT ?term1"+
//		"WHERE 	{ {"+
//		   " go:GO_0051310  rdfs:subClassOf* ?step1 ."+
//		   " ?step1 owl:someValuesFrom ?term1 ."+
//		  "}"+
//		  "UNION"+
//		  "{    go:GO_0051310  rdfs:subClassOf* ?step1 ."+
//		    "?step1 owl:onProperty go:BFO_0000050 ."+
//		    "?step2 owl:someValuesFrom ?term1 ."+
//		  "}"+
//		  "	}";
		
//		q  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
//				"PREFIX go: <http://purl.obolibrary.org/obo/>\n"+
//				"	prefix owl: <http://www.w3.org/2002/07/owl#>\n"+
//					"SELECT DISTINCT ?term1\n "+
//		
//		"WHERE{ go:GO_0051310 rdfs:subClassOf*\n"+
//		" [ a owl:Restriction ;\n"+
//		"owl:onProperty go:BFO_0000050 ;\n"+
//		 "owl:someValuesFrom ?term1 ] .}\n" ;
		
		
		
		
//		
		q  = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				"PREFIX go: <http://purl.obolibrary.org/obo/>"+
				"	prefix owl: <http://www.w3.org/2002/07/owl#>"+
					"SELECT DISTINCT ?term1 "+
		"{"
		+ "?m rdfs:subClassOf* ?term1 . "+
							  "?term1   a owl:Class"
							 + "}";
		System.out.println("Aqui");
		LCS(repo,q);
		
		time_end = System.currentTimeMillis();
//			System.out.println("Recovering LCS has taken "+ ( time_end - time_start )*0.001 +" seconds");
//		List<String> classes = Sparql.allClass(repo);
//		System.out.println(classes.size());
//		
//		
//		System.out.println(Arrays.asList(classes.get(1).split(";")));
//		
//		time_end = System.currentTimeMillis();
		System.out.println("Recovering all classes has taken "+ ( time_end - time_start )*0.001 +" seconds");

		System.out.println("OK done");
		System.exit(0);
	}
}
