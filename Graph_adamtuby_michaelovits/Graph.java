/*
You must NOT change the signatures of classes/methods in this skeleton file.
You are required to implement the methods of this skeleton file according to the requirements.
You are allowed to add classes, methods, and members as required.
 */



import java.util.*;


/**
 * This class represents a graph that efficiently maintains the heaviest neighborhood over edge addition and
 * vertex deletion.
 *
 */
public class Graph {
    // holds the hashMap of our nodes. the keys of the hashCells are Integers by default, and the generic class (this case: Node) is the value of the cells.
    private final hashMap<Node> nodesHash;
    // holds the maximum heap of our nodes. the keys of each node node in the heap are the vicinity weights of a node, and the value of a node is the actual Node
    private final maxHeap<Node> nodesHeap;

    /**
     * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
     * You may assume that the ids of distinct nodes are distinct.
     * <p>
     * Complexity: O(N), while N is the amount of nodes passed in the @nodes array.
     * </p>
     * @param nodes - an array of node objects
     */
    public Graph(Node [] nodes){
        nodesHash = new hashMap<>(nodes.length, 0.5f); // initialize the hash map of our nodes to be a new hash map with a load factor of 0.5
        nodesHeap = new maxHeap<>(nodes.length); // initialize the maximum heap of our nodes. the priority Queue of the heap is set by default to be of length N

        // adding all of the nodes to the Hash-Map and to the Maximum-Heap
        for (Node node : nodes) { // for node:
            // adding the node to the hash map with its key being its Id, and adding the node to the maximum-heap with its key being its vicinity weight (which is at default its weight at the time of the graph's creation)
            hashMap<Node>.hashCell<Node> hashForm = nodesHash.addItem(node.getId(), node);
            maxHeap<Node>.heapNode<Node> heapForm = nodesHeap.addNode(node, node.getVicinityWeight());

            // set a pointer from the node.hashForm and node.heapForm fields to the node in the hash map and in the maximum-heap, so we can access later the node's form in the hash map from within the node
            node.setHashForm(hashForm);
            node.setHeapForm(heapForm);
        }
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
        if (this.isEmpty()) { // if the graph is empty return null
            return null;
        }
        return nodesHeap.getMax().getValue();
    }

    /**
     * given a node id of a node in the graph, this method returns the neighborhood weight of that node.
     * <p>
     * Complexity: O(1)
     * </p>
     * @param node_id - an id of a node.
     * @return the neighborhood weight of the node of id 'node_id' if such a node exists in the graph.
     * Otherwise, the function returns -1.
     */
    public int getNeighborhoodWeight(int node_id){
        Node node = nodesHash.get(node_id);
        if (node != null) { // if the node with the given node_id wasn't found in the graph
            return node.getVicinityWeight();
        }
        return -1;
    }


    /**
     * This function adds an edge between the two nodes whose ids are specified.
     * If one of these nodes is not in the graph, the function does nothing.
     * The two nodes must be distinct; otherwise, the function does nothing.
     * You may assume that if the two nodes are in the graph, there exists no edge between them prior to the call.
     * <p>
     * The time complexity of this method is: O(log n).
     * </p>
     * @param node1_id - the id of the first node.
     * @param node2_id - the id of the second node.
     * @return returns 'true' if the function added an edge, otherwise returns 'false'.
     */
    public boolean addEdge(int node1_id, int node2_id){
        // accessing the nodes from the hash map with their Id-s
        Node node1 = nodesHash.get(node1_id);
        Node node2 = nodesHash.get(node2_id);

        // if the nodes of the edge we want to add are one of the following two, we would return false by default:
        if (node1 == null || node2 == null) { // if one of the nodes of the edge aren't presnet in the graph
            return false;
        } else if (node1_id == node2_id) { // if the two nodes of the edge are the same node
            return false;
        }

        // adding each node to the other's Neighbors list:
        // this process will be changing the nodes' vicinity weight due to their addition of a new Neighbor as a result of an addition of a new edge to the graph
        // this process will also cause a heapifying process in the Maximum-Heap of the Graph - since their keys in the Heap (their vicinity weight) have been change
        node1.addNeighbor(node2);
        node2.addNeighbor(node1);


        // adding the 'parallel' field of each DoublyLinkedList cell representing the freshly added edge as the parallel form of the edge in the Neighbor's DoublyLinkedList
        node1.Neighbors.tail.setParallel(node2.Neighbors.tail);
        node2.Neighbors.tail.setParallel(node1.Neighbors.tail);

        // the edge was added succesfuly, return true now
        return true;
    }

