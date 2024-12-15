
public class BasilioCertifiedLinkedList<Type>{
    private Node<Type> head;
    private Node<Type> tail;
    private int size;

    static class Node<Type> {
        Type data;
        Node<Type> next;

        Node(Type data) {
            this.data = data;
            this.next = null;
        }
    }

    public BasilioCertifiedLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void addatEnd(Type data) { //adds at end, the only thing my code needs, therefore the only necessary implementation
        Node<Type> newNode = new Node<>(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public void addatStart(Type data) { //adds at end, the only thing my code needs, therefore the only necessary implementation
        Node<Type> newNode = new Node<>(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            newNode = head;
        }
        size++;
    }

    public Type getatPos(int pos){
        if(pos == 0){
            return null;
        }

        Node<Type> temp = head;
        int i = 1;
        while (temp.next != null && i < pos) {
            temp = temp.next;
            i++;
        }
        

        return temp.data;
    }

    public Type getatEnd(){
        if (tail != null){
            return tail.data;
        }
        return null;
    }

    public boolean removeatPos(int pos) {
        if (head == null) return false;

        Node<Type> temp = head;

        int i = 1;
        while (temp.next != null && i < pos) {
            temp = temp.next;
            i++;
        }
        temp.next = temp.next.next;
        if (temp.next == null) tail = temp;
        size--;
              
        return false;
    }

    public boolean removeatEnd() {

        if (head == null){
            return false;
        }
        if (head == tail) {
            head = tail = null;
            size --;
            return true;
        }

        Node<Type> temp = head;
        
        // Traverse to the second-to-last element, it doesnt use null because i allow null as a value
         while (temp.next != tail) { 
        temp = temp.next;
         }

        // Remove the last element
        temp.next = null;
        tail = temp; // Update the tail reference
        size--;
        return true;


    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    


}