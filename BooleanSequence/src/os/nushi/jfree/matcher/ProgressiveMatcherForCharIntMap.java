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
package os.nushi.jfree.matcher;

import os.nushi.jfree.Result;
import os.nushi.jfree.ResultIdentifier;
import os.nushi.jfree.Sequence;

public class ProgressiveMatcherForCharIntMap {

	private Sequence reSequence;

	public ProgressiveMatcherForCharIntMap() {
		
	}
	
	public ProgressiveMatcherForCharIntMap(Sequence reSequence) {
		this.reSequence = reSequence;
		reset();
	}
	
	public void setSequence(Sequence reSequence){
		this.reSequence = reSequence;
		reset();
	}

	private os.nushi.jfree.model.nodes.Node state;
	private os.nushi.jfree.model.Counter index;
	
	public ResultIdentifier match(os.nushi.jfree.ds.primitive.CharIntMultiMap map){
		if(map.size() == 0) return Result.FAILED;
		for (; index.counter < map.size(); index.counter++ ) {
			os.nushi.jfree.model.nodes.Node matchingNode = match(map.getKeys(),index,state);
			if(matchingNode!=null)
				state = matchingNode;
			else{
				index = new os.nushi.jfree.model.Counter();
				return Result.FAILED;
			}
		}
		if(state.isEndNode){
			return state.resultType;
		}
		return Result.MATCHED;
	}
	
	public void reset(){
		for (os.nushi.jfree.ds.primitive.CharArrList sublist : this.reSequence.matchingGroups) {
			sublist.removeAll();
		}
		this.state = this.reSequence.startNode;
		this.index = new os.nushi.jfree.model.Counter();
	}
	
	private os.nushi.jfree.model.nodes.Node match(char[] ch, os.nushi.jfree.model.Counter index , os.nushi.jfree.model.nodes.Node nd) {
		for (os.nushi.jfree.model.nodes.Node node : nd.next) {
			if(node.match(ch,index) == Result.PASSED) return node.getNode();
		}
		return null;
	}
}
