/*
You must NOT change the signatures of classes/methods in this skeleton file.
You are required to implement the methods of this skeleton file according to the requirements.
You are allowed to add classes, methods, and members as required.
 */

import java.util.Random;

/**
 * This class represents a graph that efficiently maintains the heaviest neighborhood over edge addition and
 * vertex deletion.
 *
 */
public class Graph {
    private hashMap<Node> Nodes;
    /**
     * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
     * You may assume that the ids of distinct nodes are distinct.
     *
     * @param nodes - an array of node objects
     */
    public Graph(Node [] nodes){
        Nodes = new hashMap<Node>(nodes, 1);
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
        private int weight;
        private int totalWeight;
        private DoublyLinkedList<Node> Neighbours;

        /**
         * Creates a new node object, given its id and its weight.
         * @param id - the id of the node.
         * @param weight - the weight of the node.
         */
        public Node(int id, int weight){
            this.id = id;
            this.weight = weight;
            totalWeight = weight;
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
            return weight;
        }

        /**
         * Updates the weight of the Node in case a new edge was added/deleted
         */
        public void updateTotalWeight(int additionalWeight){
            totalWeight += additionalWeight;
        }
    }

    public class Edge{
        private int i;
        private int j;
        private Edge Parallel;

        public Edge(int i, int j, boolean isParallel){
            this.i = i;
            this.j = j;
            if (!isParallel) {
                Parallel = new Edge(j, i, true); // TODO: unsure how much this satisfies the assignment's requirements
            }

        }

        //TODO: fully implement this class;
    }


    public class DoublyLinkedList<T>{
        private DoublyLinkedCell tail;
        private DoublyLinkedCell head;

        /**
         * this method is used to add a new item to the Doubly Linked List
         * @param item
         */
        public void addItem(T item){
            DoublyLinkedCell curr = this.head;
            DoublyLinkedCell newCell = new DoublyLinkedCell(item);

            if (curr == null) {
                this.head = newCell;
                return;
            }

            this.tail = newCell;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newCell;
            newCell.prev = curr;
        }


        /**
         * this method is used to delete an item from the Doubly Linked List
         * @param item
         */
        public void deleteItem(T item){
            DoublyLinkedCell curr = this.head;
            while (curr != null) {
                if (curr.item == item) {
                    DoublyLinkedCell Prev = curr.prev;
                    DoublyLinkedCell Next = curr.next;

                    if (Prev == null || Next == null) {
                        if (Prev == null) {
                            this.head = curr.next;
                        }
                        if (Next == null) {
                            this.tail = curr.prev;
                        }
                    } else {
                        Prev.next = Next;
                        Next.prev = Prev;
                    }
                }
            }
        }

        public class DoublyLinkedCell{
            private DoublyLinkedCell next;
            private DoublyLinkedCell prev;
            private T item;

            public DoublyLinkedCell(T item) {
                this.item = item;
            }
        }
    }

    /**
     * a hashMap mapping from keys: K to values: V using chaining (LinkedList) and Universal Hashing (we'll implement a hash function within the class)
     * K is set to default as Integer since it makes the implementation easier
     * @param <V>
     */
    public class hashMap<V>{
        private int p; // the prime number of the Hash function
        private hashCell<V>[] table;
        private int a;
        private int b;

        public hashMap(Node[] nodes, float loadFactor){
            p = (int)Math.pow(10, 9) + 9;
            table = new hashCell[(int)((float)nodes.length*loadFactor)];
            Random rand = new Random();
            a = rand.nextInt(p-1) + 1;
            b = rand.nextInt(p);
            return;
        }

        /**
         * the hash function of the hashMap
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
            hashCell<V> curr = table[hashedKey];
            if (curr == null) {
                table[hashedKey] = new hashCell<V>(hashedKey, key, item, null);
                return;
            }
            hashCell<V> newItem = new hashCell<V>(hashedKey, key, item, curr);
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
        public hashCell getNode(int key) {
            int hashedKey = hash(key);
            hashCell<V> curr = table[hashedKey];
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
            hashCell<V> prev = null;
            hashCell<V> curr = table[hashedKey];
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
        public class hashCell<V>{
            private final int hash;
            private final int key;
            private V value;
            private hashCell<V> next;

            /**
             * the constructor of a Cell in the Hash-Map.
             * @param hash
             * @param key
             * @param value
             * @param next
             */
            public hashCell(int hash, int key, V value, hashCell<V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }


            /**
             * the cell's key's getter
             * @return
             */
            public int getKey() { return this.key; }


            /**
             * the cell's value's getter
             * @return
             */
            public V getValue() { return this.value; }


            /**
             * the cell's value's setter
             * @param newValue
             */
            public void setValue(V newValue){ value = newValue; }

        }
    }

    /**
     * Maximum Heap containing cells T
     */
    public class maxHeap{
        // this will hold the pointer to the top node in the heap, thus, the node with the biggest key in the heap.
        private heapNode head;

        /**
         * the constructor of the Maximum-Heap
         */
        public maxHeap(){}

        /**
         * this method is used to add a node into the heap. if the node has a key which already exists in the heap, return -1
         * @param node
         */
        public int addNode(Node node) {
            heapNode newNode = new heapNode(node.totalWeight, node);
            heapNode curr = this.head;
            if (curr == null){
                head = newNode;
                return 0;
            }

            //TODO: implement this func
            return 0;
        }



        /**
         * used to Heapify @node UP when required in order to sustain the requirements of a Maximum-Heap
         * @param node
         */
        public void HeapifyUp(heapNode node) {

        }

        /**
         * used to Heapify @node DOWN when required in order to sustain the requirements of a Maximum-Heap
         * @param node
         */
        public void HeapifyDown(heapNode node) {

        }

        /**
         * the class used to implement the nodes of the Maximum-Heap.
         */
        public class heapNode{
            private int key;
            private Node value;
            private heapNode parent;
            private heapNode leftChild;
            private heapNode rightChild;

            /**
             * the constructor of the Heap's node.
             * @param key
             * @param node
             */
            public heapNode(int key, Node node){
                this.key = key;
                this.value = node;
            }
        }
        //TODO: implement this class;
    }



}