    /**
     * Given the id of a node in the graph, deletes the node of that id from the graph, if it exists.
     * <p>
     * Time Complexity: O(log n)
     * </p>
     * @param node_id the id of the node to delete.
     * @return returns 'true' if the function deleted a node, otherwise returns 'false'
     */
    public boolean deleteNode(int node_id){
        Node node = nodesHash.get(node_id);

        if (node == null) { // if the node wasn't found in the Graph
            return false;
        } else { // the node was found in the Graph
            nodesHash.removeNode(node_id);
            nodesHeap.deleteNode(node.getHeapForm());
            DoublyLinkedList<Node>.DoublyLinkedCell currCell = node.Neighbors.head;

            // get the amount of neighbours of the node we want to delete
            int NeighborsCount = node.getNeighborsAmount();

            for (int i=0; i<NeighborsCount; i++) { // iterating between all of the edges 'node' was connected with, using the Neighbors of 'node'
                DoublyLinkedList<Node>.DoublyLinkedCell linkedCell = currCell.getParallel(); // getting the parallel form of the edge in order to remove it from the other end of the edge (which is not 'node')
                linkedCell.getRepresentativeList().deleteCell(linkedCell); // deleting the edge and removing 'node' from the Neighbors list of its Neighbor
                Node currNode = currCell.getItem(); // getting the Neighbor's form as a Node
                currNode.UpdateVicinityWeight(-node.getWeight()); // removing the 'node'`s weight from the vicinity weight of its Neighbor, and therefore, possibly Heapifying the Neighbor in the Maximum-Heap of the Graph
                currCell = currCell.next; // continuing on to the next Neighbor
            }

            // the node was allegdly deleted, thus return true
            return true;
        }
    }


    /**
     * this method is used to determine the amount of nodes in the Graph
     * <p>
     * The time complexity of this method is O(1)
     * </p>
     * @return the amount of nodes in the Maximum-Heap representing the Graph
     */
    public int getNumNodes(){
        return nodesHeap.getSize();
    }


    /**
     * this method is used to determine the amount of edges that there are in the Graph.
     * it does that by counting every edge twice and then deviding everything by two.
     * well, if we sum up the amount of neighbor each node has, we will basically get that we counted each edge twice, so all we need to do is devide everything by two.
     * <p>
     * The time complexity of this method is O(n) while n is the amount of vertices in the Graph.
     * </p>
     * @return the amount of edges in the graph
     */
    public int getNumEdges(){
        // initializing a counting variable.
        int sum = 0;
        for (maxHeap<Node>.heapNode<Node> node : nodesHeap.Heap) { // for every node in the graph
            if (node == null) { // the heap's priority Queue is designed to hold at most N nodes at a time, and therefore while it isn't full, it will have its right part of the array be a sequence of 'null'. so when we reach that point of the heap's array, we know we iterated through all of the nodes in the graph
                break;
            }
            // add to the counting varibale the amount of neighbours it has
            sum += node.getValue().getNeighborsAmount();
        }

        // return our counting variable devided by 2. while is it always even? well, sum is basically counting each edge twice (one time at each one of its nodes)
        return sum/2;
    }




    /**
     * this method is used to check if the Graph is empty or not
     * @return True if the amount of nodes in the Graph (Nodes.length) is 0.
     */
    public boolean isEmpty(){
        return this.nodesHeap.getSize() == 0;
    }



    /**
     * This class represents a node in the graph.
     */
    public static class Node{
        private final int id;
        private final int weight;
        private int vicinityWeight;
        private final DoublyLinkedList<Node> Neighbors;
        private maxHeap<Node>.heapNode<Node> heapForm;
        private hashMap<Node>.hashCell<Node> hashForm;

        /**
         * Creates a new node object, given its id and its weight.
         * the node's vicinity weight is initialized to be the weight of the node. this is true because when we insert a new node to the graph, nodes
         * are yet to be connected with it. so the vicinity weight is simply just its weight.
         * we also initialize its Neighbors' field to be a new DoublyLinkedList<Node>
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param id the id of the node.
         * @param weight the weight of the node.
         */
        public Node(int id, int weight){
            this.id = id;
            this.weight = weight;
            vicinityWeight = weight;
            Neighbors = new DoublyLinkedList<>();
        }


        /**
         * Returns the id of the node.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the id of the node.
         */
        public int getId(){
            return id;
        }

        /**
         * Returns the weight of the node.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the weight of the node.
         */
        public int getWeight(){
            return weight;
        }


