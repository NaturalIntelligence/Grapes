package os.nushi.booleansequence.matcher;

import os.nushi.booleansequence.BooleanIdentifier;
import os.nushi.booleansequence.BooleanSequence;
import os.nushi.booleansequence.ExpressionIdentifier;
import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.ds.primitive.CharIntMultiMap;
import os.nushi.booleansequence.model.Counter;
import os.nushi.booleansequence.model.nodes.Node;

public class ProgressiveMatcherForCharIntMap {

	private BooleanSequence reSequence;

	public ProgressiveMatcherForCharIntMap() {
		
	}
	
	public ProgressiveMatcherForCharIntMap(BooleanSequence reSequence) {
		this.reSequence = reSequence;
		reset();
	}
	
	public void setSequence(BooleanSequence reSequence){
		this.reSequence = reSequence;
		reset();
	}

	private Node state;
	private Counter index;
	
	public ExpressionIdentifier match(CharIntMultiMap map){
		if(map.size() == 0) return BooleanIdentifier.FAILED;
		for (; index.counter < map.size(); index.counter++ ) {
			Node matchingNode = match(map.getKeys(),index,state);
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
