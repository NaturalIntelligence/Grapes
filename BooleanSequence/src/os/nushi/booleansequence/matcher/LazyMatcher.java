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

import static os.nushi.booleansequence.BooleanIdentifier.FAILED;
import static os.nushi.booleansequence.BooleanIdentifier.MATCHED;

import os.nushi.booleansequence.BooleanSequence;
import os.nushi.booleansequence.ExpressionIdentifier;
import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.model.Counter;
import os.nushi.booleansequence.model.nodes.Node;
import os.nushi.booleansequence.model.nodes.SubSequenceNode;

public class LazyMatcher implements Matcher{

	private BooleanSequence reSequence;

	public LazyMatcher() {
		
	}
	
	public LazyMatcher(BooleanSequence reSequence) {
		this.reSequence = reSequence;
		state = this.reSequence.startNode;
	}
	
	public void setSequence(BooleanSequence reSequence){
		this.reSequence = reSequence;
		state = this.reSequence.startNode;
	}
	
	public void reset(){
		for (CharArrList sublist : this.reSequence.matchedSequenceList) {
			sublist.removeAll();
		}
		this.state = this.reSequence.startNode;
	}
	
	Node state;
	
	@Override
	public ExpressionIdentifier match(char... ch){
		Counter index = new Counter();
		return match(index,ch);
	}
	
	@Override
	public ExpressionIdentifier match(Counter index, char[] ch) {
		if(ch.length == 0) return FAILED;
		for (; index.counter < ch.length; index.counter++ ) {
			Node matchingNode = match(ch,index,state);
			if(matchingNode!=null)
				state = matchingNode;
			else
				return FAILED;;
		}
		if(state.isEndNode) return state.resultType;
		return MATCHED;
	}

	Counter subIndex = new Counter();
	private Node match(char[] ch,Counter index , Node nd) {
		for(;subIndex.counter < nd.next.size(); subIndex.counter++){
			Node node = nd.next.get(subIndex.counter);
			if(node instanceof SubSequenceNode){
				LazyMatcher lazyMatcher = getInstance(node);
				ExpressionIdentifier match = lazyMatcher.match(index,ch);
				if(match != FAILED){
					return nd;
				}
				continue;
			}
			if(node.match(ch,index)) {
				return node.getNode();
			}
		}
		return null;
	}
	
	LazyMatcher subMatcher;
	private LazyMatcher getInstance(Node node){
		if(subMatcher == null)
			subMatcher =  new LazyMatcher(((SubSequenceNode)node).sequence);
		
		return subMatcher;
	}
}