        /**
         * returns the vicinityWeight of the given node
         * <p>
         * Time Complexity: O(1)
         * </p>
         */
        public int getVicinityWeight() {
            return vicinityWeight;
        }

        /**
         * Updates the weight of the Node in case a new edge was added/deleted.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param additionalWeight the new vicinityWeight of the node will be vicinityWeight + additionalWeight
         */
        public void UpdateVicinityWeight(int additionalWeight){
            vicinityWeight += additionalWeight;
            this.heapForm.changeKey(this.vicinityWeight);
        }

        /**
         * @pre @node must not be a Neighbor
         * this method is used to add a new Neighbor, which is @node. In other words, this method is used to add a new Edge that consists of the original node and the given argument node
         * <p>
         * Time Complexity: O(1)
         * </p>
         * we simply add @node to our DoublyLinkedList of @Neighbors, and update the vicinity weight of our original node (because it has a new Neighbor). so we add the new node's weight to the vicinity weight of the original node
         * @param node
         */
        public void addNeighbor(Node node){
            Neighbors.addItem(node);
            UpdateVicinityWeight(node.getWeight());
        }



        /**
         * this method is used to add as a new field - the pointer of the node's form in the Maximum-Heap of the Graph.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param heapForm the pointer we update the field 'heapForm' to.
         */
        public void setHeapForm(maxHeap<Node>.heapNode<Node> heapForm) {
            this.heapForm = heapForm;
        }



        /**
         * this method is used to add as a new field - the pointer of the node's form in the hash-Map of the Graph.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param hashForm the pointer we update the field 'hashForm' to.
         */
        public void setHashForm(hashMap<Node>.hashCell<Node> hashForm) {
            this.hashForm = hashForm;
        }


        /**
         * the getter of the pointer to the node's form in the hashMap of the Graph
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the pointer to the node's form in the hash map of the graph
         */
        public hashMap<Node>.hashCell<Node> getHashForm() {
            return hashForm;
        }


        /**
         * the getter of the pointer to the node's form in the Maximum-Heap of the Graph
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the pointer to the node's form in the maximum heap of the graph
         */
        public maxHeap<Node>.heapNode<Node> getHeapForm() {
            return heapForm;
        }


        /**
         * this method returns the amount of Neighbors that the node has - Or in other words, the amount of edges that the node partakes in.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the DoublyLinedList's length: 'Neighbors' of the node, which contains all of its neighbors
         */
        public int getNeighborsAmount(){
            return this.Neighbors.length();
        }

    }


    /**
     * this class is an abstract class, that implements the basic format of `some` cell in `some` data structure.
     *
     * @param <T> the class of the value that we want to put in the cell
     */
    public static abstract class Cell<T>{
        // key of the cell
        protected int key;
        // value of the cell
        protected T value;

        /**
         * the constructor of the class.
         * initializes the plain fields of the instance.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param key assigned to the field `key`
         * @param value assigned to the field `value`
         */
        protected Cell(int key, T value) {
            this.key = key;
            this.value = value;
        }


        /**
         * the getter of the key
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the key of the cell
         */
        protected int getKey() {
            return key;
        }


        /**
         * the getter of the value
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the value of the cell
         */
        protected T getValue() {
            return value;
        }


        /**
         * the setter of the key
         * <p>
         * Time Complexity: O(1)
         * </p>
         */
        protected void setKey(int key) {
            this.key = key;
        }


        /**
         * the setter of the value
         * <p>
         * Time Complexity: O(1)
         * </p>
         */
        protected void setValue(T value) {
            this.value = value;
        }
    }



    /**
     * a Doubly Linked List implemented by basic pointers logic.
     * a default constructor was enough for this implementation.
     * @param <T> the class of each value in each cell of the list.
     */
    public static class DoublyLinkedList<T>{
        private DoublyLinkedCell tail;
        private DoublyLinkedCell head;
        private int length = 0;

