package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.ResultIdentifier;
import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.MatchingCharSequence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class  Node {
	public char value;
	private Set<MatchingCharSequence> refForMatchingCharSeq = new HashSet<>();

	public Set<Node> next;
	public Set<Node> last;
	public boolean isEndNode;
	public ResultIdentifier resultType;
	public boolean shouldBeCaptured;
	
	public Node() {
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public Node(MatchingCharSequence ref) {
		refForMatchingCharSeq.add(ref);
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public Node(char c) {
		this.value = c;
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public Node(char c,MatchingCharSequence ref) {
		this.value = c;
		refForMatchingCharSeq.add(ref);
		this.next = new HashSet<Node>();
		this.last = new HashSet<Node>();
	}

	public void capture(char c){
	    if(shouldBeCaptured) {
            for (MatchingCharSequence seq : refForMatchingCharSeq) {
                if(seq != null) seq.append(c);
            }
        }
	}

	public Set<MatchingCharSequence> getRefForMatchingCharSequences(){
		return refForMatchingCharSeq;
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
	abstract public Result match(char[] ch, Counter index);

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

	/**
	 * This method can be override by child classes to reset any data before comparing next data string.
	 */
	public void reset(){}


}
