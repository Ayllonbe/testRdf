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
				"GO:0008637","GO:0030239",
				
				"GO:2000483",
				"GO:0050718",
				"GO:0046578",
				"GO:0045648",
				"GO:0002761",
				"GO:0051090",
				"GO:0032727",
				"GO:0006954",
				"GO:0048489",
				"GO:0007409",
				"GO:0032728",
				"GO:0006959",
				"GO:0071375",
				"GO:0032729",
				"GO:0032689",
				"GO:1902042",
				"GO:0006691",
			    "GO:0034341",
				"GO:0050729",
				"GO:0045654",
				"GO:0051260",
				"GO:0045416",
				"GO:0051099",
				"GO:0001558",
				"GO:0043087",
				"GO:0030890",
				"GO:0032675",
				"GO:0030098",
				"GO:0030259",
				"GO:0034976",
				"GO:0055072",
				"GO:0045429",
				"GO:0060421",
				"GO:1905049",
				"GO:0097190",
				"GO:0002460",
				"GO:0007596",
				"GO:0045669",
				"GO:0045821",
				"GO:0043524",
				"GO:0001525",
				"GO:2001242",
				"GO:0090322",
				"GO:0036065",
				"GO:1904646",
				"GO:0048545",
				"GO:0007249",
				"GO:0006953",
				"GO:0001541",
				"GO:0002224",
				"GO:0071260",
				"GO:0001932",
				"GO:0030198",
				"GO:0032496",
				"GO:0001816",
				"GO:0050919",
				"GO:0120036",
				"GO:0050918",
				"GO:0006486",
				"GO:0046777",
				"GO:0045444",
				"GO:0006887",
				"GO:0045722",
				"GO:0002322",
				"GO:0015908",
				"GO:0006639",
				"GO:0043032",
				"GO:0033574",
				"GO:0030100",
				"GO:0006919",
				"GO:2000678",
				"GO:2000279",
				"GO:0034141",
				"GO:0050806",
				"GO:0007584",
				"GO:0045730",
				"GO:0008156",
				"GO:0000302",
				"GO:0007623",
				"GO:0006898",
				"GO:0048208",
				"GO:0007589",
				"GO:0098542",
				"GO:0006888",
				"GO:0032355",
				"GO:0031663",
				"GO:0070873",
				"GO:0031664",
				"GO:0072659",
				"GO:0016567",
				"GO:0008360",
				"GO:0007275",
				"GO:0042116",
				"GO:0044262",
				"GO:0070266",
				"GO:0072602",
				"GO:0034644",
				"GO:0010951",
				"GO:0045070",
				"GO:0016311",
				"GO:0045071",
				"GO:0035336",
				"GO:0015722",
				"GO:0034122",
				"GO:0007283",
				"GO:0048469",
				"GO:1901216",
				"GO:0006876",
				"GO:0006631",
				"GO:0007204",
				"GO:0032736",
				"GO:0031647",
				"GO:0071222",
				"GO:0006906",
				"GO:0019372",
				"GO:0045087",
				"GO:0042493",
				"GO:0006904",
				"GO:0006909",
				"GO:0002309",
				"GO:0034599",
				"GO:0071345",
				"GO:0050702",
				"GO:0071621",
