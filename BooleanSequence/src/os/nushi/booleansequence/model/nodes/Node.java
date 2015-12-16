package os.nushi.booleansequence.model.nodes;

import java.util.HashSet;
import java.util.Set;

import os.nushi.booleansequence.ExpressionIdentifier;
import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.model.Counter;

public abstract class  Node {
	public char value;
	public Set<Node> links;
	public boolean isEndNode;
	public ExpressionIdentifier resultType;
	
	public Node() {
		this.links = new HashSet<Node>();
	}
	
	public Node(char c) {
		this.value = c;
		this.links = new HashSet<Node>();
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
	
	public boolean isBlankNode() {
		return this.value == '\u0000';
	}
	
	private CharArrList list;
	public boolean capture;
	
	public void captureTo(CharArrList list){
		this.list = list;
	}
	
	public void capture(char c){
		if(capture)	list.add(c);
	}
}
