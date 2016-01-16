package os.nushi.booleansequence;

import static org.junit.Assert.fail;

import org.junit.Test;

import os.nushi.booleansequence.model.nodes.Node;
import os.nushi.booleansequence.model.nodes.NormalNode;

public class ReferenceTest {

	@Test
	public void test() {
		Node a = new NormalNode('a');
		Node b = new NormalNode('b');
		Node c = new NormalNode('c');
		
		System.out.println("" + a + b + c);//abc
		
		/*Node temp = new NormalNode('_');
		temp= a;
		a = b;
		b = c;
		c = temp;*/
		
		swap(a,b,c);
		
		System.out.println("" + a + b + c);//abc
		
	}
	
	private void swap(Node a, Node b, Node c){
		Node temp = new NormalNode('_');
		temp= a;
		a = b;
		b = c;
		c = temp;
		
		System.out.println("" + a + b + c);//bca
	}

	
	@Test
	public void testWrapper() {
		Integer a = new Integer(0);
		
		System.out.println("" + ++a);//abc
		
		
		increase(a);
		
		System.out.println("" + a);//abc
		
	}

	private void increase(Integer a) {
		++a;
	}
	
}
