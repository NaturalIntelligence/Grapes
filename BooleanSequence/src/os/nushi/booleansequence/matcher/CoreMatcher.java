package os.nushi.booleansequence.matcher;

import os.nushi.booleansequence.BooleanIdentifier;
import os.nushi.booleansequence.BooleanSequence;
import os.nushi.booleansequence.ExpressionIdentifier;
import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.model.Counter;
import os.nushi.booleansequence.model.nodes.Node;

public class CoreMatcher implements Matcher{
	
	private BooleanSequence reSequence;

	public CoreMatcher() {
	}
	
	public CoreMatcher(BooleanSequence reSequence) {
		this.reSequence = reSequence;
		reset();
	}
	
	public void setSequence(BooleanSequence reSequence){
		this.reSequence = reSequence;
		reset();
	}

	public void reset(){
		for (CharArrList sublist : this.reSequence.matchedSequenceList) {
			sublist.removeAll();
		}
	}
	
	@Override
	public ExpressionIdentifier match(char... ch){
		if(ch.length < reSequence.minPathLength || (!this.reSequence.hasVariableLength && ch.length > reSequence.maxPathLength) ) return BooleanIdentifier.FAILED;
		Counter index = new Counter();
		if(this.reSequence.capture) reset();
		Node node = this.reSequence.startNode;
		for (; index.counter < ch.length; index.counter++ ) {
			Node matchingNode = match(ch,index,node);
			if(matchingNode!=null)
				node = matchingNode;
			else
				return BooleanIdentifier.FAILED;
		}
		if(node.isEndNode) return node.resultType;
		return BooleanIdentifier.FAILED;
	}
	
	private Node match(char[] ch,Counter index , Node nd) {
		for (Node node : nd.links) {
			if(node.match(ch,index)) return node;
		}
		return null;
	}
}