        /**
         * this method is used to add a new item to the Doubly Linked List
         * <p>
         * this method has a time-Complexity of O(1)
         * </p>
         * @pre item must not exist in the List
         * @param item
         */
        public void addItem(T item){
            length += 1; // higher the length (the amount of cells) in the list by one
            DoublyLinkedCell newCell = new DoublyLinkedCell(item); // create the instance of the new value we want to put in the list. basically wrapping the new value in a DoublyLinkedCell (a DoublyLinkedList deals with only its non-statically created DoublyLinkedCell class)

            if (this.isEmpty()) { // this is true if and only if the List is empty
                // initialize the head and the tail of the list to be the added value + manage the .prev and .next fields accordingly
                this.head = newCell;
                this.tail = newCell;
                newCell.prev = newCell;
                newCell.next = newCell;
                return;
            }

            // inserting the item at the tail of the List
            DoublyLinkedCell curr = this.tail;
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
         * Time Complexity: O(1)
         * </p>
         * @pre item must be the direct pointer to the cell that holds the item we want deleted
         * @param item
         */
        public void deleteCell(DoublyLinkedCell item){
            length -= 1; // lower the length (the amount of cells) in the list by one

            // store the items who are before and after the item we want to delete in the list, as: Prev, and Next
            DoublyLinkedCell Prev = item.prev;
            DoublyLinkedCell Next = item.next;

            if (length == 0) { // if the item was the only item in the list, empty the list
                this.head = null;
                this.tail = null;
                return;
            } else if (this.head == item) { // if the item is the head of the list, and make Next the new head of the list
                this.head = Next;
            } else if (this.tail == item) { // if the item is the tail of the list, and make Prev the new tail of the list
                this.tail = Prev;
            }

            // update the next, and prev values of Prev, and Next, due to the deletion ocurring
            Prev.next = Next;
            Next.prev = Prev;
        }


        /**
         * this method is used to determine whether or not the list is empty
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return False if the first item of the list is a null and therefore the list is empty, else return True
         */
        private boolean isEmpty(){
            return this.head == null;
        }


        /**
         * this method returns the length of this Linked List
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return the lenght of the list
         */
        public int length() {
            return length;
        }


        /**
         * this class is the class of a single Cell in the list. The list contains several Cells which are linked to each other.
         * this class is mainly to keep the implementation of a linked list available for all kinds of lists that we want, including a linked list of Graph-Nodes.
         */
        private class DoublyLinkedCell{
            // the item coming after this instance in the list
            private DoublyLinkedCell next;
            // the item coming before this instnace in the list
            private DoublyLinkedCell prev;
            // the parallel value of the item. this is up to the client to decide what it will be. in our case this will hold a pointer to the second representation of the edge (for example the item n1 in n2's list, will hold the parallel field to be the item n2 in n1's list)
            private DoublyLinkedCell parallel;
            // the value that the cell holds
            private final T item;

            /**
             * the constructor of this class
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @param item the value we want to assign to the DoublyLinkedList cell
             */
            public DoublyLinkedCell(T item) {
                this.item = item;
            }


            /**
             * the 'setter' of this field
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @param parallel the DoublyLinkedCell we want to assign as the parallel cell of th given DoublyLinkedList cell
             */
            public void setParallel(DoublyLinkedCell parallel) {
                this.parallel = parallel;
            }

            /**
             * the 'getter' of this field
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @return the parallel cell of the given DoublyLinkedList cell
             */
            public DoublyLinkedCell getParallel() {
                return parallel;
            }


            /**
             * the 'getter' of the given cell's DoublyLinkedList
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @return the DoublyLinkedList which holds the given DoublyLinkedCell
             */
            public DoublyLinkedList<T> getRepresentativeList(){
                return DoublyLinkedList.this;
            }


            /**
             * the 'getter' of the given cell's item/value
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @return the value that the cell holds
             */
            public T getItem() {
                return item;
            }
        }
    }


    /**
     * a hashMap mapping from keys: K to values: Node, using chaining (LinkedList) and Universal Hashing (we'll implement a hash function within the class)
     * K is set to default as Integer since it makes the implementation easier
     * the hash map deals with items that are of the sub-class: hashCell<V>. this is used to generalize the use of this class, to perhaps outside this project.
     * @param <V> the values that we want our hash map to handle
     */
    public static class hashMap<V>{
        private final hashCell<V>[] table; // the table of the hash-table. the table will manage the chaining.

        // the parameters used to calculating our universally-selected modular hash function
        private final int p = (int)Math.pow(10, 9) + 9; // the prime number of the Hash function. this is set by default to the prime number that we got handed with the assignment
        private final int a; // random int between 1 to p
        private final int b; // random int between 0 to p
        private final int m; // the lenght of our hash-table

        // the amount of items in the hash map
        private int size = 0;

