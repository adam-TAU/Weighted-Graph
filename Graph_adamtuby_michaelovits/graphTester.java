
public class graphTester {
    public static void main(String[] args) {
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
        System.out.println(graph.toString());
    }

}
