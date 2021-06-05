/*
You must NOT change the signatures of classes/methods in this skeleton file.
You are required to implement the methods of this skeleton file according to the requirements.
You are allowed to add classes, methods, and members as required.
 */

import java.util.HashMap;
import java.util.Random;

/**
 * This class represents a graph that efficiently maintains the heaviest neighborhood over edge addition and
 * vertex deletion.
 *
 */
public class Graph {
    private HashMap<Node> Nodes;
    /**
     * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
     * You may assume that the ids of distinct nodes are distinct.
     *
     * @param nodes - an array of node objects
     */
    public Graph(Node [] nodes){
        Nodes = new HashMap<>(nodes, 1);
        //TODO: implement this method.
    }

    /**
     * This method returns the node in the graph with the maximum neighborhood weight.
     * Note: nodes that have been removed from the graph using deleteNode are no longer in the graph.
     * @return a Node object representing the correct node. If there is no node in the graph, returns 'null'.
     */
    public Node maxNeighborhoodWeight(){
        //TODO: implement this method.
        return null;
    }

    /**
     * given a node id of a node in the graph, this method returns the neighborhood weight of that node.
     *
     * @param node_id - an id of a node.
     * @return the neighborhood weight of the node of id 'node_id' if such a node exists in the graph.
     * Otherwise, the function returns -1.
     */
    public int getNeighborhoodWeight(int node_id){
        //TODO: implement this method.
        return 0;
    }

    /**
     * This function adds an edge between the two nodes whose ids are specified.
     * If one of these nodes is not in the graph, the function does nothing.
     * The two nodes must be distinct; otherwise, the function does nothing.
     * You may assume that if the two nodes are in the graph, there exists no edge between them prior to the call.
     *
     * @param node1_id - the id of the first node.
     * @param node2_id - the id of the second node.
     * @return returns 'true' if the function added an edge, otherwise returns 'false'.
     */
    public boolean addEdge(int node1_id, int node2_id){
        //TODO: implement this method.
        return false;
    }

    /**
     * Given the id of a node in the graph, deletes the node of that id from the graph, if it exists.
     *
     * @param node_id - the id of the node to delete.
     * @return returns 'true' if the function deleted a node, otherwise returns 'false'
     */
    public boolean deleteNode(int node_id){
        //TODO: implement this method.
        return false;
    }


    /**
     * This class represents a node in the graph.
     */
    public class Node{
        private int id;
        private long weight;
        private DoublyLinkedList<Node> Neighbours;

        /**
         * Creates a new node object, given its id and its weight.
         * @param id - the id of the node.
         * @param weight - the weight of the node.
         */
        public Node(int id, int weight){
            this.id = id;
            this.weight = weight;
            Neighbours = new DoublyLinkedList<>();
        }

        /**
         * Returns the id of the node.
         * @return the id of the node.
         */
        public int getId(){
            return id;
        }

        /**
         * Returns the weight of the node.
         * @return the weight of the node.
         */
        public int getWeight(){
            //TODO: implement this method.
            return 0;
        }

        /**
         * Updates the weight of the Node in case a new Neightbour was added
         */
        public void updateWeight(){
            // update the weight of the node
        }
    }

    public class Edge{
        private int i;
        private int j;

        public Edge(int i, int j){
            this.i = i;
            this.j = j;
        }

        //TODO: implement this class;
    }


    public class DoublyLinkedList<T>{
        //TODO: implement this class;
    }

    /**
     * a HashMap mapping from keys: K to values: V using chaining (LinkedList) and Universal Hashing (we'll implement a hash function within the class)
     * K is set to default as Integer since it makes the implementation easier
     * @param <V>
     */
    public class HashMap<V>{
        private int p; // the prime number of the Hash function
        private hashNode<V>[] table;
        private int a;
        private int b;

        public HashMap(Node[] nodes, float loadFactor){
            p = (int)Math.pow(10, 9) + 9;
            table = new hashNode[(int)((float)nodes.length*loadFactor)];
            Random rand = new Random();
            a = rand.nextInt(p-1) + 1;
            b = rand.nextInt(p);
            return;
        }

        /**
         * the hash function of the HashMap
         * @return
         */
        private int hash(int key) { return Math.floorMod(a*key+b, p); }


