package os.nushi.jfree.model;

public class Counter {
	public Counter() {
	}
	
	public Counter(int c) {
		counter = c;
	}
	
	public int counter;

	@Override
	public String toString() {
		return counter + "";
	}
}
