import java.math.BigInteger;
import java.net.URL;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQueryResult;

import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepositoryConnection;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.google.common.primitives.Doubles;

public class TESTONTOAPI {

	


	
	public static void main(String[] args) throws Exception {
		
		String sparqlEndpoint = new URL("http", "localhost", 8080, "/bigdata").toExternalForm();
		
		RemoteRepositoryManager repoManager = new RemoteRepositoryManager(sparqlEndpoint);
        RemoteRepository remoteRepo = repoManager.getRepositoryForNamespace("aaronbase"); // defautnamespace de blazegraph
        BigdataSailRemoteRepository repo = remoteRepo.getBigdataSailRemoteRepository();
        
		repo.initialize();
		
		BigdataSailRemoteRepositoryConnection conn = repo.getConnection();
		// ADD ONTOLOGY
		/*
		 * Il faut uniquement faire un graph en update de tel style:
		 * INSERT {
  GRAPH <http://aaronMETAdata> {
  	<http://geneontology> aaron:curVersion <http://gov1> .  
  }
}
WHERE {
  GRAPH <http://aaronMETAdata> {
  	<http://geneontology> aaron:curVersion ?toto .  
  }
}
		 * DONC <http://gov1> c'est un valeur String aleatoire (pe. la date.) 
		 * Quand on aura une nouvelle version il faut l'ajouter dans triple store avec une nouvelle nom (conn.add(FILE, String(vide),RDF,valueFactory.createURI(http://geneOnto/go-13-12-2017.owl"))
		 * en suite on refait la query precedent dans UPDATE et en suite on ecrase la owl ancienne. 
		 * 
		 * Et le code suivant c'est un exemple de requete et lui bouge pas : 
		 * 
SELECT * 
WHERE {
  GRAPH <http://aaronMETAdata> {
	<http://geneontology> aaron:curVersion ?toto .  
  } .
  GRAPH ?toto {
      ?2eGO a owl:Class.
  	?2eGO oboInOwl:id ?s.
  ?_restriction a owl:Class.
  ?2eGO  rdfs:subClassOf* ?_restriction . 
  }
  
}
		 */
		/*
		 * try { *
		  conn.add(new File("/home/aaron/Documents/Thesis_Project/Data/ontology/go-13-12-2017.owl"), "", RDFFormat.RDFXML, valueFactory.createURI(http://geneOnto/go-13-12-2017.owl"));
		 * System.out.println("Charged");
		 * } catch (RDFParseException e) {
		 * // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * } catch (IOException e) {
		 * // TODO Auto-generated catch block
		 * e.printStackTrace();
		 * }
		 * */
		
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
				"GO:0071621"
};
		
		Set<String> set = new HashSet<>(Arrays.asList(terms));
		System.out.println(set.size());
		long s = System.currentTimeMillis();

		
		StringBuilder sb = new StringBuilder();
		for(String t : set) {
			sb.append("go:"+t.replace(":", "_") + ", ");
		}
		
		sb.delete(sb.length()-2, sb.length());
		String query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
				"prefix owl: <http://www.w3.org/2002/07/owl#> \n" + 
				"SELECT ?s1 ?s2\n" + 
				"WHERE{\n" + 
				"  FILTER(?2eGO IN ("+sb.toString()+"))\n" + 
				"?2eGO rdfs:label ?s1.\n"+
				"  ?2eGO a owl:Class.\n" + 
				"?_restriction rdfs:label ?s2.\n"+
				"  ?_restriction a owl:Class.\n" + 
				"  ?2eGO  rdfs:subClassOf* ?_restriction . \n" + 
				
				" \n" + 
				"  }" ;
		
		
		query = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
		"PREFIX go: <http://purl.obolibrary.org/obo/>\n" + 
		"prefix owl: <http://www.w3.org/2002/07/owl#>\n" + 
		"PREFIX oboInOwl: <http://www.geneontology.org/formats/oboInOwl#>"+
		"SELECT ?s1 ?s2\n" + 
		" WHERE{\n" + 
		"?2eGO oboInOwl:id  ?s1.\n"+
		"?2eGO a owl:Class.\n" + 
		"?_restriction oboInOwl:id  ?s2.\n"+
		"?_restriction a owl:Class.\n" + 
		"           ?2eGO go:partOf* ?_restriction . \n" + 
		"  FILTER(?2eGO IN ("+sb.toString()+"))\n" + 
		" \n" + 
		"  }" ;
		
		
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
		}
		System.out.println(test1.size());
		System.out.println(test2.size());
		System.out.println(new Random().doubles().findAny().getAsDouble());
		long d = new Random().longs().findAny().getAsLong();
		System.out.println(Long.toHexString(d));
		long e = System.currentTimeMillis();
		
		System.out.println((e-s) * 0.001);
	System.exit(0);

	}
}
