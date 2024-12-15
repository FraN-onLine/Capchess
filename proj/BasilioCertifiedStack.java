
// Custom implementation of LinkedList
public class BasilioCertifiedStack<Type> {
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

    public BasilioCertifiedStack() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void push(Type data) { //adds at end
        Node<Type> newNode = new Node<>(data);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }


    public Type peek(){
        if (tail != null){
            return tail.data;
        }
        return null;
    }


    public boolean pop() { //adds at end, the only thing my code needs, therefore the only necessary implementation

        if (head == null){
            return false;
        }
        if (head == tail) {
            head = tail = null;
            size --;
            return true;
        }

        Node<Type> temp = head;
        
        // Traverse to the second-to-last element
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