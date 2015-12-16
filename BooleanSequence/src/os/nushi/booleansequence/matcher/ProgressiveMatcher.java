package os.nushi.booleansequence.matcher;

import os.nushi.booleansequence.BooleanIdentifier;
import os.nushi.booleansequence.BooleanSequence;
import os.nushi.booleansequence.ExpressionIdentifier;
import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.model.Counter;
import os.nushi.booleansequence.model.nodes.Node;

public class ProgressiveMatcher implements Matcher{

	private BooleanSequence reSequence;

	public ProgressiveMatcher() {
		
	}
	
	public ProgressiveMatcher(BooleanSequence reSequence) {
		this.reSequence = reSequence;
		reset();
	}
	
	public void setSequence(BooleanSequence reSequence){
		this.reSequence = reSequence;
		reset();
	}

	private Node state;
	private Counter index;
	
	@Override
	public ExpressionIdentifier match(char... ch){
		if(ch.length == 0) return BooleanIdentifier.FAILED;
		for (; index.counter < ch.length; index.counter++ ) {
			Node matchingNode = match(ch,index,state);
			if(matchingNode!=null)
				state = matchingNode;
			else{
				index = new Counter();
				return BooleanIdentifier.FAILED;
			}
		}
		if(state.isEndNode){
			return state.resultType;
		}
		return BooleanIdentifier.MATCHED;
	}
	
	public void reset(){
		for (CharArrList sublist : this.reSequence.matchedSequenceList) {
			sublist.removeAll();
		}
		this.state = this.reSequence.startNode;
		this.index = new Counter();
	}
	
	private Node match(char[] ch,Counter index , Node nd) {
		for (Node node : nd.links) {
			if(node.match(ch,index)) return node;
		}
		return null;
	}
}
