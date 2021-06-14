/*
You must NOT change the signatures of classes/methods in this skeleton file.
You are required to implement the methods of this skeleton file according to the requirements.
You are allowed to add classes, methods, and members as required.
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

// TODO: add a double pointer to each edge
// TODO: implement the deletion method

/**
 * This class represents a graph that efficiently maintains the heaviest neighborhood over edge addition and
 * vertex deletion.
 *
 */
public class Graph {
    private final hashMap nodesHash;
    private final maxHeap nodesHeap;

    /**
     * Initializes the graph on a given set of nodes. The created graph is empty, i.e. it has no edges.
     * You may assume that the ids of distinct nodes are distinct.
     *
     * @param nodes - an array of node objects
     */
    public Graph(Node [] nodes){
        nodesHash = new hashMap(nodes.length, 0.5f);
        nodesHeap = new maxHeap(nodes.length);

        for (Node node : nodes) { // adding all of the nodes to the Hash-Map and to the Maximum-Heap
            nodesHash.addItem(node.getId(), node);
            nodesHeap.addNode(node);
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
        // this process will also cause a heapifying process in the Maximum-Heap of the Graph - since their keys in the Heap (their vicinity weight) have been changed
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
            nodesHash.removeNode(node.getVicinityWeight());
            DoublyLinkedList.DoublyLinkedCell currCell = node.Neighbours.head;

            for (int i=0; i<node.Neighbours.getSize(); i++) { // iterating between all of the edges 'node' was connected with, using the Neighbours of 'node'
                DoublyLinkedList.DoublyLinkedCell linkedCell = currCell.getParallel(); // getting the parallel form of the edge in order to remove it from the other end of the edge (which is not 'node')
                currCell.getRepresentativeList().deleteCell(currCell); // deleting the edge and removing 'node' from the Neighbours list of its Neighbour
                Node currNode = (Node) currCell.getItem(); // getting the Neighbour's form as a Node
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
        for (maxHeap.heapNode node : nodesHeap.Heap) {
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

        result = "is the graph empty? " + this.isEmpty() + "\n";
        result += this.nodesHash.toString();
        this.nodesHeap.printHeap();

        return result;
        // TODO: implement this method. implement a 'toString' to the Maximum-Heap and to the HashMap.
    }




    /**
     * This class represents a node in the graph.
     */
    public static class Node{
        private final int id;
        private final int weight;
        private int vicinityWeight;
        private final DoublyLinkedList<Node> Neighbours;
        private maxHeap.heapNode heapForm;
        private hashMap.hashCell hashForm;

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
        public void setHeapForm(maxHeap.heapNode heapForm) {
            this.heapForm = heapForm;
        }



        /**
         * this method is used to add as a new field - the pointer of the node's form in the hash-Map of the Graph.
         * @param hashForm the pointer we update the field 'hashForm' to.
         */
        public void setHashForm(hashMap.hashCell hashForm) {
            this.hashForm = hashForm;
        }


        /**
         * this method returns the amount of Neighbours that the node has - Or in other words, the amount of edges that the node partakes in.
         * @return
         */
        public int getNeighboursAmount(){
            return this.Neighbours.getSize();
        }


        /**
         * the method called when we want to print an Object of type 'Node'
         * the implementation of this method is the basic preview we want for a Node to make basic validity tests
         * @return
         */
        @Override
        public String toString(){
            return "";
            //TODO: implement this method
        }
    }


    /**
     * a Doubly Linked List implemented by basic pointers logic.
     * @param <T> the class of each value in each cell of the list.
     */
    public static class DoublyLinkedList<T>{
        private DoublyLinkedCell tail;
        private DoublyLinkedCell head;
        private int size;

        /**
         * this method is used to add a new item to the Doubly Linked List
         * <p>
         * this method has a time-Complexity of O(1)
         * </p>
         * @pre item must not exist in the List
         * @param item
         */
        public void addItem(T item){
            size += 1;
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
         * performed in O(1)
         * </p>
         * @pre item must be the direct pointer to the cell that holds the item we want deleted
         * @param item
         */
        public void deleteCell(DoublyLinkedCell item){
            DoublyLinkedCell Prev = item.prev;
            DoublyLinkedCell Next = item.next;

            if (size == 1) { // if the item is the only item in the list
                this.head = null;
                this.tail = null;
                size = 0;
                return;
            } else if (this.head == item) { // if the item is the head of the list
                this.head = Next;
            } else if (this.tail == item) { // if the item is the tail of the list
                this.tail = Prev;
            }

            Prev.next = Next;
            Next.prev = Prev;
            size -= 1;
        }


        /**
         * this method returns the length of this Linked List
         * @return
         */
        public int getSize() {
            return size;
        }

        /**
         * this class is the class of a single Cell in the list. The list contains ceveral Cells which are linked to each other.
         * this class is mainly to keep the implementation of a linked list available for all kinds of lists that we want, including a linked list of Graph-Nodes.
         */
        public class DoublyLinkedCell{
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
            public DoublyLinkedList getRepresentativeList(){
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
    public class hashMap{
        private final int p; // the prime number of the Hash function
        private final hashCell[] table;
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
        public void addItem(int key, Node item){
            int hashedKey = hash(key);
            hashCell curr = table[hashedKey];

            if (curr == null) {
                table[hashedKey] = new hashCell(hashedKey, key, item, null);
                return;
            }

            hashCell newItem = new hashCell(hashedKey, key, item, curr);
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.setNext(newItem);
        }




        /**
         * returns the node in the hashMap who holds the given key
         * @param key
         * @return
         */
        public Node get(int key) {
            int hashedKey = hash(key);
            hashCell curr = table[hashedKey];
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
            hashCell prev = null;
            hashCell curr = table[hashedKey];
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
            String sb;
            sb = "hashMap's array size is: " + this.table.length;
            return sb;
        }


        /**
         * the class used to implement the Cell class of the items in the Hash Map.
         * The Cell holds a pointer to the actual node in the Graph that has the given key, and in addition, holds a pointer to its cell in the Maximum-Heap.
         */
        public class hashCell{
            private final int hash;
            private final int key;
            private Node value;
            private hashCell next;

            /**
             * the constructor of a Cell in the Hash-Map.
             * @param hash
             * @param key
             * @param value
             * @param next
             */
            public hashCell(int hash, int key, Node value, hashCell next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
                value.setHashForm(this);
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
            public Node getValue() { return this.value; }


            /**
             * the cell's value's setter
             * @param newValue
             */
            public void setValue(Node newValue){ value = newValue; }


            /**
             * the 'setter' of the 'next' field of the cell
             * @param next
             */
            public void setNext(hashCell next) {
                this.next = next;
            }
        }
    }


    /**
     * Maximum Heap containing cells T
     */
    public class maxHeap{
        // this will be the array that represents the Maximum-Heap
        private final heapNode[] Heap;
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
        private int smallestChild(int pos) {
            int pos1 = leftChild(pos);
            int pos2 = rightChild(pos);

            if (isLeaf(pos) || pos > getMaxIndex()) {
                return -1;
            }
            if (pos1 <= getMaxIndex() && pos2 <= getMaxIndex()) {
                if (Heap[pos1].key < Heap[pos2].key) {
                    return pos1;
                } else {
                    return pos2;
                }
            } else if (pos1 <= getMaxIndex()) {
                return pos1;
            } else {
                return pos2;
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
            heapNode tmp = Heap[pos1];
            Heap[pos1] = Heap[pos2];
            Heap[pos2] = tmp;
        }




        /**
         * this method is used to add a node into the heap. it also performs the Heapifying-process used to maintain the balance and requirements of a Maximum-Heap
         * @pre the node must not exist in the Heap
         * @param node
         */
        public void addNode(Node node) {
            Heap[size++] = new heapNode(node.getVicinityWeight(), node, size);
            int curr = getMaxIndex();

            if (curr == 0) {
                return;
            }

            do {
                swap(curr, parent(curr));
                curr = parent(curr);
                if (curr == 0) {
                    break;
                }
            } while (Heap[curr].key > Heap[parent(curr)].key);
        }





        /**
         * this method is used when we want to delete a certain node from the whole Graph, and therefore we delete it from the Maximum-Heap as well.
         * @pre the node must exist in the Heap
         * @param node a pointer to the node in the Graph.
         */
        public void deleteNode(Node node){
            if (size == 1) {
                Heap[0] = null;
                return;
            }
            heapNode currNode = node.heapForm;
            heapNode newNode = Heap[size-1];
            swap(currNode.getPos(), newNode.getPos());
            Heap[--size] = null;
            Heapify(newNode);
        }





        /**
         * this method is used to Heapify either UP or DOWN a node who has got its key changed.
         * <p>
         * this method is a recursive-wrapper-method to the recursive method: 'Heapify_Rec', which does the actual Heapifyingment.
         * </p>
         * @param node
         */
        public void Heapify(heapNode node) {
            int pos = node.getPos();
            Heapify_Rec(pos);
        }


        /**
         * this is the recursive-method that 'Heapify' wraps.
         * @param pos the index at the Heap's array of the node we want to validate and Heapify.
         */
        private void Heapify_Rec(int pos){
            if (Heap[pos].key > Heap[parent(pos)].key) {
                swap(pos, parent(pos));
                Heapify_Rec(parent(pos));
                return;
            }

            int smallerChild = smallestChild(pos);

            if (smallerChild < 0) {
                return;
            } else if (Heap[pos].key < Heap[smallerChild].key) {
                swap(pos, smallerChild);
                Heapify_Rec(smallerChild);
            } else {
                if (smallerChild == leftChild(pos) && rightChild(pos) <= getMaxIndex()) {
                    if (Heap[pos].key < Heap[rightChild(pos)].key) {
                        swap(pos, rightChild(pos));
                        Heapify_Rec(rightChild(pos));
                    }
                } else if (smallerChild == rightChild(pos) && leftChild(pos) <= getMaxIndex()) {
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
        public heapNode getMax(){
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
        public String printHeap(){
            // TODO: check out why this method isn't working
            if (this.Heap.length == 0) {
                System.out.println("Heap is empty.");
            }
            int treeHeight = (int) (Math.log(this.getSize()) / Math.log(2)) + 1;
            int treeWidth = (int) Math.pow(2, treeHeight);
            List<heapNode> curr = new ArrayList<heapNode>(1), next = new ArrayList<heapNode>(2);
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
                for(heapNode n : curr) {
                    System.out.print(textBuffer);
                    if(n == null) {

                        System.out.print("        ");
                        next.add(null);
                        next.add(null);
                    } else {
                        if (n != null) {
                            System.out.printf("(%6d)", n.getKey());
                        }
                        else {
                            System.out.printf("        ");
                        }
                        if (rightChild(n.getPos()) <= getMaxIndex()) {
                            next.add(this.Heap[leftChild(n.getPos())]);
                            next.add(this.Heap[rightChild(n.getPos())]);
                        } else if (leftChild(n.getPos()) <= getMaxIndex()) {
                            next.add(this.Heap[leftChild(n.getPos())]);
                        }

                    }
                    System.out.print(textBuffer);
                }
                System.out.println();
                // Print tree node extensions for next level.
                if(i < treeHeight - 1) {
                    for(heapNode n : curr) {
                        System.out.print(textBuffer);
                        if(n == null)
                            System.out.print("        ");
                        else
                            System.out.printf("%s      %s",
                                    leftChild(n.getPos()) <= getMaxIndex() ? (this.Heap[leftChild(n.getPos())] == null ? " " : "/") : "/", rightChild(n.getPos()) <= getMaxIndex() ? (this.Heap[rightChild(n.getPos())] == null ? " " : "\\") : "\\");
                        System.out.print(textBuffer);
                    }
                    System.out.println();
                }
                // Renewing indicators for next run.
                elements *= 2;
                curr = next;
                next = new ArrayList<heapNode>(elements);
            }
            System.out.println("\n\n");
            return "";
        }



        /**
         * the class used to implement the nodes of the Maximum-Heap.
         */
        public class heapNode{
            private int key;
            private final Node value;
            private int pos;

            /**
             * the constructor of the Heap's node.
             * @param key
             * @param node
             */
            public heapNode(int key, Node node, int pos){
                this.key = key;
                this.value = node;
                this.pos = pos;
                node.setHeapForm(this);
            }



            /**
             * the getter of the Graph-node which lies under the given heap-Node
             * @return
             */
            public Node getValue() {
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
                // TODO: check if this method is actually necessary
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
                Heapify(this);
            }
        }
    }



}


