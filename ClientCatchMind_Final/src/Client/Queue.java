package Client;

public class Queue {
	private final int MAX_SIZE = 10000;
	private int size;
	private int rear, front;
	private int[][] arr;
	
	public Queue() {
		size = 0;	rear = 0;	front = 0;
		arr = new int[10000][2];
	}
	public int getSize() {
		return size;
	}
	public boolean isFull() {
		if(size == MAX_SIZE) {
			return true;
		}
		return false;
	}
	public boolean isEmpty() {
		if(size == 0) {
			return true;
		}
		return false;
	}
	public void push(int x, int y) {
		if(isFull()) {
			System.out.println("Queue is full");
			return;
		}
		arr[front][0] = x; arr[front][1] = y;
		front = (front + 1) % MAX_SIZE;
		size++;
	}
	public int[] pop() {
		int[] res = new int[2];
		if(isEmpty()) {
			System.out.println("Queue is empty");
			return null;
		}
		res[0] = arr[rear][0];	res[1] = arr[rear][1];
		rear = (rear + 1) % MAX_SIZE;
		size--;
		return res;
	}
	public int[] front() {
		int[] res = new int[2];
		if(isEmpty()) {
			System.out.println("Queue is empty");
			return null;
		}
		res[0] = arr[rear][0];	res[1] = arr[rear][1];
		return res;
	}
	
}
