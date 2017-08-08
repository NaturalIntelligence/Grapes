package os.nushi.jfree.model.nodes;

import java.util.HashSet;
import java.util.Set;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;

public abstract class  Node {
	public char value;
	public CharArrList refForMatchingCharSeq;
	public Set<Node> next;
	public Set<Node> last;
	public boolean isEndNode;
	public os.nushi.jfree.ExpressionIdentifier resultType;
	
	public Node() {
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public Node(CharArrList ref) {
		refForMatchingCharSeq = ref;
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public Node(char c) {
		this.value = c;
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public Node(char c,CharArrList ref) {
		this.value = c;
		refForMatchingCharSeq = ref;
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	
	@Override
	public String toString() {
		return new String(value == '\u0000' ? "" : value + "");
	}

	/**
	 * Match <b>value</b> char at/from <b>index</b> position in <b>ch[]</b>
	 * @param ch
	 * @param index
	 * @return
	 */
	abstract public boolean match(char[] ch, Counter index);

	/**
	 * when both objects are instance of same class and their values match.
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return this.getClass().isInstance(obj) && this.value == ((Node) obj).value;
	}
	
	public boolean isJointNode() {
		return this.value == '\u0000';
	}
	
	public Node getNode(){
		return this;
	}
	/*
	public Node findMatchingNode(char[] ch,Counter index) {
		for (Node node : this.next) {
			if(node.match(ch,index)) {
				index.counter++;
				return node;
			}
		}
		index.counter++;
		return null;
	}

	Node callerNode;
	
	public void setCallerNode(Node node){
		if(node != this)
			this.callerNode = node;
	}
	
	public Node getCallerNode(){
		return callerNode;
	}*/
}
