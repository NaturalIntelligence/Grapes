package os.nushi.booleansequence.model.nodes;

import java.util.HashSet;
import java.util.Set;

import os.nushi.booleansequence.ExpressionIdentifier;
import os.nushi.booleansequence.model.Counter;

public abstract class  Node {
	public char value;
	public Set<Node> next;
	public Set<Node> last;
	public boolean isEndNode;
	public ExpressionIdentifier resultType;
	
	public Node() {
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}
	
	public Node(char c) {
		this.value = c;
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}
	
	@Override
	public String toString() {
		return new String(value == '\u0000' ? "" : value + "");
	}
	
	abstract public boolean match(char[] ch, Counter index);
	
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
