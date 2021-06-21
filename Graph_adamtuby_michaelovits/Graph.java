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
    private final hashMap<Node> nodesHash;
    private final maxHeap<Node> nodesHeap;

    /**
     * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
     * You may assume that the ids of distinct nodes are distinct.
     *
     * @param nodes - an array of node objects
     */
    public Graph(Node [] nodes){
        nodesHash = new hashMap<>(nodes.length, 0.5f);
        nodesHeap = new maxHeap<>(nodes.length);

        for (Node node : nodes) { // adding all of the nodes to the Hash-Map and to the Maximum-Heap
            hashMap<Node>.hashCell<Node> hashForm = nodesHash.addItem(node.getId(), node);
            node.setHashForm(hashForm);
            maxHeap<Node>.heapNode<Node> heapForm = nodesHeap.addNode(node, node.getVicinityWeight());
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
     * The time complexity of this method is: O(log n)
     * </p>
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

        // adding each node to the other's Neighbours list:
        // this process will be changing the nodes' vicinity weight due to their addition of a new neighbour as a result of an addition of a new edge to the graph
        // this process will also cause a heapifying process in the Maximum-Heap of the Graph - since their keys in the Heap (their vicinity weight) have been change
        node1.addNeighbour(node2);
        node2.addNeighbour(node1);


        // adding the 'parallel' field of each DoublyLinkedList cell representing the freshly added edge as the parallel form of the edge in the neighbour's DoublyLinkedList
        node1.Neighbours.tail.setParallel(node2.Neighbours.tail);
        node2.Neighbours.tail.setParallel(node1.Neighbours.tail);

        return true;
    }

    /**
     * Given the id of a node in the graph, deletes the node of that id from the graph, if it exists.
     *
     * @param node_id the id of the node to delete.
     * @return returns 'true' if the function deleted a node, otherwise returns 'false'
     */
    public boolean deleteNode(int node_id){
        Node node = nodesHash.get(node_id);

        if (node == null) { // if the node wasn't found in the Graph
            return false;
        } else { // the node was found in the Graph
            nodesHash.removeNode(node_id);
            nodesHeap.deleteNode(node.heapForm);
            DoublyLinkedList<Node>.DoublyLinkedCell currCell = node.Neighbours.head;

            int neighboursCount = node.getNeighboursAmount();

            for (int i=0; i<neighboursCount; i++) { // iterating between all of the edges 'node' was connected with, using the Neighbours of 'node'
                DoublyLinkedList<Node>.DoublyLinkedCell linkedCell = currCell.getParallel(); // getting the parallel form of the edge in order to remove it from the other end of the edge (which is not 'node')
                linkedCell.getRepresentativeList().deleteCell(linkedCell); // deleting the edge and removing 'node' from the Neighbours list of its Neighbour
                Node currNode = currCell.getItem(); // getting the Neighbour's form as a Node
                currNode.UpdateVicinityWeight(-node.getWeight()); // removing the 'node'`s weight from the vicinity weight of its Neighbour, and therefore, possibly Heapifying the Neighbour in the Maximum-Heap of the Graph
                currCell = currCell.next; // continuing on to the next Neighbour
            }

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
     * <p>
     * The time complexity of this method is O(n) while n is the amount of vertices in the Graph.
     * </p>
     * @return
     */
    public int getNumEdges(){
        int sum = 0;
        for (maxHeap<Node>.heapNode<Node> node : nodesHeap.Heap) {
            if (node == null) {
                break;
            }
            sum += node.getValue().getNeighboursAmount();
        }
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
     * the method called when we want to print an Object of type 'Graph'
     * the implementation of this method is the basic preview we want for a graph to make basic validity tests
     * @return
     */
    @Override
    public String toString(){
        String result = "";
        result += "\n\n\n\n";
        result += "Is the graph empty? " + (this.isEmpty() ? "Yes" : "Nope") + "\n";
        result += "Maximum Neighbour Weight (the node's ID): " + maxNeighborhoodWeight().getId() + "\n";
        result += "Amount of Nodes in the Graph: " + getNumNodes() + "\n";
        result += "Amount of Edges in the Graph: " + getNumEdges() + "\n";
        result += this.nodesHash.toString() + repeat("\n",5);
        result += this.nodesHeap.toString();
        return result;
    }


    private String repeat(String inp, int reps) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < reps; i++) {
            out.append(inp);
        }
        return out.toString();
    }


    /**
     * This class represents a node in the graph.
     */
    // todo: implement getters to the hashForm and heapForm
    public static class Node{
        private final int id;
        private final int weight;
        private int vicinityWeight;
        private final DoublyLinkedList<Node> Neighbours;
        private maxHeap<Node>.heapNode<Node> heapForm;
        private hashMap<Node>.hashCell<Node> hashForm;

        /**
         * Creates a new node object, given its id and its weight.
         * @param id the id of the node.
         * @param weight the weight of the node.
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
            this.heapForm.changeKey(this.vicinityWeight);
        }

        /**
         * this method is used to add a new Neighbour, which is @node. In other words, this method is used to add a new Edge that consists of the original node and the given argument node
         * @param node
         */
        public void addNeighbour(Node node){
            Neighbours.addItem(node);
            UpdateVicinityWeight(node.getWeight());
        }



        /**
         * this method is used to add as a new field - the pointer of the node's form in the Maximum-Heap of the Graph.
         * @param heapForm the pointer we update the field 'heapForm' to.
         */
        public void setHeapForm(maxHeap<Node>.heapNode<Node> heapForm) {
            this.heapForm = heapForm;
        }



        /**
         * this method is used to add as a new field - the pointer of the node's form in the hash-Map of the Graph.
         * @param hashForm the pointer we update the field 'hashForm' to.
         */
        public void setHashForm(hashMap<Node>.hashCell<Node> hashForm) {
            this.hashForm = hashForm;
        }


        /**
         * this method returns the amount of Neighbours that the node has - Or in other words, the amount of edges that the node partakes in.
         * @return
         */
        public int getNeighboursAmount(){
            return this.Neighbours.length();
        }


        /**
         * the method called when we want to print an Object of type 'Node'
         * the implementation of this method is the basic preview we want for a Node to make basic validity tests
         * @return
         */
        @Override
        public String toString(){
            return "";
            //TODO: implement this method (not to relevant tho)
        }
    }


    /**
     * a Doubly Linked List implemented by basic pointers logic.
     * @param <T> the class of each value in each cell of the list.
     */
    private static class DoublyLinkedList<T>{
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
            length += 1;
            DoublyLinkedCell newCell = new DoublyLinkedCell(item);

            if (this.isEmpty()) { // this is true if and only if the List is empty
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
         * performed in O(1)
         * </p>
         * @pre item must be the direct pointer to the cell that holds the item we want deleted
         * @param item
         */
        public void deleteCell(DoublyLinkedCell item){
            length -= 1;
            DoublyLinkedCell Prev = item.prev;
            DoublyLinkedCell Next = item.next;

            if (length == 1) { // if the item is the only item in the list
                this.head = null;
                this.tail = null;
                return;
            } else if (this.head == item) { // if the item is the head of the list
                this.head = Next;
            } else if (this.tail == item) { // if the item is the tail of the list
                this.tail = Prev;
            }

            Prev.next = Next;
            Next.prev = Prev;
        }


        /**
         * this method is used to determine whether or not the list is empty
         * @return False if the first item of the list is a null and therefore the list is empty, else return True
         */
        public boolean isEmpty(){
            return this.head == null;
        }


        /**
         * this method returns the length of this Linked List
         * @return
         */
        public int length() {
            return length;
        }

        /**
         * this class is the class of a single Cell in the list. The list contains several Cells which are linked to each other.
         * this class is mainly to keep the implementation of a linked list available for all kinds of lists that we want, including a linked list of Graph-Nodes.
         */
        private class DoublyLinkedCell{
            private DoublyLinkedCell next;
            private DoublyLinkedCell prev;
            private DoublyLinkedCell parallel;
            private final T item;

            /**
             * the constructor of this class
             * @param item the value we want to assign to the DoublyLinkedList cell
             */
            public DoublyLinkedCell(T item) {
                this.item = item;
            }


            /**
             * the 'setter' of this field
             * @param parallel the DoublyLinkedCell we want to assign as the parallel cell of th given DoublyLinkedList cell
             */
            public void setParallel(DoublyLinkedCell parallel) {
                this.parallel = parallel;
            }

            /**
             * the 'getter' of this field
             * @return the parallel cell of the given DoublyLinkedList cell
             */
            public DoublyLinkedCell getParallel() {
                return parallel;
            }


            /**
             * the 'getter' of the given cell's DoublyLinkedList
             * @return the DoublyLinkedList which holds the given DoublyLinkedCell
             */
            public DoublyLinkedList<T> getRepresentativeList(){
                return DoublyLinkedList.this;
            }


            /**
             * the 'getter' of the given cell's item/value
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
     */
    private class hashMap<V>{
        private final int p; // the prime number of the Hash function
        private final hashCell<V>[] table;
        private final int a;
        private final int b;
        private final int m;

        public hashMap(int m, float loadFactor){
            this.p = (int)Math.pow(10, 9) + 9;
            this.m = (int)((float)m*(1/loadFactor));
            this.table = new hashCell[this.m];

            Random rand = new Random();
            this.a = rand.nextInt(p-1) + 1;
            this.b = rand.nextInt(p);
        }

        /**
         * the hash function of the hashMap
         * @return
         */
        private int hash(int key) {
            return Math.floorMod(Math.floorMod(a*key+b, p), this.m);
        }




        /**
         * a method used to add a new item to the Hash Map. this method would be used only amongst the class's constructor,
         * due to its simplicity of use and due to the project's needs.
         * @pre the key must not exist in the Hash-Map
         * @param key
         * @param item
         */
        public hashCell<V> addItem(int key, V item){
            int hashedKey = hash(key);
            hashCell<V> curr = table[hashedKey];

            if (curr == null) {
                table[hashedKey] = new hashCell<>(hashedKey, key, item, null);
                return table[hashedKey];
            }

            hashCell<V> newItem = new hashCell<>(hashedKey, key, item, null);
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.setNext(newItem);
            return newItem;
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
         * the method called when we want to print an Object of type 'hashMap'
         * the implementation of this method is the basic preview we want for a hashMap to make basic validity tests
         * @return
         */
        @Override
        public String toString(){
            StringBuilder result;
            result = new StringBuilder("HashMap's array size is: " + this.table.length + "\n");
            result.append(repeat("\t", this.table.length - 1)).append("Hash-Map").append("\n");
            result.append(repeat("----", this.table.length * 2)).append("\n");

            result.append(repeat("\t", this.table.length - 3)).append("Number of nodes in the Graph: ").append(Graph.this.nodesHeap.getSize()).append("\n");
            result.append(repeat("----", this.table.length * 2)).append("\n");

            result.append(repeat("\t", this.table.length - 2)).append("Columns taken: ").append(Arrays.stream(table).filter(Objects::nonNull).mapToInt(x -> 1).sum()).append("\n");
            result.append(repeat("----", this.table.length * 2)).append("\n");

            String textBuffer = "\t";
            hashCell<V>[] tmpTable = this.table.clone();

            while (Arrays.stream(tmpTable).filter(Objects::nonNull).mapToInt(x -> 1).sum() > 0) {
                for (int i=0; i<this.table.length; i++) {
                    if (tmpTable[i] != null) {
                        result.append("| ").append(tmpTable[i].getKey()).append(" |").append(textBuffer);
                        tmpTable[i] = tmpTable[i].next;
                    } else {
                        result.append("     ").append(textBuffer);
                    }
                }
                result.append("\n");
            }

            return result.toString();
        }


        /**
         * the class used to implement the Cell class of the items in the Hash Map.
         * The Cell holds a pointer to the actual node in the Graph that has the given key, and in addition, holds a pointer to its cell in the Maximum-Heap.
         */
        private class hashCell<T>{
            private final int hash;
            private final int key;
            private T value;
            private hashCell<T> next;

            /**
             * the constructor of a Cell in the Hash-Map.
             * @param hash
             * @param key
             * @param value
             * @param next
             */
            public hashCell(int hash, int key, T value, hashCell<T> next) {
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
            public T getValue() { return this.value; }


            /**
             * the cell's value's setter
             * @param newValue
             */
            public void setValue(T newValue){ value = newValue; }


            /**
             * the 'setter' of the 'next' field of the cell
             * @param next
             */
            public void setNext(hashCell<T> next) {
                this.next = next;
            }
        }
    }


    /**
     * Maximum Heap containing cells T
     */
    private class maxHeap<T>{
        // this will be the array that represents the Maximum-Heap
        private final heapNode<T>[] Heap;
        // this will hold the number of nodes in the Heap
        private int size;
        // this will be initialized in the Constructor as the maximum number of nodes in the Heap at any given time
        private final int maxSize;



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
         * @param pos the index of the node in the Heap's array
         */
        private int parent(int pos) {
            if (pos % 2 == 0) {
                return pos / 2 - 1;
            }
            return Math.floorDiv(pos, 2);
        }





        /**
         * this method is used to return the position (index) of the left Child (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * @param pos the index of the original node in the Heap's array
         */
        private int leftChild(int pos) {
            return 2*(pos+1)-1;
        }





        /**
         * this method is used to return the position (index) of the Right Child (in the Heap's array) - of the node at the given @pos (in the Heap's array), using the Heap's array.
         * @param pos the index of the original node in the Heap's array
         */
        private int rightChild(int pos) {
            return 2*(pos+1);
        }





        /**
         * this method's role is when a node in the Heap has more than one child, it returns the index (in the Heap's array) of the child with the smaller key.
         * if the given index is bigger than the Heap's array size, and thus, there is no such node , return -1.
         * if the given index holds a node which is a leaf, and therefore has no children, return -1.
         * @param pos
         * @return - the child with the smaller key of the node which lies at the index pos in the Heap's array
         */
        private int biggerChild(int pos) {
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
         * @param pos
         * @return True if the node at the given @pos (in the Heap's array) is a leaf in the array, using the Heap's array.
         */
        private boolean isLeaf(int pos){
            return (leftChild(pos) > getMaxIndex()) && (pos <= getMaxIndex());
        }





        /**
         * this method is mainly created for the sake of performing Heapfies-UP and Heapifies-DOWN.
         * it swaps the nodes at the given positions (@pos1, @pos2) in the Heap's array
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
         * @pre the node must not exist in the Heap
         * @param node
         */
        public heapNode<T> addNode(T node, int key) {
            Heap[size++] = new heapNode<>(key, node, size); // key = node.getVicinityWeight()
            int curr = getMaxIndex();
            heapNode<T> heapForm = Heap[curr];

            if (curr == 0) {
                return heapForm;
            }

            do {
                swap(curr, parent(curr));
                curr = parent(curr);
                if (curr == 0) {
                    break;
                }
            } while (Heap[curr].key > Heap[parent(curr)].key);
            return heapForm;
        }





        /**
         * this method is used when we want to delete a certain node from the whole Graph, and therefore we delete it from the Maximum-Heap as well.
         * @pre the node must exist in the Heap
         * @param node a pointer to the node in the Graph.
         */
        public void deleteNode(heapNode<T> node){
            int pos = node.getPos();

            if (size == 1 || isLeaf(pos)) {
                Heap[--size] = null;
                return;
            }

            swap(pos, getMaxIndex());
            Heap[--size] = null;
            Heapify(this.Heap[pos]);
        }





        /**
         * this method is used to Heapify either UP or DOWN a node who has got its key changed.
         * <p>
         * this method is a recursive-wrapper-method to the recursive method: 'Heapify_Rec', which does the actual Heapifyingment.
         * </p>
         * @param node
         */
        public void Heapify(heapNode<T> node) {
            int pos = node.getPos();
            Heapify_Rec(pos);
        }


        /**
         * this is the recursive-method that 'Heapify' wraps.
         * @param pos the index at the Heap's array of the node we want to validate and Heapify.
         */
        private void Heapify_Rec(int pos){

            // if we need to heapify the node up
            if (pos != 0) {
                if (Heap[pos].key > Heap[parent(pos)].key) {
                    swap(pos, parent(pos));
                    Heapify_Rec(parent(pos));
                    return;
                }
            }


            // if we need to heapify the node down
            int biggerchild = biggerChild(pos);

            if (biggerchild < 0) { // if the node doesn't have such thing as a 'biggerChild' (could result due to it being a leaf/non-node)
                return;
            } else if (Heap[pos].key < Heap[biggerchild].key) { // checking if we actually need to Heapify down the node
                swap(pos, biggerchild);
                Heapify_Rec(biggerchild);
                return;
            } else {
                if (leftChild(pos) == biggerchild && rightChild(pos) <= getMaxIndex()) { // if the the non-smaller child is a left child and is bigger than the parent
                    if (Heap[pos].key < Heap[rightChild(pos)].key) {
                        swap(pos, rightChild(pos));
                        Heapify_Rec(rightChild(pos));
                    }
                } else if (rightChild(pos) == biggerchild && leftChild(pos) <= getMaxIndex()) { // if the the non-smaller child is a right child and is bigger than the parent
                    if (Heap[pos].key < Heap[leftChild(pos)].key) {
                        swap(pos, leftChild(pos));
                        Heapify_Rec(leftChild(pos));
                    }
                }
            }
        }





        /**
         * this method returns the "head" of the Heap, or in other words: the node in the Heap which holds the largest key.
         * @return
         */
        public heapNode<T> getMax(){
            return Heap[0];
        }




        /**
         * this method is used to get the amount of nodes in the Heap, or in other words, the amount of nodes in the overall Graph.
         * @return
         */
        public int getSize() {
            return size;
        }


        /**
         * the index of the last node in the Array of this Heap to be different than a 'null'
         * @return
         */
        public int getMaxIndex(){
            return size-1;
        }


        /**
         * the method called when we want to print an Object of type 'maxHeap'
         * the implementation of this method is the basic preview we want for a maxHeap to make basic validity tests
         * @return
         */
        @Override
        public String toString(){
            if (this.Heap.length == 0) {
                return "Heap is empty.";
            }
            StringBuilder result = new StringBuilder();

            int treeHeight = (int) (Math.log(this.getSize()) / Math.log(2)) + 1;
            int treeWidth = (int) Math.pow(2, treeHeight);
            result.append(repeat("\t", treeWidth / 2 - 1)).append("Maximum Heap\n");
            result.append(repeat("-", treeWidth * 4)).append("\n");
            List<heapNode<T>> curr = new ArrayList<>(1), next = new ArrayList<>(2);
            curr.add(getMax());
            final int maxHalfLength = 4;
            int elements = 1;
            StringBuilder sb = new StringBuilder(maxHalfLength * treeWidth);
            for(int i = 0; i < maxHalfLength * treeWidth; i++)
                sb.append(' ');
            String textBuffer;
            // Iterating through height levels.
            for(int i = 0; i < treeHeight; i++) {

                sb.setLength(maxHalfLength * ((int)Math.pow(2, treeHeight-1-i) - 1));
                // Creating spacer space indicator.
                textBuffer = sb.toString();
                // Print tree node elements
                for(heapNode<T> n : curr) {
                    result.append(textBuffer);
                    if(n == null) {

                        result.append("        ");
                        next.add(null);
                        next.add(null);
                    } else {
                        if (n != null) {
                            String some = String.format("(" + "%d" + "\033[1m" + ":%4d)" + "\033[0m", (n.getValue() instanceof Node) ? ((Node) n.getValue()).getId() : 1 , n.getKey()); // if we feel the need to change this just remove the '\003' part and delete the first % param
                            result.append(some);
                        }
                        else {
                            result.append("        ");
                        }
                        if (rightChild(n.getPos()) <= getMaxIndex()) {
                            next.add(this.Heap[leftChild(n.getPos())]);
                            next.add(this.Heap[rightChild(n.getPos())]);
                        } else if (leftChild(n.getPos()) <= getMaxIndex()) {
                            next.add(this.Heap[leftChild(n.getPos())]);
                        }

                    }
                    result.append(textBuffer);
                }
                result.append("\n");
                // Print tree node extensions for next level.
                if(i < treeHeight - 1) {
                    for(heapNode<T> n : curr) {
                        result.append(textBuffer);
                        if(n == null) {
                            result.append("        ");}
                        else {
                            String some = String.format("%s      %s",
                                    leftChild(n.getPos()) <= getMaxIndex() ? (this.Heap[leftChild(n.getPos())] == null ? " " : "/") : "/", rightChild(n.getPos()) <= getMaxIndex() ? (this.Heap[rightChild(n.getPos())] == null ? " " : "\\") : "\\");
                            result.append(some);
                            result.append(textBuffer);
                        }
                    }
                    result.append("\n");
                }
                // Renewing indicators for next run.
                elements *= 2;
                curr = next;
                next = new ArrayList<>(elements);
            }
            result.append("\n\n\n");
            return result.toString();
        }



        /**
         * the class used to implement the nodes of the Maximum-Heap.
         */
        private class heapNode<V>{
            private int key;
            private final V value;
            private int pos;

            /**
             * the constructor of the Heap's node.
             * @param key
             * @param node
             */
            public heapNode(int key, V node, int pos){
                this.key = key;
                this.value = node;
                this.pos = pos;
            }



            /**
             * the getter of the Graph-node which lies under the given heap-Node
             * @return
             */
            public V getValue() {
                return value;
            }


            /**
             * the getter of the key of the heapNode
             * @return
             */
            public int getKey() {
                return key;
            }

            /**
             * this method is the 'setter' of the 'key' field of a heap-Node.
             * this method will be used when we want to *initialize* the key of the given heap-Node/
             * @param key the new 'key' field's value
             */
            public void setKey(int key) {
                this.key = key;
                // TODO: check if this method's necessity
            }




            /**
             * this method returns the position of the given heap-Node in the Heap's array
             * @return
             */
            public int getPos() {
                return pos;
            }




            /**
             * this method sets the position of the given heap-Node in the Heap's array
             * @param pos
             */
            public void setPos(int pos) {
                this.pos = pos;
            }



            /**
             * this method is used to *change* the key of an already existing node in the Heap.
             * @param key the new key of this node
             */
            public void changeKey(int key){
                this.setKey(key);
                Heapify((heapNode<T>) this);
            }
        }
    }



}


