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
    private hashMap<Node> nodesHash;
    private maxHeap nodesHeap;
    private Node[] Nodes;

    /**
     * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
     * You may assume that the ids of distinct nodes are distinct.
     *
     * @param nodes - an array of node objects
     */
    public Graph(Node [] nodes){
        Nodes = nodes;
        nodesHash = new hashMap<Node>(nodes, 1);

        for (Node node : nodes) { // adding all of the nodes to the Hash-Map
            nodesHash.addItem(node.getId(), node);
        }

        nodesHeap = new maxHeap(nodes.length);
        //TODO: implement this method.
    }

    /**
     * This method returns the node in the graph with the maximum neighborhood weight.
     * Note: nodes that have been removed from the graph using deleteNode are no longer in the graph.
     * <p>
     * this method takes O(1), since extracting the Maximum-node (the top node) of a Maximum Heap costs O(1)
     * </p>
     * @return a Node object representing the correct node. If there is no node in the graph, returns 'null'.
     */
    public Node maxNeighborhoodWeight(){
        if (this.isEmpty()) {
            return null;
        }
        return nodesHeap.getMax().getValue();
    }

    /**
     * given a node id of a node in the graph, this method returns the neighborhood weight of that node.
     *
     * @param node_id - an id of a node.
     * @return the neighborhood weight of the node of id 'node_id' if such a node exists in the graph.
     * Otherwise, the function returns -1.
     */
    public int getNeighborhoodWeight(int node_id){
        Node node = nodesHash.get(node_id);
        if (node != null) {
            return node.getVicinityWeight()
        }
        return -1;
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
        Node node1 = nodesHash.get(node1_id);
        Node node2 = nodesHash.get(node2_id);

        if (node1 == null || node2 == null) {
            return false;
        } else if (node1_id == node2_id) {
            return false;
        }

        Edge edge = new Edge(node1_id, node2_id, false);
        //TODO: add the Edge to the Graph
        return false;
    }

    /**
     * Given the id of a node in the graph, deletes the node of that id from the graph, if it exists.
     *
     * @param node_id - the id of the node to delete.
     * @return returns 'true' if the function deleted a node, otherwise returns 'false'
     */
    public boolean deleteNode(int node_id){
        Node node = nodesHash.get(node_id);

        if (node == null) {
            return false;
        }

        // TODO: delete it from the Graph
        return false;
    }


    /**
     * this method is used to check if the Graph is empty or not
     * @return True if the amount of nodes in the Graph (Nodes.length) is 0.
     */
    public boolean isEmpty(){
        return Nodes.length == 0;
    }

    /**
     * This class represents a node in the graph.
     */
    public class Node{
        private int id;
        private int weight;
        private int vicinityWeight;
        private DoublyLinkedList<Node> Neighbours;

        /**
         * Creates a new node object, given its id and its weight.
         * @param id - the id of the node.
         * @param weight - the weight of the node.
         */
        public Node(int id, int weight){
            this.id = id;
            this.weight = weight;
            vicinityWeight = weight;
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
         * returns the vicinityWeight of the given node
         */
        public int getVicinityWeight() {
            return vicinityWeight;
        }

        /**
         * Updates the weight of the Node in case a new edge was added/deleted
         */
        public void UpdateVicinityWeight(int additionalWeight){
            vicinityWeight += additionalWeight;
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
         * <p>
         * this method has a time-Complexity of O(1)
         * </p>
         * @param item
         */
        public void addItem(T item){
            DoublyLinkedCell curr = this.head;
            DoublyLinkedCell newCell = new DoublyLinkedCell(item);

            if (curr == null) { // this is true if and only if the List is empty
                this.head = newCell;
                this.tail = newCell;
                newCell.prev = newCell;
                newCell.next = newCell;
                return;
            }

            // inserting the item at the tail of the List
            curr = this.tail;
            this.tail = newCell;
            curr.next = newCell;
            newCell.prev = curr;

            // since the item was inserted at the tail of the List, we now need to update its following cell to be the head of the List
            newCell.next = this.head;
            this.head.prev = newCell;
        }


        /**
         * this method is used to delete an item from the Doubly Linked List
         * <p>
         * performed in O(n), while n is the length of the List
         * </p>
         * @param item
         */
        public void deleteItem(T item){
            DoublyLinkedCell curr = this.head;
            while (curr != null) {
                if (curr.item == item) {
                    DoublyLinkedCell Prev = curr.prev;
                    DoublyLinkedCell Next = curr.next;

                    if (Prev == null || Next == null) { // in case the item which we want to delete was either the head or the tail of the List
                        if (Prev == null && Next == null) { // if the List contains only one item and its the item we want to delete
                            this.head = null;
                            this.tail = null;
                        } else if (Prev == null) { // if the item we want to delete is the head of the List
                            this.head = Next;
                            Next.prev = this.tail;
                        } else { // if the item we want to delete is the tail of the List
                            this.tail = Prev;
                            Prev.next = this.head;
                        }
                    } else {
                        Prev.next = Next;
                        Next.prev = Prev;
                    }
                    return; // the deletion was done, now stop the process of the method
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

        public hashMap(V[] nodes, float loadFactor){
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
        public V get(int key) {
            int hashedKey = hash(key);
            hashCell<V> curr = table[hashedKey];
            while (curr != null) {
                if (curr.key == key){
                    return curr.getValue();
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
        // this will be the array that represents the Maximum-Heap
        private heapNode[] Heap;
        // this will hold the number of nodes in the Heap
        private int size;
        // this will be initialized in the Constructor as the maximum number of nodes in the Heap at any given time
        private int maxSize;

        /**
         * the constructor of the Maximum-Heap
         * initializes the maxSize with the given argument when called
         * creates the Heap's array according to the given maxSize
         * initializes the head of the Heap to be a so-called "virtual node", in order to maintain balance when inserting the first node to the Heap.
         */
        public maxHeap(int maxSize){
            this.maxSize = maxSize;
            this.size = 0;
            Heap = new heapNode[maxSize];
        }


        /**
         * this method is used to return the position (index) of the parent node (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * @param pos - the index of the node in the Heap's array
         */
        private int parent(int pos) {
            return Math.floorDiv(pos, 2);
        }

        /**
         * this method is used to return the position (index) of the left Child (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * @param pos - the index of the original node in the Heap's array
         */
        private int leftChild(int pos) {
            return 2*pos;
        }

        /**
         * this method is used to return the position (index) of the Right Child (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * @param pos - the index of the original node in the Heap's array
         */
        private int rightChild(int pos) {
            return 2*pos + 1;
        }

        /**
         * @param pos
         * @return True if the node at the given @pos (in the Heap's array) is a leaf in the array, using the Heap's array.
         */
        private boolean isLeaf(int pos){
            return (pos > size/2) && (pos <= size);
        }


        /**
         * this method is mainly created for the sake of performing Heapfies-UP and Heapifies-DOWN.
         * it swaps the nodes at the given positions (@pos1, @pos2) in the Heap's array/
         * @param pos1
         * @param pos2
         */
        private void swap(int pos1, int pos2){
            heapNode tmp = Heap[pos1];
            Heap[pos1] = Heap[pos2];
            Heap[pos2] = tmp;
        }

        /**
         * this method is used to add a node into the heap. it also performs the Heapifying-process used to maintain the balance and requirements of a Maximum-Heap
         * @param node
         */
        public void addNode(Node node) {
            Heap[size++] = new heapNode(node.vicinityWeight, node);
            int curr = size;

            while (Heap[curr].key > Heap[parent(curr)].key) {
                swap(curr, parent(curr));
                curr = parent(curr);
            }
        }


        /**
         * this method returns the "head" of the Heap, or in other words: the node in the Heap which holds the largest key.
         * @return
         */
        public heapNode getMax(){
            return Heap[0];
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


            /**
             * a Constructor to create a virtual node used in the initialization of a Maximum-Heap
             */
            public heapNode(){
                this.key = Integer.MAX_VALUE;
                this.value = null;
            }

            /**
             * checks if the given node is the virtual node that we create at the initialization of the Maximum-Heap
             * @return
             */
            public boolean isRealNode(){
                return this.value != null;
            }

            /**
             * the getter of the Graph-node which lies under the given heap-Node
             * @return
             */
            public Node getValue() {
                return value;
            }
        }
    }



}