        /**
         * the constructor of this class.
         * initializes the hash table to be of size this.m (after we calculate this.m with our @loadFactor and @m).
         * p is initialized
         * initializes a and b to be random numbers as they are supposed to be as discussed above.
         * <p>
         * Time Complexity: O(m) = O(N)
         * </p>
         * @SupressWarnings("unchecked") this is to avoid a warning caused by our attempt at generifying this class
         * @param m the amount of items that the hash map is intended to hold at most
         * @param loadFactor the load factor that we want our hash table to have. this.m (the length of our table) is calculated by taking the maximal amount of items that the hash map initially held (@m) and multiplying it by the 1/@loadFactor
         */
        @SuppressWarnings("unchecked")
        public hashMap(int m, float loadFactor){
            // initializing our fields as discussed above
            this.m = (int)((float)m*(1/loadFactor));
            this.table = (hashMap<V>.hashCell<V>[]) new hashCell[this.m];

            // randomizing a and b
            Random rand = new Random();
            this.a = rand.nextInt(p-1) + 1;
            this.b = rand.nextInt(p);
        }


        /**
         * the hash function of the hashMap
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return
         */
        private int hash(int key) {
            return Math.floorMod(Math.floorMod(a*key+b, p), this.m);
        }




        /**
         * a method used to add a new item to the Hash Map. this method would be used only amongst the class's constructor,
         * due to its simplicity of use and due to the project's needs.
         * we iterate through the chaining model of the hash map
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @pre the key must not exist in the Hash-Map
         * @param key the key of the item we want to insert to the map
         * @param item the value of the item we want to insert to the map
         * @return the pointer to the hashCell that wraps the inserted item in the hash-map
         */
        public hashCell<V> addItem(int key, V item){
            // highring the hash map's size by one
            size++;

            // calculating the hashed key for the given key
            int hashedKey = hash(key);

            // accessing the linked list we have at the table's array cell at index @hashedKey
            hashCell<V> curr = table[hashedKey];

            // if the linked list at table[hashedKey] is empty, initialize it to be the item we want to insert
            if (curr == null) {
                table[hashedKey] = new hashCell<>(key, item, null); // wrapping our new item with the sub-class: hashCell<V>
                return table[hashedKey];
            }

            // if the linked list at table[hashedKey] isn't empty, iterate to its end and add our new item to the end of the list
            hashCell<V> newItem = new hashCell<>(key, item, null); // wrapping our new item with the sub-class: hashCell<V>
            while (curr.next != null) { // iterating to the end of the linked list
                curr = curr.next;
            }

            // adding the new item to the end of the list
            curr.setNext(newItem);
            return newItem;
        }




        /**
         * returns the node in the hashMap who holds the given key.
         * done by accessing the likned list chain at index hashedKey(@key) in the hash-map's table, and by searching the given key in that list.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param key
         * @return the value of the hashCell that holds the given @key
         * @post if the given key is in the hash map, $ret is the value of the hashCell holding it, else $ret = null.
         */
        public V get(int key) {
            // calculate the hash code of the given key
            int hashedKey = hash(key);

            // accessing the chain that sits at the index of the hash code of the given key in the hash map's table
            hashCell<V> curr = table[hashedKey];

            // iterating through the linked list trying to find a hashCell that holds @key
            while (curr != null) {
                if (curr.key == key){ // if the hashCell holds the given key, return its value
                    return curr.getValue();
                }
                curr = curr.next; // move to the next hashCell in the chain
            }

            // if the key wasn't found in the hash map, return null
            return null;
        }





        /**
         * removes the node in the hashMap who holds the given key. if the node isn't in the map, return -1
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param key the key that the hashCell we want to delete holds
         */
        public int removeNode(int key) {
            // calculate the hash code of the given key in order to access the right chain of hashCells in the hash-map's table
            int hashedKey = hash(key);

            // initializing a variable `prev` which will always hold the pointer to the hashCell coming prior to the `curr` hashCell in their chain
            hashCell<V> prev = null;
            // the first item in the linked list in table[hashedKey]
            hashCell<V> curr = table[hashedKey];

            // searching the key in the chain at table[hashedKey]
            while (curr != null) {
                if (curr.key == key){ // if the `curr` hashCell holds the wanted key
                    if (prev != null){ // if `curr` *isn't* the head of the linked list at table[hashedKey], update the hashCell *after* `prev` to be the hashCell coming after the cell `curr` that we want to delete
                        prev.next = curr.next;
                    } else { // if `curr` is the head of the linked list at table[hashedKey], change the head of that list to be the hashCell coming affter `curr`
                        table[hashedKey] = curr.next;
                    }

                    // lower the size of the hash map by one
                    size--;

                    // return 1, since we found a cell to delete
                    return 1;
                }

                // steping forward in the linked list of table[hashedKey]
                prev = curr;
                curr = curr.next;
            }

            // return -1 because if the method reached thus far, no cell was delete, and therefore the given key wasn't initially in the graph / hash map
            return -1;
        }



