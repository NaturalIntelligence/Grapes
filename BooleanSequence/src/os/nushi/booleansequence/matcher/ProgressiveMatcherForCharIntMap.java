/***
 * The MIT License (MIT)

Copyright (c) 2015 NaturalIntelligence

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
/**
 * @author Amit Gupta
 */
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
