import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import scala.Int;

import java.io.*;
import java.util.*;

class AlgorithmQuality{
    static ProjectController pc;
    static Workspace workspace;
    public static void main(String [] args) throws IOException {
        pc = Lookup.getDefault().lookup(ProjectController.class);
        getCliqueStatistics();
    }
    static void getClustersStatistics() throws FileNotFoundException {
        DirectedGraph graph = getGraph(10, "-cluster");
        Map<Integer, Set<Node>> communities = getCommunities(graph,5);
        double F_ODF = F_ODF(communities,graph);
        System.out.println("F-ODF = " + F_ODF);
    }
    static void getCliqueStatistics() throws IOException {
        DirectedGraph graph = getGraph(80,"-clique");
        Map<String, Integer> nodes = getData(30);
        double oRatio = ORatio(nodes,graph);
        System.out.println(oRatio);
    }
    static DirectedGraph getGraph(int k, String fileName) throws FileNotFoundException {
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
        File file = new File("data/" + k + fileName + ".gexf");

        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        container = importController.importFile(file);
        container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);
        importController.process(container, new DefaultProcessor(), workspace);
        final GraphModel graphModel = Lookup.getDefault()
                .lookup(GraphController.class).getGraphModel();
        DirectedGraph directedGraph = graphModel.getDirectedGraph();


        return directedGraph;

    }
    static Map<Integer, Set<Node>> getCommunities(DirectedGraph directedGraph,int index){
        Map<Integer, Set<Node>>  communities = new HashMap<>();
        Set<Integer> communitiesSet = new HashSet<>();
        for (Node node: directedGraph.getNodes()){
            Integer communityId = Integer.valueOf(node.getAttributes()[index].toString());
            communitiesSet.add(communityId);
        }
        for (Integer community: communitiesSet){
            communities.put(community, new HashSet<Node>());
        }
        for (Node node: directedGraph.getNodes()){
            Integer communityId = Integer.valueOf(node.getAttributes()[index].toString());
            communities.get(communityId).add(node);
        }
        return communities;
    }
    static double F_ODF(Map<Integer, Set<Node>> communities, DirectedGraph graph){

        double F_ODF = 0;
        //для каждого сообщества
        for (Map.Entry<Integer, Set<Node>> community: communities.entrySet()){
            int listForSum = 0;
            //для каждого u
            for (Node node: community.getValue()){
                //получить количество исходящих ребер
                Collection<Node> neighbours = graph.getSuccessors(node).toCollection();
                double D_out = neighbours.size() / 2;
                int col = 0;
                for (Node another_node: neighbours){
                    if (!community.getValue().contains(another_node)){
                        col += 1;
                    }
                }
                if (col < D_out){
                    listForSum += 1;
                }
            }
            F_ODF += (double) listForSum / (double) community.getValue().size();

        }
        return F_ODF;
    }

    static Map<String, Integer> getData(int k) throws IOException {

        Map<String, Integer> nodeCommunities = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(
                "data/"+k+"-cliques.csv"));

        // считываем построчно
        String line = null;
        Scanner scanner = null;
        int index = 0;


        while ((line = reader.readLine()) != null) {

            scanner = new Scanner(line);

                String data = line;
                String nodeId = data.split(";")[0];
                if (nodeId.equals("nodeId"))
                    continue;
                int count = data.split(";")[1].split(",").length;
                nodeCommunities.put(nodeId,count);

        }

        //закрываем наш ридер
        reader.close();
        return nodeCommunities;

    }
    static double ORatio(Map<String, Integer> nodeCommunities, DirectedGraph graph){
        double oRatio = 0.0;
        for (Map.Entry<String, Integer> node: nodeCommunities.entrySet()){
            oRatio += (double)node.getValue()/(double)graph.getNodeCount();
        }
        return oRatio;
    }
}