        /**
         * the class used to implement the Cell class of the items in the Hash Map.
         * The Cell holds a pointer to the actual node in the Graph that has the given key, and in addition, holds a pointer to its cell in the Maximum-Heap.
         * This class implements the abstract class Cell<T>.
         * each instance of this class must maintain its field `next`, in order to have the structure of the hash-map the structure of a chaining hash-map
         */
        private class hashCell<T> extends Cell<T>{
            // the hashCell coming after the this instance in the hash map
            private hashCell<T> next;

            /**
             * the constructor of a Cell in the Hash-Map.
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @param key
             * @param value
             * @param next
             */
            public hashCell(int key, T value, hashCell<T> next) {
                // calling the super class's constructor with the basic key,value pair
                super(key, value);

                // initializing the `next` field
                this.next = next;
            }


            /**
             * the 'setter' of the 'next' field of the cell
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @param next
             */
            public void setNext(hashCell<T> next) {
                this.next = next;
            }
        }
    }


    /**
     * Maximum Heap containing cells T
     * The heap maintains a field called `Heap` which is an array of length @maxSize (determined at the constructor) of heapNodes<T> (sub-class of this class).
     * `Heap` is implemented as we saw in class with a priority Queue of a plain maximum-heap:
     * say the indices of the array are from 1 to maxSize: 1,2, ... , maxSize (and not 0,1, ... , maxSize -1), so:
     * For each node at the index i in the priority Queue, our array `Heap` upholds:
     * its parent is at inedx [i/2]
     * its left child is at index 2*i
     * its right child is at index 2*i + 1
     */
    public static class maxHeap<T>{
        // this will be the array that represents the Maximum-Heap
        private final heapNode<T>[] Heap;
        // this will hold the number of nodes in the Heap
        private int size;


        /**
         * the constructor of the Maximum-Heap
         * initializes the maxSize with the given argument when called
         * creates the Heap's array according to the given maxSize
         * initializes the head of the Heap to be a so-called "virtual node", in order to maintain balance when inserting the first node to the Heap.
         * <p>
         * Time Complexity: O(maxSize) = O(N)
         * </p>
         * @SupressWarnings("unchecked") this is to avoid a warning caused by our attempt at generifying this class
         */
        @SuppressWarnings("unchecked")
        public maxHeap(int maxSize){
            // initializing the size of the heap to be 0
            this.size = 0;

            // initializing the priority Queue of the maximum heap with an array of heapNodes of size @maxSize
            Heap = (maxHeap<T>.heapNode<T>[]) new heapNode[maxSize];
        }




        /**
         * this method is used to return the position (index) of the parent node (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param pos the index of the node in the Heap's array. if the given @pos is 0 (thus the root of the heap), then we return -1.
         */
        private int parent(int pos) {
            return Math.floorDiv(pos+1,2 ) - 1;
        }





        /**
         * this method is used to return the position (index) of the left Child (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param pos the index of the original node in the Heap's array
         */
        private int leftChild(int pos) {
            return 2*(pos+1)-1;
        }





        /**
         * this method is used to return the position (index) of the Right Child (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param pos the index of the original node in the Heap's array
         */
        private int rightChild(int pos) {
            return 2*(pos+1);
        }





        /**
         * this method's role is when a node in the Heap has more than one child, it returns the index (in the Heap's array) of the child with the smaller key.
         * if the given index is bigger than the Heap's array size, and thus, there is no such node , return -1.
         * if the given index holds a node which is a leaf, and therefore has no children, return -1.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param pos
         * @return - the child with the smaller key of the node which lies at the index pos in the Heap's array
         */
        private int biggerChild(int pos) {
            // tracing the childs of the node who stands at the given position in our heap's priority Queue: `Heap`
            int pos1 = leftChild(pos);
            int pos2 = rightChild(pos);

            if (isLeaf(pos) || pos > getMaxIndex()) { // if the pos is the position of a: leaf/non-node, return -1 so we know it doesn't have a such thing as: 'smallestChild'
                return -1;
            } else if (pos1 <= getMaxIndex() && pos2 <= getMaxIndex()) { // if the node has both children, return the position of the child with the smaller key
                return Heap[pos1].key < Heap[pos2].key ? pos2 : pos1;
            } else { // the node doesn't have a right child but isn't a leaf - therefore the leftChild is the only possible 'biggerChild'
                return pos1;
            }
        }






