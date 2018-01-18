

import java.net.URL;

import org.apache.log4j.Logger;

import com.bigdata.blueprints.BigdataGraph;
import com.bigdata.blueprints.BigdataGraphClient;
import com.bigdata.rdf.sail.remote.BigdataSailRemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepository;
import com.bigdata.rdf.sail.webapp.client.RemoteRepositoryManager;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;


public class SampleBlazegraphBlueprintsRemote {
	
	protected static final Logger log = Logger.getLogger(SampleBlazegraphBlueprintsRemote.class);

	public static void main(String[] args) throws Exception {
String sparqlEndpoint = new URL("http", "localhost", 8080, "/bigdata").toExternalForm();
		
		RemoteRepositoryManager repoManager = new RemoteRepositoryManager(sparqlEndpoint);
        RemoteRepository remoteRepo = repoManager.getRepositoryForNamespace("aaronbase");
        BigdataSailRemoteRepository repo = remoteRepo.getBigdataSailRemoteRepository();
        repo.initialize();
		final BigdataGraph graph = new BigdataGraphClient(repo);
		
		
		
		
//		try {
//			graph.loadGraphML(SampleBlazegraphBlueprintsRemote.class.getResource("/graph-example-1.xml").getFile());
//			for (Vertex v : graph.getVertices()) {
//				log.info(v);
//			}
//			for (Edge e : graph.getEdges()) {
//				log.info(e);
//			}
//		} finally {
//			graph.shutdown();
//		}
        for (Vertex v : graph.getVertices()) {
		log.info(v);
		System.out.println(v.toString());
	}
	for (Edge e : graph.getEdges()) {
		log.info(e);
	}
	System.exit(0);
	}
}