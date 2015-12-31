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
package os.nushi.booleansequence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.ds.primitive.CharStack;
import os.nushi.booleansequence.matcher.CoreMatcher;
import os.nushi.booleansequence.matcher.ProgressiveMatcher;
import os.nushi.booleansequence.model.SequenceLength;
import os.nushi.booleansequence.model.nodes.AnyNode;
import os.nushi.booleansequence.model.nodes.LazyNode;
import os.nushi.booleansequence.model.nodes.Node;
import os.nushi.booleansequence.model.nodes.NormalNode;
import os.nushi.booleansequence.model.nodes.RangeNode;
import os.nushi.booleansequence.util.CharArrayUtil;
import os.nushi.booleansequence.util.CharUtil;

//TODO : possible improvements
// a) combine all the nodes to allOfNode during minimize process : a-> b -> c => abc
// b) combine [abc] to single anyOfNode 
//https://swtch.com/~rsc/regexp/regexp1.html
//to implement *,+ use stacking. so whenever assertion fails take the node from stack and start from back
//Lazy/Progressive check supporting in //1 cases too
//support for {},+,* 
//Support for backslash chars
/**
 * @author Amit Gupta
 *
 */
public class BooleanSequence {

	private char[] re;
	private ExpressionIdentifier expressionIdentifier;

	public BooleanSequence(String str) {
		this(str.toCharArray());
	}
	
	public BooleanSequence(String str,ExpressionIdentifier expressionIdentifier) {
		this(str.toCharArray(),expressionIdentifier);
	}
	
	public BooleanSequence(char[] re) {
		this(re, BooleanIdentifier.PASSED);
	}
	
	public BooleanSequence(char[] re,ExpressionIdentifier expressionIdentifier) {
		this.re = re;
		this.expressionIdentifier = expressionIdentifier;
		this.startNode = new NormalNode();
		this.currentNode = this.startNode;
		this.endNode = new NormalNode();
	}
	
	public Node startNode;
	private Node currentNode;
	public Node endNode;
	public boolean capture;
	private CharArrList matchedSequence;
	public List<CharArrList> matchedSequenceList = new ArrayList<CharArrList>();
	private boolean subsequencecapture;
	private boolean subsequence;
	public boolean hasVariableLength;
	
	public BooleanSequence compile(){
		Node beforeNode = null;
		for (int i=0; i< re.length; i++) {
			
			if(re[i] == '('){
				int startIndex = i+1;
				CharStack stack = new CharStack();
				while(true){
					if(re[i] == '('){
						stack.push('(');
					}else if(re[i] == ')'){
						stack.pop();
						if(stack.size() == 0) break;
					}
					i++;
				}
				
				BooleanSequence subsequence = new BooleanSequence(CharArrayUtil.subArray(re, startIndex,i-1));
				subsequence.subsequence = true;
				CharArrList matchedSequence = new CharArrList();
				matchedSequenceList.add(matchedSequence);
				subsequence.matchedSequence = matchedSequence;
				subsequence.subsequencecapture = this.capture;
				subsequence.compile();
				this.currentNode.links.addAll(subsequence.startNode.links);
				beforeNode = currentNode;
				this.currentNode = subsequence.endNode;
			}else if(re[i] == '|'){
				this.currentNode.links.add(endNode);
				if(!this.subsequence) {
					this.currentNode.isEndNode = true;
					this.currentNode.resultType = expressionIdentifier;
				}
				this.currentNode = this.startNode;
			}else if(re[i] == '['){
				NormalNode blankNode = new NormalNode();
				for(i++;re[i] != ']';i++){
					if(re[i+1]=='-'){
						Node node = new RangeNode(re[i],re[i+2]);
						node.links.add(blankNode);
						this.currentNode.links.add(node);
						i=i+2;
						continue;
					}
					Node node = getNode(re[i]);
					node.links.add(blankNode);
					this.currentNode.links.add(node);
				}
				beforeNode = this.currentNode;
				this.currentNode = blankNode;
			}else if(re[i] == '?'){
				NormalNode blankNode = new NormalNode();
				currentNode.links.add(blankNode);
				beforeNode.links.add(blankNode);
				currentNode = blankNode;
			}else if(re[i] == '.'){
				AnyNode node = new AnyNode();
				currentNode.links.add(node);
				beforeNode = currentNode;
				currentNode = node;
			}else if(re[i] == '{'){
				int start = ++i;
				//read until } is found
				for(;re[i] != '}';i++){
					if(!CharUtil.isDigit(re[i])){
						System.out.println("Invalid Boolean Expression");
					}
				}
				
				int num = Integer.parseInt(new String(CharArrayUtil.subArray(re, start, i-1)));
				System.out.println(num);
				/*AnyNode node = new AnyNode();
				currentNode.links.add(node);
				beforeNode = currentNode;
				currentNode = node;*/
			}else if(re[i] == '\\'){
				//add next char as plain Node
				char c = re[++i];
				Node node = null;
				if(CharUtil.isDigit(c)){
					hasVariableLength = true;
					LazyNode lazynode = new LazyNode(c);
					int position = Integer.parseInt(c+"");
					lazynode.source(matchedSequenceList.get(position-1));
					node = lazynode;
				}else{
					node = getNode(c);
				}
				currentNode.links.add(node);
				beforeNode = currentNode;
				currentNode = node;
			}else{
				Node node = getNode(re[i]);
				currentNode.links.add(node);
				beforeNode = currentNode;
				currentNode = node;
			}
			
		}
		this.currentNode.links.add(endNode);
		if(!this.subsequence) {
			this.currentNode.isEndNode = true;
			this.currentNode.resultType = expressionIdentifier;
		}
		return this;
	}
	
	private Node getNode(char ch){
		if(subsequencecapture) {
			NormalNode node = new NormalNode(ch);
			node.capture = true;
			node.captureTo(matchedSequence);
			return node;
		}
		return new NormalNode(ch);
	}
	/**
	 * Removes blank nodes
	 */
	public BooleanSequence minimize(){
		Node parentNode = this.startNode;
		removeBlankNodes(parentNode);
		System.gc();
		updatePathLength();
		return this;
	}

	private void removeBlankNodes(Node parentNode) {
		Set<Node> toRemove = new HashSet<Node>();
		
		for(Node node : parentNode.links){
			if(!node.links.isEmpty())
				removeBlankNodes(node);
			
			if(node.isBlankNode()){
				if(node.isEndNode){
					parentNode.isEndNode = true;
					parentNode.resultType = expressionIdentifier;
					toRemove.add(node);
				}else{
					toRemove.add(node);
				}
			}
		}
		parentNode.links.removeAll(toRemove);
		for(Node node : toRemove){
			parentNode.links.addAll(node.links);
		}
		RESequenceUtil.mergeDuplicateNodes(parentNode.links);
	}

	public int minPathLength;
	public int maxPathLength;
	
	public void updatePathLength(){
		SequenceLength depth = RESequenceUtil.calculateDepth(this);
		minPathLength = depth.min;
		maxPathLength = depth.max;
	}
	
	public BooleanSequence merge(BooleanSequence sequence){
		RESequenceUtil.mergeSequences(this, sequence);
		return this;
	}
	
	public CoreMatcher getCoreMatcher(){
		return new CoreMatcher(this);
	}
	
	public ProgressiveMatcher getProgressiveMatcher(){
		return new ProgressiveMatcher(this);
	}
}