"GO:0008637","GO:0030239",
				
				"GO:2000483",
				"GO:0050718",
				"GO:0046578",
				"GO:0045648",
				"GO:0002761",
				"GO:0051090",
				"GO:0032727",
				"GO:0006954",
				"GO:0048489",
				"GO:0007409",
				"GO:0032728",
				"GO:0006959",
				"GO:0071375",
				"GO:0032729",
				"GO:0032689",
				"GO:1902042",
				"GO:0006691",
			    "GO:0034341",
				"GO:0050729",
				"GO:0045654",
				"GO:0051260",
				"GO:0045416",
				"GO:0051099",
				"GO:0001558",
				"GO:0043087",
				"GO:0030890",
				"GO:0032675",
				"GO:0030098",
				"GO:0030259",
				"GO:0034976",
				"GO:0055072",
				"GO:0045429",
				"GO:0060421",
				"GO:1905049",
				"GO:0097190",
				"GO:0002460",
				"GO:0007596",
				"GO:0045669",
				"GO:0045821",
				"GO:0043524",
				"GO:0001525",
				"GO:2001242",
				"GO:0090322",
				"GO:0036065",
				"GO:1904646",
				"GO:0048545",
				"GO:0007249",
				"GO:0006953",
				"GO:0001541",
				"GO:0002224",
				"GO:0071260",
				"GO:0001932",
				"GO:0030198",
				"GO:0032496",
				"GO:0001816",
				"GO:0050919",
				"GO:0120036",
				"GO:0050918",
				"GO:0006486",
				"GO:0046777",
				"GO:0045444",
				"GO:0006887",
				"GO:0045722",
				"GO:0002322",
				"GO:0015908",
				"GO:0006639",
				"GO:0043032",
				"GO:0033574",
				"GO:0030100",
				"GO:0006919",
				"GO:2000678",
				"GO:2000279",
				"GO:0034141",
				"GO:0050806",
				"GO:0007584",
				"GO:0045730",
				"GO:0008156",
				"GO:0000302",
				"GO:0007623",
				"GO:0006898",
				"GO:0048208",
				"GO:0007589",
				"GO:0098542",
				"GO:0006888",
				"GO:0032355",
				"GO:0031663",
				"GO:0070873",
				"GO:0031664",
				"GO:0072659",
				"GO:0016567",
				"GO:0008360",
				"GO:0007275",
				"GO:0042116",
				"GO:0044262",
				"GO:0070266",
				"GO:0072602",
				"GO:0034644",
				"GO:0010951",
				"GO:0045070",
				"GO:0016311",
				"GO:0045071",
				"GO:0035336",
				"GO:0015722",
				"GO:0034122",
				"GO:0007283",
				"GO:0048469",
				"GO:1901216",
				"GO:0006876",
				"GO:0006631",
				"GO:0007204",
				"GO:0032736",
				"GO:0031647",
				"GO:0071222",
				"GO:0006906",
				"GO:0019372",
				"GO:0045087",
				"GO:0042493",
				"GO:0006904",
				"GO:0006909",
				"GO:0002309",
				"GO:0034599",
				"GO:0071345",
				"GO:0050702",
				"GO:0071621",
				"GO:0090081",
				"GO:0090080",
				"GO:0090079",
				"GO:0090078",
				"GO:0090077",
				"GO:0090076",
				"GO:0090075",
				"GO:0090074",
				"GO:0090073",
				"GO:0090071",
				"GO:0090070",
				"GO:0090069",
				"GO:0090068",
				"GO:0090067",
				"GO:0090066",
				"GO:0090065",
				"GO:0090064",
				"GO:0090063",
				"GO:0090062",
				"GO:0090060",
				"GO:0090059",
				"GO:0090058",
				"GO:0090057",
				"GO:0090056",
				"GO:0090055",
				"GO:0090054",
				"GO:0090053",
				"GO:0090052",
				"GO:0090051",
				"GO:0090050",
				"GO:0090049",
				"GO:0090045",
				"GO:0090044",
				"GO:0090043",
				"GO:0090042",
				"GO:0090038",
				"GO:0090037",
				"GO:0090036",
				"GO:0090035",
				"GO:0090034",
				"GO:0090033",
				"GO:0090032",
				"GO:0090031",
				"GO:0090030",
				"GO:0090029",
				"GO:0090028",
				"GO:0090027",
				"GO:0090026",
				"GO:0090025",
				"GO:0090024",
				"GO:0090023",
				"GO:0090022",
				"GO:0090021",
				"GO:0090020",
				"GO:0090019",
				"GO:0090018",
				"GO:0090017",
				"GO:0090016",
				"GO:0090015",
				"GO:0090014",
				"GO:0090013",
				"GO:0090012",
				"GO:0090011",
				"GO:0090010",
				"GO:0090009",
				"GO:0090008",
				"GO:0090006",
				"GO:0090001",
				"GO:0043699",
				"GO:0043698",
				"GO:0043697",
				"GO:0043696",
				"GO:0043695",
				"GO:0043694",
				"GO:0043693",
				"GO:0043692",
				"GO:0043691",
				"GO:0043690",
				"GO:0043689",
				"GO:0043688",
				"GO:0043687",
				"GO:0043686",
				"GO:0043685",
				"GO:0043684",
				"GO:0043683",
				"GO:0043682",
				"GO:0043680",
				"GO:0043679",
				"GO:0043678",
				"GO:0043677",
				"GO:0043676",
				"GO:0043675",
				"GO:0043674",
				"GO:0043673",
				"GO:0043672",
				"GO:0043671",
				"GO:0043670",
				"GO:0031699",
				"GO:0031698",
				"GO:0031697",
				"GO:0031696",
				"GO:0031695",
				"GO:0031694",
				"GO:0031693",
				"GO:0031692",
				"GO:0031691",
				"GO:0031690",
				"GO:0043669",
				"GO:0043668",
				"GO:0043667",
				"GO:0043666",
				"GO:0043665",
				"GO:0043664",
				"GO:0043663",
				"GO:0043662",
				"GO:0043661",
				"GO:0043660",
				"GO:0031689",
				"GO:0031688",
				"GO:0031687",
				"GO:0031686",
				"GO:0031685",
				"GO:0031683",
				"GO:0031682",
				"GO:0031681",
				"GO:0031680",
				"GO:0043659",
				"GO:0043658",
				"GO:0043657",
				"GO:0043656",
				"GO:0043655",
				"GO:0043654",
				"GO:0043653",
				"GO:0043652",
				"GO:0043651",
				"GO:0043650",
				"GO:0031679",
				"GO:0031676",
				"GO:0031674",
				"GO:0031673",
				"GO:0031672",
				"GO:0031671",
				"GO:0031670",
				"GO:0043649",
				"GO:0043648",
				"GO:0043647",
				"GO:0043646",
				"GO:0043645",
				"GO:0043644",
				"GO:0043643",
				"GO:0043642",
				"GO:0043641",
				"GO:0043640",
				"GO:0031669",
				"GO:0031668",
				"GO:0031667",
				"GO:0031666",
				"GO:0031665",
				"GO:0031664",
				"GO:0031663",
				"GO:0031662",
				"GO:0031661",
				"GO:0031660",
				"GO:0005395",
				"GO:0005391",
				"GO:0043639",
				"GO:0043638",
				"GO:0043637",
				"GO:0043636",
				"GO:0043635",
				"GO:0043634",
				"GO:0043633",
				"GO:0043632",
				"GO:0043631",
				"GO:0043630",
				"GO:0031659",
				"GO:0031658",
				"GO:0031657",
				"GO:0031656",
				"GO:0031655",
				"GO:0031654",
				"GO:0031653",
				"GO:0031652",
				"GO:0031651",
				"GO:0031650",
				"GO:0005388",
				"GO:0005385",
				"GO:0005384",
				"GO:0005381",
				"GO:0043629",
				"GO:0043628",
				"GO:0043627",
				"GO:0043626",
				"GO:0043625",
				"GO:0043624",
				"GO:0043623",
				"GO:0043622",
				"GO:0043621",
				"GO:0043620",
				"GO:0031649",
				"GO:0031648",
				"GO:0031647",
				"GO:0031646",
				"GO:0031645",
				"GO:0031644",
				"GO:0031643",
				"GO:0031642",
				"GO:0031641",
				"GO:0031640",
				"GO:0005375",
				"GO:0005372",
				"GO:0005371",
				"GO:0043619",
				"GO:0043618",
				"GO:0043617",
				"GO:0043616",
				"GO:0043615",
				"GO:0043614",
				"GO:0043613",
				"GO:0043612",
				"GO:0043611",
				"GO:0043610",
				"GO:0031639",
				"GO:0031638",
				"GO:0031637",
				"GO:0031635",
				"GO:0031634",
				"GO:0031633",
				"GO:0031632",
				"GO:0031631",
				"GO:0005369",
				"GO:0031630",
				"GO:0005368",
				"GO:0005367",
				"GO:0005366",
				"GO:0005365",
				"GO:0005364",
				"GO:0005363",
				"GO:0005362",
				"GO:0005360",
				"GO:0043609",
				"GO:0043608",
				"GO:0043607",
				"GO:0043606",
				"GO:0043605",
				"GO:0043604",
				"GO:0043603",
				"GO:0043602",
				"GO:0043601",
				"GO:0043600",
				"GO:0031629",
				"GO:0031628",
				"GO:0031627",
				"GO:0031626",
				"GO:0031625",
				"GO:0031624",
				"GO:0031623",
				"GO:0031622",
				"GO:0031621",
				"GO:0005359",
				"GO:0031620",
				"GO:0005358",
				"GO:0005357",
				"GO:0005356",
				"GO:0005355",
				"GO:0005354",
				"GO:0005353",
				"GO:0005352",
				"GO:0005351",
				"GO:0005350",
				"GO:0031619",
				"GO:0031618",
				"GO:0031617",
				"GO:0031616",
				"GO:0031615",
				"GO:0031613",
				"GO:0031612",
				"GO:0031610",
				"GO:0005347",
				"GO:0005346",
				"GO:0005345",
				"GO:0005344",
				"GO:0005343",
				"GO:0005342",
				"GO:0005340",
				"GO:0031609",
				"GO:0031607",
				"GO:0031606",
				"GO:0031604",
				"GO:0031603",
				"GO:0031601",
				"GO:0005338",
				"GO:0031600",
				"GO:0005337",
				"GO:0005335",
				"GO:0005334",
				"GO:0005333",
				"GO:0005332",
				"GO:0005330",
				"GO:0005329",
				"GO:0005328",
				"GO:0005326",
				"GO:0005325",
				"GO:0005324",
				"GO:0005319",
				"GO:0005316",
				"GO:0005315",
				"GO:0005314",
				"GO:0005313",
				"GO:0005310",
				"GO:0005309",
				"GO:0005308",
				"GO:0005307",
				"GO:0005304",
				"GO:0005302",
				"GO:0005300",
				"GO:0043599",
				"GO:0043598",
				"GO:0043597",
				"GO:0043596",
				"GO:0043595",
				"GO:0043594",
				"GO:0043593",
				"GO:0043592",
				"GO:0043591",
				"GO:0043590",
				"GO:0043589",
				"GO:0043588",
				"GO:0043587",
				"GO:0043586",
				"GO:0043585",
				"GO:0043584",
				"GO:0043583",
				"GO:0043582",
				"GO:0043580",
				"GO:0043579",
				"GO:0043578",
				"GO:0043577",
				"GO:0043576",
				"GO:0043575",
				"GO:0043574",
				"GO:0043573",
				"GO:0043572",
				"GO:0043571",
				"GO:0043570",
				"GO:0031598",
				"GO:0031597",
				"GO:0031595",
				"GO:0031594",
				"GO:0031593",
				"GO:0031592",
				"GO:0031591",
				"GO:0031590",
				"GO:0043569",
				"GO:0043568",
				"GO:0043567",
				"GO:0043565",
				"GO:0043564",
				"GO:0043563",
				"GO:0043562",
				"GO:0043561",
				"GO:0043560",
				"GO:0031589",
				"GO:0031588",
				"GO:0031587",
				"GO:0031586",
				"GO:0031585",
				"GO:0031584",
				"GO:0031583",
				"GO:0031582",
				"GO:0031581",
				"GO:0031580",
				"GO:0043559",
				"GO:0043558",
				"GO:0043557",
				"GO:0043556",
				"GO:0043555",
				"GO:0043554",
				"GO:0043553",
				"GO:0043552",
				"GO:0043551",
				"GO:0043550",
				"GO:0031579",
				"GO:0031578",
				"GO:0031577",
				"GO:0031573",
				"GO:0031572",
				"GO:0031571",
				"GO:0031570",
				"GO:0043549",
				"GO:0043548",
				"GO:0043547",
				"GO:0043546",
				"GO:0043545",
				"GO:0043544",
				"GO:0043543",
				"GO:0043542",
				"GO:0043541",
				"GO:0043540",
				"GO:0031569",
				"GO:0031568",
				"GO:0031567",
				"GO:0031566",
				"GO:0031564",
				"GO:0031563",
				"GO:0031562",
				"GO:0101031",
				"GO:0101030",
				"GO:0031561",
				"GO:0005298",
				"GO:0031560",
				"GO:0005297",
				"GO:0005295",
				"GO:0005294",
				"GO:0005292",
				"GO:0005291",
				"GO:0005290",
				"GO:0043539",
				"GO:0043538",
				"GO:0043537",
				"GO:0043536",
				"GO:0043535",
				"GO:0043534",
				"GO:0043533",
				"GO:0043532",
				"GO:0043531",
				"GO:0043530",
				"GO:0101029",
				"GO:0101028",
				"GO:0031559",
				"GO:0101027",
				"GO:0101026",
				"GO:0031556",
				"GO:0101025",
				"GO:0031555",
				"GO:0101024",
				"GO:0031554",
				"GO:0101023",
				"GO:0031553",
				"GO:0031552",
				"GO:0101021",
				"GO:0031551",
				"GO:0005289",
				"GO:0101020",
				"GO:0031550",
				"GO:0005287",
				"GO:0005283",
				"GO:0005280",
				"GO:0043529",
				"GO:0043528",
				"GO:0043527",
				"GO:0043525",
				"GO:0043524",
				"GO:0043523",
				"GO:0043522",
				"GO:0043521",
				"GO:0043520",
				"GO:0101019",
				"GO:0031549",
				"GO:0101018",
				"GO:0031548",
				"GO:0101017",
				"GO:0031547",
				"GO:0101016",
				"GO:0031546",
				"GO:0031545",
				"GO:0101014",
				"GO:0031544",
				"GO:0101013",
				"GO:0031543",
				"GO:0101012",
				"GO:0031542",
				"GO:0101011",
				"GO:0031541",
				"GO:0101010",
				"GO:0031540",
				"GO:0005278",
				"GO:0005277",
				"GO:0005276",
				"GO:0005275",
				"GO:0005274",
				"GO:0005272",
				"GO:0043519",
				"GO:0043518",
				"GO:0043517",
				"GO:0043516",
				"GO:0043515",
				"GO:0043514",
				"GO:0043513",
				"GO:0043512",
				"GO:0043511",
				"GO:0043510",
				"GO:0031539",
				"GO:0101008",
				"GO:0031538",
				"GO:0101007",
				"GO:0031537",
				"GO:0101006",
				"GO:0031536",
				"GO:0101005",
				"GO:0031535",
				"GO:0101004",
				"GO:0031534",
				"GO:0101003",
				"GO:0031533",
				"GO:0101002",
				"GO:0031532",
				"GO:0031531",
				"GO:0031530",
				"GO:0005267",
				"GO:0005262",
				"GO:0005261",
				"GO:0005260",
				"GO:0043509",
				"GO:0043508",
				"GO:0043507",
				"GO:0043506",
				"GO:0043505",
				"GO:0043504",
				"GO:0043503",
				"GO:0043502",
				"GO:0043501",
				"GO:0043500",
				"GO:0031529",
				"GO:0031528",
				"GO:0031527",
				"GO:0031526",
				"GO:0031525",
				"GO:0031524",
				"GO:0031523",
				"GO:0031522",
				"GO:0031521",
				"GO:0031520",
				"GO:0005254",
				"GO:0005253",
				"GO:0005252",
				"GO:0005251",
				"GO:0005250",
				"GO:0031519",
				"GO:0031518",
				"GO:0031517",
				"GO:0031516",
				"GO:0031515",
				"GO:0031514",
				"GO:0005249",
				"GO:0031511",
				"GO:0005248",
				"GO:0031510",
				"GO:0005247",
				"GO:0005246",
				"GO:0005245",
				"GO:0005244",
				"GO:0005243",
				"GO:0005242",
				"GO:0031509",
				"GO:0031508",
				"GO:0031507",
				"GO:0031506",
				"GO:0031505",
				"GO:0031504",
				"GO:0031503",
				"GO:0031502",
				"GO:0031501",
				"GO:0031500",
				"GO:0005237",
				"GO:0005234",
				"GO:0005231",
				"GO:0005230",
				"GO:0005229",
				"GO:0005228",
				"GO:0005227",
				"GO:0005225",
				"GO:0005223",
				"GO:0005222",
				"GO:0005221",
				"GO:0005220",
				"GO:0005219",
				"GO:0005217",
				"GO:0005216",
				"GO:0005215",
				"GO:0005214",
				"GO:0005213",
				"GO:0005212",
				"GO:0005201",
				"GO:0005200",
				"GO:0043497",
				"GO:0043496",
				"GO:0043495",
				"GO:0043494",
				"GO:0043493",
				"GO:0043492",
				"GO:0043491",
				"GO:0043490",
				"GO:0043489",
				"GO:0043488",
				"GO:0043487",
				"GO:0043486",
				"GO:0043485",
				"GO:0043484",
				"GO:0043483",
				"GO:0043482",
				"GO:0043481",
				"GO:0043480",
				"GO:0043479",
				"GO:0043478",
				"GO:0043477",
				"GO:0043476",
				"GO:0043475",
				"GO:0043474",
				"GO:0043473",
				"GO:0043472",
				"GO:0043471",
				"GO:0043470",
				"GO:0031499",
				"GO:0031498",
				"GO:0031497",
				"GO:0031496",
				"GO:0031495",
				"GO:0031494",
				"GO:0031493",
				"GO:0031492",
				"GO:0031491",
				"GO:0031490",
				"GO:0043469",
				"GO:0043468",
				"GO:0043467",
				"GO:0043466",
				"GO:0043465",
				"GO:0043464",
				"GO:0043463",
				"GO:0043462",
				"GO:0017199",
				"GO:0043461",
				"GO:0017198",
				"GO:0017197",
				"GO:0017196",
				"GO:0031489",
				"GO:0017195",
				"GO:0031488",
				"GO:0017194",
				"GO:0031487",
				"GO:0017193",
				"GO:0031486",
				"GO:0017192",
				"GO:0031485",
				"GO:0031484",
				"GO:0017190",
				"GO:0031483",
				"GO:0031482",
				"GO:0031481",
				"GO:0031480",
				"GO:0043458",
				"GO:0043457",
				"GO:0043456",
				"GO:0043455",
				"GO:0043454",
				"GO:0043453",
				"GO:0043452",
				"GO:0017189",
				"GO:0043451",
				"GO:0043450",
				"GO:0017188",
				"GO:0017187",
				"GO:0017186",
				"GO:0031479",
				"GO:0017185",
				"GO:0031478",
				"GO:0017184",
				"GO:0031477",
				"GO:0017183",
				"GO:0031476",
				"GO:0017182",
				"GO:0031475",
				"GO:0017181",
				"GO:0031474",
				"GO:0017180",
				"GO:0031473",
				"GO:0031472",
				"GO:0031471",
				"GO:0031470",
				"GO:0043449",
				"GO:0043448",
				"GO:0043447",
				"GO:0043446",
				"GO:0043445",
				"GO:0043444",
				"GO:0043443",
				"GO:0043442",
				"GO:0017179",
				"GO:0043441",
				"GO:0017178",
				"GO:0017177",
				"GO:0017176",
				"GO:0031469",
				"GO:0031468",
				"GO:0017174",
				"GO:0031467",
				"GO:0031466",
				"GO:0017172",
				"GO:0031465",
				"GO:0017171",
				"GO:0031464",
				"GO:0031463",
				"GO:0031462",
				"GO:0005199",
				"GO:0031461",
				"GO:0031460",
				"GO:0005198",
				"GO:0043438",
				"GO:0043436",
				"GO:0043435",
				"GO:0043434",
				"GO:0043433",
				"GO:0017169",
				"GO:0043431",
				"GO:0017168",
				"GO:0043430",
				"GO:0017166",
				"GO:0031459",
				"GO:0031458",
				"GO:0031457",
				"GO:0031456",
				"GO:0031455",
				"GO:0017162",
				"GO:0031454",
				"GO:0017161",
				"GO:0031453",
				"GO:0017160",
				"GO:0031452",
				"GO:0031451",
				"GO:0031450",
				"GO:0005186",
				"GO:0005185",
				"GO:0005184",
				"GO:0005183",
				"GO:0043429",
				"GO:0043428",
				"GO:0043427",
				"GO:0043426",
				"GO:0043425",
				"GO:0043424",
				"GO:0043423",
				"GO:0043422",
				"GO:0043421",
				"GO:0017159",
				"GO:0017158",
				"GO:0043420",
				"GO:0017157",
				"GO:0017156",
				"GO:0031449",
				"GO:0031448",
				"GO:0031447",
				"GO:0017154",
				"GO:0031446",
				"GO:0017153",
				"GO:0031445",
				"GO:0031444",
				"GO:0017151",
				"GO:0031443",
				"GO:0017150",
				"GO:0031442",
				"GO:0031441",
				"GO:0005179",
				"GO:0031440",
				"GO:0005178",
				"GO:0005176",
				"GO:0005175",
				"GO:0005174",
				"GO:0005173",
				"GO:0005172",
				"GO:0005171",
				"GO:0005170",
				"GO:0043419",
				"GO:0043418",
				"GO:0043417",
				"GO:0043416",
				"GO:0043415",
				"GO:0043414",
				"GO:0043413",
				"GO:0043412",
				"GO:0017148",
				"GO:0043410",
				"GO:0017147",
				"GO:0031439",
				"GO:0017146",
				"GO:0017145",
				"GO:0031438",
				"GO:0017144",
				"GO:0031437",
				"GO:0017143",
				"GO:0031436",
				"GO:0031435",
				"GO:0031434",
				"GO:0031433",
				"GO:0031432",
				"GO:0005169",
				"GO:0031431",
				"GO:0005168",
				"GO:0031430",
				"GO:0005167",
				"GO:0005166",
				"GO:0005165",
				"GO:0005164",
				"GO:0005163",
				"GO:0005161",
				"GO:0005160",
				"GO:0043409",
				"GO:0043408",
				"GO:0043407",
				"GO:0043406",
				"GO:0043405",
				"GO:0043404",
				"GO:0043403",
				"GO:0043402",
				"GO:0043401",
				"GO:0043400",
				"GO:0017137",
				"GO:0017136",
				"GO:0031429",
				"GO:0031428",
				"GO:0031427",
				"GO:0017134",
				"GO:0031426",
				"GO:0017133",
				"GO:0031425",
				"GO:0017132",
				"GO:0031424",
				"GO:0017131",
				"GO:0017130",
				"GO:0031423",
				"GO:0031422",
				"GO:0005159",
				"GO:0031421",
				"GO:0005158",
				"GO:0031420",
				"GO:0005157",
				"GO:0005154",
				"GO:0005153",
				"GO:0005152",
				"GO:0005151",
				"GO:0005150",
				"GO:0017129",
				"GO:0017128",
				"GO:0017127",
				"GO:0017126",
				"GO:0031419",
				"GO:0017125",
				"GO:0031418",
				"GO:0017124",
				"GO:0031417",
				"GO:0031416",
				"GO:0017122",
				"GO:0031415",
				"GO:0017121",
				"GO:0031414",
				"GO:0031413",
				"GO:0031412",
				"GO:0005149",
				"GO:0031411",
				"GO:0005148",
				"GO:0031410",
				"GO:0005147",
				"GO:0005146",
				"GO:0005145",
				"GO:0005144",
				"GO:0005143",
				"GO:0005142",
				"GO:0005141",
				"GO:0005140",
				"GO:0017119",
				"GO:0017118",
				"GO:0017117",
				"GO:0017116",
				"GO:0031409",
				"GO:0031408",
				"GO:0031407",
				"GO:0017113",
				"GO:0031406",
				"GO:0017112",
				"GO:0031405",
				"GO:0017111",
				"GO:0031404",
				"GO:0017110",
				"GO:0031403",
				"GO:0031402",
				"GO:0031401",
				"GO:0005139",
				"GO:0031400",
				"GO:0005138",
				"GO:0005137",
				"GO:0005136",
				"GO:0005135",
				"GO:0005134",
				"GO:0005133",
				"GO:0005132",
				"GO:0005131",
				"GO:0005130",
				"GO:0017109",
				"GO:0017108",
				"GO:0017107",
				"GO:0017105",
				"GO:0017103",
				"GO:0017102",
				"GO:0017101",
				"GO:0005129",
				"GO:0005128",
				"GO:0005127",
				"GO:0005126",
				"GO:0005125",
				"GO:0005124",
				"GO:0005123",
				"GO:0005122",
				"GO:0005121",
				"GO:0005119",
				"GO:0005118",
				"GO:0005117",
				"GO:0005115",
				"GO:0005114",
				"GO:0005113",
				"GO:0005112",
				"GO:0005111",
				"GO:0005110",
				"GO:0005109",
				"GO:0005105",
				"GO:0005104",
				"GO:0005102",
				"GO:0043399",
				"GO:0043398",
				"GO:0043397",
				"GO:0043396",
				"GO:0043395",
				"GO:0043394",
				"GO:0043393",
				"GO:0043392",
				"GO:0043391",
				"GO:0043390",
				"GO:0043388",
				"GO:0043387",
				"GO:0043386",
				"GO:0043385",
				"GO:0043384",
				"GO:0043383",
				"GO:0043382",
				"GO:0043381",
				"GO:0043380",
				"GO:0043379",
				"GO:0043378",
				"GO:0043377",
				"GO:0043376",
				"GO:0043375",
				"GO:0043374",
				"GO:0043373",
				"GO:0043372",
				"GO:0043371",
				"GO:0043370",
				"GO:0031399",
				"GO:0031398",
				"GO:0031397",
				"GO:0031396",
				"GO:0031395",
				"GO:0031394",
				"GO:0031393",
				"GO:0031392",
				"GO:0031391",
				"GO:0031390",
				"GO:0043369",
				"GO:0043368",
				"GO:0043367",
				"GO:0043366",
				"GO:0043365",
				"GO:0043364",
				"GO:0043363",
				"GO:0043362",
				"GO:0017099",
				"GO:0017098",
				"GO:0017096",
				"GO:0031389",
				"GO:0031388",
				"GO:0017095",
				"GO:0031387",
				"GO:0031386",
				"GO:0031385",
				"GO:0031384",
				"GO:0017091",
				"GO:0031383",
				"GO:0017090",
				"GO:0031382",
				"GO:0031381",
				"GO:0031380",
				"GO:0043354",
				"GO:0043353",
				"GO:0017089",
				"GO:0017087",
				"GO:0017086",
				"GO:0031379",
				"GO:0017085",
				"GO:0017084",
				"GO:0017083",
				"GO:0017082",
				"GO:0017081",
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
//			sb.append("go:"+t.replace(":", "_") + ", ");
			sb.append("\""+t + "\", ");