        /**
         * this method checks if the heapNode at the given index @pos (in the heap's array @Heap) is a leaf in the heap.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @param pos
         * @return True if the node at the given @pos (in the Heap's array) is a leaf in the array, using the Heap's array.
         */
        private boolean isLeaf(int pos){
            return (leftChild(pos) > getMaxIndex()) && (pos <= getMaxIndex());
        }





        /**
         * this method is mainly created for the sake of performing Heapfies-UP and Heapifies-DOWN.
         * it swaps the nodes at the given positions (@pos1, @pos2) in the Heap's array
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @pre pos1 <= size && pos2 <= size
         * @param pos1
         * @param pos2
         */
        private void swap(int pos1, int pos2){
            // updating the new positions fields of the nodes given at the indices of the Heap's array - given as the arguments
            Heap[pos1].setPos(pos2);
            Heap[pos2].setPos(pos1);

            // performing the swapping process
            heapNode<T> tmp = Heap[pos1];
            Heap[pos1] = Heap[pos2];
            Heap[pos2] = tmp;
        }




        /**
         * this method is used to add a node into the heap. it also performs the Heapifying-process used to maintain the balance and requirements of a Maximum-Heap
         * <p>
         * Time Complexity: O(log n), while n is the amount of nodes in the Heap (or in the graph)
         * </p>
         * @pre the node must not exist in the Heap
         * @param node
         * @return the pointer to the node's representation in the maximum heap, in order for the node to access in O(1) its heapNode
         */
        public heapNode<T> addNode(T node, int key) {
            // wrapping the given node of class T with @key in a heapNode<T>
            // also highering size by one
            Heap[size++] = new heapNode<>(key, node, size); // key = node.getVicinityWeight()

            // getting the index of the last node in the priority Queue of the heap denoted: `Heap`
            int curr = getMaxIndex();

            // adding our new item at the end of thr priority Queue as we should
            heapNode<T> heapForm = Heap[curr];

            // if only the recently inserted item is in the heap, then there is no need to Heapify the node up (or down), so we shalll return the pointer to the newly added heapNode
            if (curr == 0) {
                return heapForm;
            }

            // performing a Heapify-up process. We don't need to perform any heapiyfing-down process due to the implementation of this data structure
            do {
                if (parent(curr) < 0) { // breaking the loop when we reach the root: parent() returns a value lower then 0 only when the given position is the position of the root.
                    break;
                }
                swap(curr, parent(curr)); // swapping the newly added heapNode with its `current` parent
                curr = parent(curr); // advancing to the next iteration
                if (curr == 0) { // safety precaution
                    break;
                }
            } while (Heap[curr].key > Heap[parent(curr)].key); // continuing the loop as long as the newly added heapNode holds a key greater than its parent
            return heapForm; // returning the pointer to the freshly added item
        }





        /**
         * this method is used when we want to delete a certain node from the whole Graph, and therefore we delete it from the Maximum-Heap as well.
         * <p>
         * Time Complexity: O(log n), while n is the amount of nodes in the heap (or in the graph)
         * </p>
         * @pre the node must exist in the Heap
         * @param node a pointer to the node in the Graph.
         */
        public void deleteNode(heapNode<T> node){
            // get the position of the node in our priority Queue heap's array:
            int pos = node.getPos();

            // if the heap contains only one node, or if node is the last node in the priority Queue, then there must be no need for a heapifying process
            if (size == 1 || pos == getMaxIndex()) {
                Heap[--size] = null; // deleting @node by replacing its value in the array (Heap[pos]) with `null`. we are also reducing the `size` of the heap by one.
                return; // no need foor a heapifying process
            }

            // as shown in class, when we want a node deleted, we want to first replace it with the last node in the priority Queue, and only then `delete it`
            swap(pos, getMaxIndex());
            // deleting the node from the heap
            Heap[--size] = null;
            // since we swapped the delete node's position with the last node in the priority Queue's position, we now need to possibly perform a heapiyment process on the swapped noed. so we send that node to the Heapify method, which role is to perform the needed heapiying process on the node
            Heapify(this.Heap[pos]);
        }





