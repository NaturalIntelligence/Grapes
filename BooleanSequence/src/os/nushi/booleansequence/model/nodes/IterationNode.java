package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.model.nodes.Node;
import os.nushi.booleansequence.model.Counter;

public class IterationNode extends Node {

	private int min;
	private int max;
	private Node node;
	private Counter iterations = new Counter();
	private Node startingNode;
	
	public IterationNode(Node node) {
		super('@');
		this.node = node;
		this.last = node.last;
		this.next = node.next;
	}

	public void setIterationRange(int min,int max) {
		this.min = min;
		if(max == -1)
			this.max = Integer.MAX_VALUE;
	}

	public void setIterations(Counter iterations){
		this.iterations = iterations;
	}
	
	public void setStartingNode(Node startingNode){
		this.startingNode = startingNode;
	}
	
	/*@Override
	public Node findMatchingNode(char[] ch,Counter index) {
		if(ch.length == index.counter+1) return this;
		//return any last node to restart subsequence validation
		if(iterations.counter++ < max){
			return startingNode.getCallerNode();
		}
		
		return null;
	}*/
	
	@Override
	public boolean match(char[] ch, Counter index) {
		if(this.node.match(ch,index)){
			iterations.counter++;
			if(iterations.counter >=  min && iterations.counter < max){
				return true;
			}
		}
		return false;
	}

	public Node getNode(){
		if(iterations.counter++ < max){
			return startingNode;
		}
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		return obj instanceof IterationNode && this.min == ((IterationNode) obj).max && this.max == ((IterationNode) obj).max;
	}
	
	@Override
	public String toString() {
		return new String(node.toString() + "{" +min + "," + max + "}");
	}
}
