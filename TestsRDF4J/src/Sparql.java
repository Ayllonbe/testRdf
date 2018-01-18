import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;

public class Sparql {


	RemoteRepositoryManager repoManager ;
    RemoteRepository remoteRepo ; // defautnamespace de blazegraph
    BigdataSailRemoteRepository repo ;
	public Sparql() throws Exception {
//		Repository repo = new SPARQLRepository(sparqlEndpoint);
		
		String sparqlEndpoint = new URL("http", "localhost", 9000, "/bigdata").toExternalForm();
		
		 repoManager = new RemoteRepositoryManager(sparqlEndpoint);
        remoteRepo = repoManager.getRepositoryForNamespace("aaronbase"); // defautnamespace de blazegraph
        repo = remoteRepo.getBigdataSailRemoteRepository();
        
		repo.initialize();}
	public static List<String> allClass(BigdataSailRemoteRepository repo) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		
		String query = 
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX owl:  <http://www.w3.org/2002/07/owl#>"+
		    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
			"PREFIX obo:	<http://www.geneontology.org/formats/oboInOwl#>"+
			"SELECT * "+
			"WHERE { "+
			"?s rdf:type owl:Class . "	+
			"?s rdfs:label ?p . "+
			"?s obo:id ?i "+
			"filter not exists{?s owl:deprecated ?o}"+
			"}";
		
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		TupleQueryResult t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		//prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		try {
			List<String> res = new ArrayList<String>();
			while (t.hasNext()) {
				BindingSet bs = t.next();

				Value s = bs.getValue("s");
				Value p = bs.getValue("p");
				Value i = bs.getValue("i");
				res.add(s.stringValue() + ";"+p.stringValue()+";"+i.stringValue());
				//System.out.println(s.stringValue() + " "+s);

			}
			return res;
		} finally {
			t.close();
			conn.close();
		}
	}
	
public static List<String> superClass(BigdataSailRemoteRepository repo,String term) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		
		String query = 
		    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
			"PREFIX owl:  <http://www.w3.org/2002/07/owl#>"+
		    "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
			"PREFIX obo:	<http://www.geneontology.org/formats/oboInOwl#>"+
			"SELECT * "+
			"WHERE { "+
			"<"+term+"> rdfs:subClassOf ?o . "	+
			"?s rdfs:label ?p . "+
			"filter not exists{?s owl:deprecated ?o}"+
			"}";
		
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		TupleQueryResult t = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, query).evaluate();
		//prepareTupleQuery(QueryLanguage.SPARQL, query).evaluate();
		try {
			List<String> res = new ArrayList<String>();
			while (t.hasNext()) {
				BindingSet bs = t.next();
				Value s = bs.getValue("o");
				res.add(s.stringValue());
				//System.out.println(s.stringValue() + " "+s);

			}
			return res;
		} finally {
			t.close();
			conn.close();
		}
	}
	
public static final Map<String, RDFFormat> transcodeMime2RDFFormat;
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

			
			Value o = bs.getValue("o");
			
			System.out.println(o.stringValue());

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

//public static void importFILErdf(Repository repo, String namespace, File f, String mimeType, IRI...iris) throws RDFParseException, RepositoryException, IOException {
//	RepositoryConnection conn = repo.getConnection();
//	
//	try {
//		conn.add(f, namespace, transcodeMime2RDFFormat.get(mimeType), iris);
//	} finally {
//		if (conn != null) {
//			try {
//				conn.close(); 
//			} catch (final RepositoryException ignore) { 
//				
//			}
//		}
//	}
//}
//
//public static void importURLrdf(Repository repo, String namespace, URL u, String mimeType, IRI...iris) throws RDFParseException, RepositoryException, IOException {
//	RepositoryConnection conn = repo.getConnection();
//
//	try {
//		conn.add(u, namespace, transcodeMime2RDFFormat.get(mimeType), iris);
//	} finally {
//		if (conn != null) {
//			try {
//				conn.close(); 
//			} catch (final RepositoryException ignore) { 
//				
//			}
//		}
//	}
//}

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
	
}