        /**
         * a method used to add a new item to the Hash Map. this method would be used only amongst the class's constructor,
         * due to its simplicity of use and due to the project's needs.
         * @param key
         * @param item
         */
        public void addItem(int key, V item){
            int hashedKey = hash(key);
            hashNode<V> curr = table[hashedKey];
            if (curr == null) {
                table[hashedKey] = new hashNode<V>(hashedKey, key, item, null);
                return;
            }
            hashNode<V> newItem = new hashNode<V>(hashedKey, key, item, curr);
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newItem;
        }


        /**
         * returns the node in the hashMap who holds the given key
         * @param key
         * @return
         */
        public hashNode getNode(int key) {
            int hashedKey = hash(key);
            hashNode<V> curr = table[hashedKey];
            while (curr != null) {
                if (curr.key == key){
                    return curr;
                }
                curr = curr.next;
            }
            return null;
        }


        /**
         * removes the node in the hashMap who holds the given key. if the node isn't in the map, return -1
         * @param key
         */
        public int removeNode(int key) {
            int hashedKey = hash(key);
            hashNode<V> prev = null;
            hashNode<V> curr = table[hashedKey];
            while (curr != null) {
                if (curr.key == key){
                    if (prev != null){
                        prev.next = curr.next;
                    } else {
                        table[hashedKey] = curr.next;
                    }
                    return 1;
                }
                prev = curr;
                curr = curr.next;
            }
            return -1;
        }

        /**
         * the class used to implement the Cell class of the items in the Hash Map.
         * The Cell holds a pointer to the actual node in the Graph that has the given key, and in addition, holds a pointer to its cell in the Maximum-Heap.
         * @param <V>
         */
        public class hashNode<V>{
            private final int hash;
            private final int key;
            private V value;
            private hashNode<V> next;

            public hashNode(int hash, int key, V value, hashNode<V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }

            public int getKey() { return this.key; }
            public V getValue() { return this.value; }
            public void setValue(V newValue){ value = newValue; }

        }
    }

    /**
     * Maximum Heap containing cells T
     */
    public class maxHeap<V>{
        // this will hold the pointer to the top node in the heap, thus, the node with the biggest key in the heap.
        private heapNode<V> top;

        /**
         * the constructor of the Maximum-Heap
         */
        public maxHeap(){

        }

        /**
         * this method is used to add a node into the heap. if the node has a key which already exists in the heap, return -1
         * @param node
         */
        public int addNode(V node) {
            int key = calculateKey(node);
            //TODO: implement this func
        }

        /**
         * used to calculate the sum of all the weights of the neighbours of @node, and the weight of @node itself.
         * This is basically the method to calculate the key of the node we want to insert.
         * @param node
         * @return
         */
        private int calculateKey(V node) {
            //TODO: implement this func
        }


        /**
         * the class used to implement the nodes of the Maximum-Heap.
         * @param <V>
         */
        public class heapNode<V>{
            private int key;
            private V value;
            private heapNode parent;
            private heapNode leftChild;
            private heapNode rightChild;


            /**
             * the constructor of the Heap's node.
             * @param key
             * @param node
             */
            public heapNode(int key, V node){
                this.key = key;
                this.value = node;
            }

            
            /**
             * the node's key's getter
             * @return
             */
            public int getKey() {
                return key;
            }


            /**
             * the node's parent's getter
             * @return
             */
            public heapNode getParent() {
                return parent;
            }


            /**
             * the node's left Child's getter
             * @return
             */
            public heapNode getLeftChild() {
                return leftChild;
            }


            /**
             * the node's right Child's getter
             * @return
             */
            public heapNode getRightChild() {
                return rightChild;
            }


            /**
             * the node's right Child's setter
             * @param rightChild
             */
            public void setRightChild(heapNode rightChild) {
                this.rightChild = rightChild;
            }


            /**
             * the node's left Child's setter
             * @param leftChild
             */
            public void setLeftChild(heapNode leftChild) {
                this.leftChild = leftChild;
            }


            /**
             * the node's parent's setter
             * @param parent
             */
            public void setParent(heapNode parent) {
                this.parent = parent;
            }


        }
        //TODO: implement this class;
    }



}


