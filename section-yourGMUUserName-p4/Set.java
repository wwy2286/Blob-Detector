
//
// Task 1. Set<T> class (10%)
// This is used in DisjointSets<T> to store actual data in the same sets
//

//You cannot import additonal items
import java.util.AbstractCollection;
import java.util.Iterator;
/**
 * 
 * @author William Yu
 *
 * @param <T> Set Class structured in a linked list
 * CS310 Fall 2018
 */

public class Set<T> extends AbstractCollection<T> {
	
	private Node<T> head;
	private Node<T> tail;
	private int size;

	/**
	 * Default Constructor
	 */
	public Set() {
		head = null;
		tail = null;
		size = 0;
	}
	
	
	/**
	 * private boolean method to check if an item is added to the list
	 * 
	 * @param t is the item
	 * @return true if the item is added
	 */
	public boolean add(T item) {
		if (item == null)
			return false;
		else if (head == null) {
			head = new Node<T>(item);
			tail = head;
			size++;
			return true;
		} else {
			tail.setNext(new Node<T>(item));
			tail = tail.getNext();
			size++;
			return true;
		}
	
	}
	
	/**
	 * method to add one set to another set
	 * @param other is the set to be added
	 * @return true if the set was added correctly
	 */
	public boolean addAll(Set<T> other) {
		if (other == null) {
			return false;
		} else if (head == null) {
			head = other.head;
			tail = other.tail;
			size = size + other.size();
			return true;
		} else {
			tail.setNext(other.head);
			tail = other.tail;
			size = size + other.size();
			return true;
		}
		
	}
	
	/**
	 * clears the set
	 */
	public void clear() {
		size = 0;
		head = null;
		tail = null;
	}
	
	/**
	 * @return the size of the set
	 */
	public int size() {
		return this.size;
	}
	
	/**
	 * iterator for the set
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			//O(1)
			Node<T> current = head;
			public T next() {
				if (!hasNext())
				{
					throw new NullPointerException("Next can not be null");
				}
				T toReturn = current.getValue();
				current = current.getNext();
				return toReturn;
			}
			
			//O(1)
			public boolean hasNext() {
				return current !=  null;
			}
		};
	}
	
	/**
	 * 
	 * @author Will
	 *	Node class for out linked list set class
	 * @param <T> is the object being added to the node
	 */
	private class Node<T>{
		
		private T value;
		private Node<T> next;
		/**
		 * 
		 * @param value is the data added to the node
		 */
		private Node(T value) {
			this.value = value;
		}
		/**
		 * 
		 * @return the value of the node
		 */
		private T getValue() {
			return value;
		}
		/**
		 * 
		 * @return the next node
		 */
		public Node<T> getNext(){
			return this.next;
		}
		/**
		 * gives the next node
		 * @param next
		 */
		private void setNext(Node<T> next) {
			this.next = next;
		}
	}
	
	//main method just for your testing
	//edit as much as you want
	public static void main(String[] args) {
		Set<Integer> s1 = new Set<Integer>();
		for (int i = 0; i<3 ; i++) {
			s1.add(i);
		}
		
		Set<Integer> s2 = new Set<Integer>();
		for (int i = 3; i<6; i++) {
			s2.add(i);
		}
		System.out.println(s1.toString());
		System.out.println(s1.size());
		System.out.println(s2.toString());
		System.out.println(s2.size());
		s2.addAll(s1);
		System.out.println(s2.toString());
		s1.clear();
		System.out.println(s1.size());
		s1.addAll(s2);
		System.out.println(s1.toString());
		System.out.println(s1.size());
	}
}
