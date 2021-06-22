

import javafx.util.Pair;
import sun.text.normalizer.UnicodeSetIterator;

import java.util.*;
import java.util.function.BiFunction;

public class graphTester {

    public Graph G;
    public int N;
    public Set<Integer> nodesIds;
    public Map<Integer, Integer> nodesWeights;
    public Set<Pair<Integer, Integer>> edges;
    public int numNodes;
    public int numEdges;
    public Map<Integer, Integer> neighborhoodWeights;
    public Random rnd = new Random();

    public graphTester(int N)
    {
        this.N = N;
        Graph.Node[] nodes = new Graph.Node[N];
        nodesIds = new HashSet<>();
        nodesWeights = new HashMap<>();
        neighborhoodWeights = new HashMap<>();
        for(int i=0;i<N;i++)
        {
            int id;
            do { id = Math.abs(rnd.nextInt(100000)); } while(nodesIds.contains(id));
            int weight = Math.abs(rnd.nextInt(100000));
            nodesIds.add(id);
            nodesWeights.put(id, weight);
            neighborhoodWeights.put(id, weight);
            nodes[i] = new Graph.Node(id, weight);
        }
        this.G = new Graph(nodes);
        this.edges = new HashSet<>();
        this.numNodes = N;
        this.numEdges = 0;
    }

    public boolean addEdge(int v1, int v2)
    {
        if(edges.contains(new Pair<Integer, Integer>(v1, v2)))
        {
            return false;
        }
        G.addEdge(v1, v2);
        neighborhoodWeights.replace(v1, neighborhoodWeights.get(v1) + nodesWeights.get(v2));
        neighborhoodWeights.replace(v2, neighborhoodWeights.get(v2) + nodesWeights.get(v1));
        return true;
    }

    public boolean deleteNode(int v)
    {
        if(!nodesIds.contains(v))
        {
            return false;
        }
        G.deleteNode(v);
        neighborhoodWeights.replaceAll((u, Sw) -> edges.contains(new Pair<>(v,u)) ? Sw - nodesWeights.get(u) : Sw);
        edges.removeIf(p -> p.getKey() == v || p.getValue() == v);
        nodesIds.remove(v);
        nodesWeights.remove(v);
        numNodes = nodesIds.size();
        numEdges = edges.size();
        return true;
    }

    public void randomEdges(int amount)
    {
        Integer[] idsArr = nodesIds.toArray(new Integer[numNodes]);
        for(int i=0;i<amount;i++)
        {
            int v1, v2;

            do {
                v1 = idsArr[rnd.nextInt(numNodes)];
                v2 = idsArr[rnd.nextInt(numNodes)];
            } while(v1 == v2 || edges.contains(new Pair<>(v1, v2)));
            this.addEdge(v1, v2);
        }
        assert !verifyAll();
    }

    public void randomDeletions(int amount)
    {
        HashSet<Integer> deletionIndicesSet = new HashSet<>();
        int[] deletionIndices = new int[amount];
        while(deletionIndicesSet.size() < amount)
        {
            int index;
            do {
                index = rnd.nextInt(numNodes);
            } while(deletionIndicesSet.contains(index));
            deletionIndicesSet.add(index);
            deletionIndices[deletionIndicesSet.size()-1] = index;
        }
        for(int i=0 ; i<amount ; i++) {
            int counter = 0;
            for (int v : nodesIds.toArray(new Integer[numNodes])) {
                if (counter == deletionIndices[i]) {
                    this.deleteNode(v);
                }
                counter++;
            }
        }
        assert !verifyAll();
    }

    public void randomOperations(int amount) // not so random, but kinda...
    {
        randomEdges(amount/6);
        randomDeletions(amount/3 - amount/6);
        randomEdges(amount/2 - amount/3);
        randomDeletions(2*amount/3 - amount/2);
        randomEdges(5*amount/6 - 2*amount/3);
        randomDeletions(amount - 5*amount/6);
        assert !verifyAll();
    }

    public void deleteOneByOne()
    {
        this.randomDeletions(numNodes);
        assert numNodes != 0;
    }

    public void addAllEdgesOneByOne()
    {
        randomEdges(numNodes*(numNodes-1)/2-numEdges);
        assert numEdges != numNodes*(numNodes-1)/2;
    }

    public boolean verifyAll()
    {
        return verifyAmounts() && verifyMaximum();
    }

    public boolean verifyAmounts()
    {
        if (!(numNodes == G.getNumNodes() && numEdges == G.getNumEdges())){
            System.out.println("ERR - Amounts");
            int a = 1/0;
            return false;
        }
        return true;
    }