        /**
         * this method is used to Heapify either UP or DOWN a node who has got its key changed.
         * <p>
         * Time Complexity: O(log n), while n is the amount of nodes in the heap (or in the graph)
         * </p>
         * @param node the node that we got sent with with the suspicion of a need in a heapiyinf process
         */
        public void Heapify(heapNode<T> node) {
            // get the position of the node in our heap's priority Queue: `Heap`
            int pos = node.getPos();

            // perform a re
            while (pos >= 0 && pos <= getMaxIndex()) {

                // if we need to heapify the node up:
                if (parent(pos) >= 0) { // we can heapify up a node only if its not the root of the heap. and the method parent() returns a negative value only for the position of the root of the heap
                    if (Heap[pos].key > Heap[parent(pos)].key) { // if the node indeed holds a key greater than its parent's, we perform a swap, update the variable `pos` to the position of the swapped node, which holds the position in the priority Queue of the node we need to Heapiy, and continue to the next iteration
                        swap(pos, parent(pos));
                        pos = parent(pos);
                        continue;
                    }
                }


                // if we need to heapify the node down
                int biggerchild = biggerChild(pos);

                if (biggerchild < 0) { // if the node doesn't have such thing as a 'biggerChild' (could result due to it being a leaf/non-node)
                    break;
                } else if (Heap[pos].key < Heap[biggerchild].key) { // checking if we actually need to Heapify down the node
                    swap(pos, biggerchild);
                    pos = biggerchild;
                    continue;
                } else { // safety precaution, in case biggerChild() reached an extreme case
                    if (leftChild(pos) == biggerchild && rightChild(pos) <= getMaxIndex()) { // if the non-smaller child is a left child and is smaller than the parent
                        if (Heap[pos].key < Heap[rightChild(pos)].key) { // if the node indeed holds a key greater its child, we perform a swap, update the variable `pos` to the position of the swapped node, which holds the position in the priority Queue of the node we need to Heapiy, and continue to the next iteration
                            swap(pos, rightChild(pos));
                            pos = rightChild(pos);
                            continue;
                        } else { // we don't need to perform a heapiyment process. therefore we are breaking the loop and terminating the run of this method
                            break;
                        }
                    } else if (rightChild(pos) == biggerchild && leftChild(pos) <= getMaxIndex()) { // if the non-smaller child is a right child and is smaller than the parent
                        if (Heap[pos].key < Heap[leftChild(pos)].key) { // if the node indeed holds a key greater its child, we perform a swap, update the variable `pos` to the position of the swapped node, which holds the position in the priority Queue of the node we need to Heapiy, and continue to the next iteration
                            swap(pos, leftChild(pos));
                            pos = leftChild(pos);
                            continue;
                        } else {
                            break; // we don't need to perform a heapiyment process. therefore we are breaking the loop and terminating the run of this method
                        }
                    }
                }
                break; // we don't need to perform a heapiyment process. therefore we are breaking the loop and terminating the run of this method
            }
        }



        /**
         * this method returns the "head" of the Heap, or in other words: the node in the Heap which holds the largest key.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return
         */
        public heapNode<T> getMax(){
            return Heap[0];
        }




        /**
         * this method is used to get the amount of nodes in the Heap, or in other words, the amount of nodes in the overall Graph.
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return
         */
        public int getSize() {
            return size;
        }


        /**
         * the index of the last node in the Array of this Heap to be different than a 'null'
         * <p>
         * Time Complexity: O(1)
         * </p>
         * @return
         */
        public int getMaxIndex(){
            return size-1;
        }




        /**
         * the class used to implement the nodes of the Maximum-Heap.
         * This class implements the abstract class Cell<V>.
         * each cell holds a field called `pos`, which maintains the index of the node in the priority Queue of the heap: `Heap`
         */
        private class heapNode<V> extends Cell<V>{
            private int pos;

            /**
             * the constructor of the Heap's node.
             * @param key the key of the cell
             * @param node the value of the cell
             */
            public heapNode(int key, V node, int pos){
                // calling the super class's constructor: Cell<V>
                super(key, node);
                // assigning @pos to the field `pos`
                this.pos = pos;
            }



            /**
             * this method returns the position of the given heap-Node in the Heap's array
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @return
             */
            public int getPos() {
                return pos;
            }




            /**
             * this method sets the position of the given heap-Node in the Heap's array
             * <p>
             * Time Complexity: O(1)
             * </p>
             * @param pos
             */
            public void setPos(int pos) {
                this.pos = pos;
            }



            /**
             * this method is used to *change* the key of an already existing node in the Heap.
             * <p>
             * Time Complexity: O(log n), while n is the amount of nodes in the heap (or in the graph)
             * </p>
             * @param key the new key of this node
             * @SupressWarnings("unchecked") this is to avoid a warning caused by our attempt at generifying this class
             */
            @SuppressWarnings("unchecked")
            public void changeKey(int key){
                // change the key of the node
                this.setKey(key);
                // send the node as a suspect which is prone to needing a heapifyment process
                maxHeap.this.Heapify((heapNode<T>) this);
            }
        }
    }



}


