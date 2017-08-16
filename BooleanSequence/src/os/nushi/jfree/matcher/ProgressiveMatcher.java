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
import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.MatchingCharSequence;
import os.nushi.jfree.model.nodes.BackReferenceNode;
import os.nushi.jfree.model.nodes.Node;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.nodes.SequenceEndNode;

import java.util.HashMap;
import java.util.Map;

public class ProgressiveMatcher implements Matcher {

	private Sequence reSequence;
	private Map<Integer,MatchingCharSequence> groups;

	public ProgressiveMatcher(Sequence reSequence) {
		groups = new HashMap<>();
		this.reSequence = reSequence;
		reset();
	}
	
	public void setSequence(Sequence reSequence){
		this.reSequence = reSequence;
		reset();
	}

	private Node state;
	private Counter index;
	
	@Override
	public ResultIdentifier match(char... ch){
		if(ch.length == 0) return Result.FAILED;
		for (; index.counter < ch.length; index.counter++ ) {
			Node matchingNode = match(ch,index,state);
			if(matchingNode!=null)
				state = matchingNode;
			else{
				index = new Counter();
				return Result.FAILED;
			}
		}
		if(state.isEndNode)
			return state.resultType;
		else {
			Node n = processSeqEndNodes(state);
			if(n != null) {
				SequenceEndNode snd = (SequenceEndNode) n;
				this.groups.put(snd.getSeqNumber(),snd.getMatchingSequence());
				return n.resultType;
			}
			else if(state.next.isEmpty())
				return Result.FAILED;
			else
				return Result.MATCHED;
		}
	}
	
	public void reset(){
		for (Integer groupnum: groups.keySet()) {
			groups.get(groupnum).getMatchingSequence().removeAll();
		}
		groups = new HashMap<>();
		this.state = this.reSequence.startNode;
		this.index = new Counter();
	}
	
	private Node match(char[] ch, Counter index , Node nd) {
		for (Node node : nd.next) {
			if(node instanceof SequenceEndNode){
				SequenceEndNode snd = (SequenceEndNode) node;
				//CapturedGroup should be set only when control passes from seqEndNode
				this.groups.put(snd.getSeqNumber(),snd.getMatchingSequence());
				//node.reset();
				index.counter--;
				return node.getNode();
			}else if(node instanceof BackReferenceNode){
				((BackReferenceNode) node).setGroups(groups);
			}
			Result result = node.match(ch,index);
			if( result == Result.MATCHED)
				return nd;
			else if(result == Result.PASSED)
				return node.getNode();
		}
		return null;
	}

	private Node processSeqEndNodes(Node node){
		for (Node n : node.next) {
			if(n instanceof SequenceEndNode){
				if(n.isEndNode){
					return n;
				}
				return processSeqEndNodes(n);
			}
		}
		return null;
	}

	@Override
	public Map<Integer, MatchingCharSequence> getGroups() {
		return groups;
	}
}