//			if(count>-1) {
//				break;
//			}
			count++;
		}
		
		sb.delete(sb.length()-2, sb.length());
//		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
//				"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
//				"prefix owl: <http://www.w3.org/2002/07/owl#> \n" + 
//				"PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>\n"+
//				"SELECT ?s1 ?s2\n" + 
//				"WHERE{\n" + 
//				"  FILTER(?2eGO IN ("+sb.toString()+"))\n" + 
//				"?2eGO oboInOwl:id ?s1.\n"+
//				"  ?2eGO a owl:Class.\n" + 
//				"?_restriction oboInOwl:id ?s2.\n"+
//				"  ?_restriction a owl:Class.\n" + 
//				"  ?2eGO  rdfs:subClassOf* ?_restriction . \n" + 
//				
//				" \n" + 
//				"  }" ;
		
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"			PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
				"			prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"			PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>\n" + 
				"			SELECT ?uri \n" + 
				"WHERE{\n" + 
				"  Filter (?id IN ("+sb.toString()+"))\n" + 
				"  			?uri a owl:Class. \n" + 
				"			?uri  oboInOwl:id ?id.\n" + 
				"           \n" + 
				
				"                \n" + 
				"            }";
		
		
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
				Value t1 = bs.getValue("uri");
//				Value t2 = bs.getValue("s2");
				
			test1.add(t1.stringValue());
			System.out.println(t1.stringValue());

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