    public boolean verifyMaximum()
    {
        int maxNeighborhoodWeightNode = 0;
        for(Integer v : nodesIds)
        {
            if(neighborhoodWeights.get(v) > neighborhoodWeights.get(maxNeighborhoodWeightNode))
            {
                maxNeighborhoodWeightNode = v;
            }
        }
        if(maxNeighborhoodWeightNode != G.maxNeighborhoodWeight().getId())
        {
            System.out.println("ERR - Maximum");
            int a = 1/0;
            return false;
        }
        return true;
    }


    public static void main(String[] args){
//        measurements();
        whatsappTest();
        firstTest();
    }




    private static void measurements(){
        Map<Integer, Integer> hashTable = new HashMap<>();

        for (int i = 6; i<=21;i++){
            // for i:
            int n = (int)Math.pow(2,i);
            Graph.Node[] nodes = new Graph.Node[n]; // vertices' array
            for (int j = 1; j <=n;j++ ){  // generating those n vertices
                nodes[j-1] = new Graph.Node(j , 1);
            }
            Graph g = new Graph(nodes); // generating the graph
            int x;
            int y;
            HashMap<Set<Integer>, Integer> hash_table = new HashMap<>(); // hash table used to determine which edge is already in the graph
            for(int k = 0;k<n;k++){ // generating n randomly selected edges
                Random rand = new Random();
                x = rand.nextInt(n)+1; // one vertice of the edge - P = 1/n, בלתי תלוי
                y = rand.nextInt(n)+1; // second vertice of the edge - P = 1/n, בלתי תלוי
                Set<Integer> hash_Set = new HashSet<>(); // the representation of the edge within a hash-set
                hash_Set.add(x);
                hash_Set.add(y);
                if ((!hash_table.containsKey(hash_Set)) && (x!=y)){
                    hash_table.put(hash_Set,1);
                    //System.out.println(hash_Set);
                    g.addEdge(x,y);
                }
            }

            System.out.printf("%n%n%n" + "\033[1m" + "i = %d" + "\033[0m" + "%n", i);
            System.out.printf("there are %d edges in the graph, while there are %d nodes in the graph%n", g.getNumEdges(), g.getNumNodes());
            //System.out.println(g.maxHeap.Heap[1][0]-1);
            Graph.Node maxVertice = g.maxNeighborhoodWeight();
            System.out.printf("the vertice who holds the maximum neighborhood weight is of id: %d, and its rank is: %d%n%n", maxVertice.getId(), maxVertice.getVicinityWeight()-1);
            //System.out.println(g.maxHeap.Heap.length-1);
            hashTable.put(i, maxVertice.getVicinityWeight() - 1);
        }

        System.out.println(hashTable.entrySet());

    }




    private static void whatsappTest(){
        graphTester T;
        // #1:
        System.out.println("\nTest #1: (avg inputs)");
        T = new graphTester(1000);
        T.randomEdges(2000);
        System.out.println("Random edges' insertion succeeded!");
        T.randomDeletions(500);
        System.out.println("Random nodes' deletion succeeded!");
        T.randomOperations(500);
        System.out.println("Random modifications' operation succeeded!");
        // #2:
        System.out.println("\nTest #2: (small inputs)");
        T = new graphTester(50);
        T.randomEdges(200);
        System.out.println("Random edges' insertion succeeded!");
        T.randomDeletions(15);
        System.out.println("Random nodes' deletion succeeded!");
        T.randomOperations(30);
        System.out.println("Random modifications' operation succeeded!");
        T.addAllEdgesOneByOne();
        System.out.println("One by one addition of edges succeeded!");
        T.deleteOneByOne();
        System.out.println("One by one deletion of nodes succeeded!");
        // #3:
        System.out.println("\nTest #3: (big inputs)");
        T = new graphTester(10000);
        T.randomEdges(50000);
        System.out.println("Random edges' insertion succeeded!");
        T.randomDeletions(2000);
        System.out.println("Random nodes' deletion succeeded!");
        T.randomOperations(8000);
        System.out.println("Random modifications' operation succeeded!");
    }


    private static void firstTest(){
        Graph.Node[] nodes = new Graph.Node[10];
        nodes[0] = new Graph.Node(1, 3);
        nodes[1] = new Graph.Node(2, 4);
        nodes[2] = new Graph.Node(3, 1);
        nodes[3] = new Graph.Node(4, 1);
        nodes[4] = new Graph.Node(5, 1);
        nodes[5] = new Graph.Node(6, 9);
        nodes[6] = new Graph.Node(7, 15);
        nodes[7] = new Graph.Node(8, 6);
        nodes[8] = new Graph.Node(9, 111);
        nodes[9] = new Graph.Node(10, 2);

        Graph graph = new Graph(nodes);
        graph.addEdge(9, 10);
        graph.addEdge(9, 1);
        graph.addEdge(9, 5);
        graph.addEdge(2,10);
        graph.addEdge(4,7);
        graph.addEdge(1,3);
        graph.addEdge(7,6);

        System.out.println(graph.toString());
        graph.deleteNode(9);
        System.out.println(graph.toString());
    }

}
