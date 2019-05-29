// Task 1. DisjointSets class (15%)

// Hint: you can use the DisjointSets from your textbook
// but it must be changed also union the the actual data together

import java.util.ArrayList;

/**
 * 
 * @author Will
 *
 * @param <T> takes in any object for our DisjointSets CS310 Fall 2018
 */
public class DisjointSets<T> {

	private int[] s; // the sets
	private ArrayList<Set<T>> sets; // the actual data for the sets

	/**
	 * default constructor that adds a list into the disjoint set
	 * 
	 * @param data is the array data in your disjoint sets
	 */
	public DisjointSets(ArrayList<T> data) {
		if (data == null) {
			throw new NullPointerException("Data in DisjointSets can not be null");
		}
		s = new int[data.size()];
		sets = new ArrayList<Set<T>>();
		for (int i = 0; i < data.size(); i++) {
			Set<T> newSet = new Set<T>();
			this.sets.add(newSet);
			// this.sets.set(i, newSet);
			if (data.get(i) == null) {
				throw new NullPointerException("A null data can not be added to the Disjoint Set");
			}
			this.sets.get(i).add(data.get(i));
			s[i] = -1;
		}

	}

	/**
	 * method to check root
	 * 
	 * @param root
	 * @return true the param is a root
	 */
	private boolean checkRoot(int root) {
		if (root < 0 || root >= sets.size()) {
			throw new IndexOutOfBoundsException("The root is out of the bounds of the array");
		}
		if (s[root] >= 0) {
			throw new IllegalArgumentException("the input is not a root");
		}
		return (s[root] < 0);
	}

	/**
	 * Compute the union of two sets using rank union by size
	 * 
	 * @param root1
	 * @param root2
	 * @return return the index of the largets set after unioned
	 */
	public int union(int root1, int root2) {
		if (!checkRoot(root1) || !checkRoot(root2) || root1 == root2) {
			throw new IllegalArgumentException();
		}

		if (s[root1] <= s[root2]) {
			s[root1] = s[root1] + s[root2];
			s[root2] = root1;
			sets.get(root1).addAll(sets.get(root2));
			sets.get(root2).clear();
			return root1;
		} else {
			s[root2] = s[root2] + s[root1];
			s[root1] = root2;
			sets.get(root2).addAll(sets.get(root1));
			sets.get(root1).clear();
			return root2;
		}

	}

	/**
	 * find method using path compression
	 * 
	 * @param x is the index
	 * @return the root where x is
	 */
	public int find(int x) {
		if (x < 0 || x >= sets.size()) {
			throw new IndexOutOfBoundsException("The index is out of the bounds of the array");
		}

		if (s[x] < 0)
			return x;
		else {
			int temp = s[x];
			int temp2;
			int root;

			while (s[temp] >= 0) {
				temp = s[temp];
			}
			root = temp;
			temp = s[x];
			s[x] = root;
			while (s[temp] >= 0) {
				temp2 = temp;
				temp = s[temp];
				s[temp2] = root;
			}

			return root;
		}
	}

	/**
	 * get method for disjoint set
	 * 
	 * @param root
	 * @return items in the set where the root is
	 */
	public Set<T> get(int root) {
		return sets.get(root);
	}


	public static void main(String[] args) {
		ArrayList<Integer> arr = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			arr.add(i);

		DisjointSets<Integer> ds = new DisjointSets<>(arr);

		System.out.println(ds.find(0)); // should be 0

		System.out.println(ds.find(1)); // should be 1

		System.out.println(ds.union(0, 1)); // should be 0

		System.out.println(ds.find(0)); // should be 0

		System.out.println(ds.find(1)); // should be 0 System.out.println("-----");
		System.out.println(ds.find(0)); // should be 0
		System.out.println(ds.find(2)); // should be 2
		System.out.println(ds.union(0, 2)); // should be 0
		System.out.println(ds.find(0)); // should be 0
		System.out.println(ds.find(2)); // should be 0 System.out.println("-----");
		// Note: AbstractCollection provides toString() method using the iterator //see:
		// //https://docs.oracle.com/javase/8/docs/api/java/util/AbstractCollection.html#
		// toString-- // so your iterator in Set needs to work for this to print out
		// correctly
		ds.union(6, 5);
		ds.union(8, 7);
		ds.union(8, 9);
		ds.union(6, 8);
		System.out.println(ds.get(0)); // should be [0, 1, 2]
		System.out.println(ds.get(1)); // should be []

		System.out.println(ds.get(3)); // should be [3]

		System.out.println(ds.sets);
		for (int i = 0; i < ds.s.length; i++) {
			System.out.print("[" + ds.s[i] + "]");

		}
		System.out.println(ds.find(5));
		for (int i = 0; i < ds.s.length; i++) {
			System.out.print("[" + ds.s[i] + "]");

		}

	}
}
