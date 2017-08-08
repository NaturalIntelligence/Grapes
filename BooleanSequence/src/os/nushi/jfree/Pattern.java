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
package os.nushi.jfree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.ds.primitive.CharStack;
import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.matcher.ProgressiveMatcher;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.SequenceLength;
import os.nushi.jfree.model.nodes.*;
import os.nushi.jfree.util.CharArrayUtil;
import os.nushi.jfree.util.CharUtil;

import static os.nushi.jfree.BooleanIdentifier.PASSED;

//TODO : possible improvements

//https://swtch.com/~rsc/regexp/regexp1.html
//to implement *,+ use stacking. so whenever assertion fails take the node from stack and start from back
//Lazy/Progressive check supporting in //1 cases too

//Support for backslash chars
/**
 * @author Amit Gupta
 *
 */
public class Pattern {

	private char[] re;
	private ExpressionIdentifier expressionIdentifier;

	public Pattern(String str) {
		this(str.toCharArray());
	}

	public Pattern(String str, ExpressionIdentifier expressionIdentifier) {
		this(str.toCharArray(),expressionIdentifier);
	}

	public Pattern(char[] re) {
		this(re, PASSED);
	}

	public Pattern(char[] re, ExpressionIdentifier expressionIdentifier) {
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
	private boolean shouldCaptureSubSequence;
	private boolean isItSubSequence;
	public boolean hasVariableLength;
	Integer index;

	public Pattern compile(){
		Node lastNode = null;
		Pattern subsequence = null;
		index = 0;
		for (index=0; index< re.length; index++) {

			if(re[index] == '('){
				CharArrList matchedSequence = new CharArrList();
				matchedSequenceList.add(matchedSequence);//It'll store the result of captured sequence

				subsequence = getMeASubSequence();
				subsequence.matchedSequence = matchedSequence;
				subsequence.compile();

				Util.mergeNodes(currentNode, subsequence.startNode);

				//forward linking
				lastNode = currentNode;
				currentNode = subsequence.endNode;
			}else if(re[index] == '|'){
				closeTheCurrentSequence();
				//Start new sequence
				currentNode = this.startNode;
			}else if(re[index] == '['){
				lastNode = linkNextNode(generateNodesForBracketSequence());
			}else if(re[index] == '?'){
				linkJointNode(lastNode);
			}else if(re[index] == '.'){
				lastNode = linkNextNode(getAnyNode());
			}/*else if(re[index] == '+'){//1 or more
				//convert end node of last sequence to iteration node

				if(currentNode.isJointNode()){
					//Go one more step backward
					Set<Node> lastNodes = new HashSet<Node>(currentNode.last);
					currentNode.last.clear();
					for (Node node : lastNodes) {
						IterationNode iNode = getIterationNode(node,1,-1,node);
						iNode.setStartingNode(iNode);
						//update references
						for (Node lastNode : node.last) {
							lastNode.next.remove(node);
							lastNode.next.add(iNode);
						}
						currentNode.last.add(iNode);
					}
				}else{
					Node iNode = getIterationNode(currentNode,1,-1,currentNode);
					//update references
					for (Node lastNode : currentNode.last) {
						lastNode.next.remove(currentNode);
						lastNode.next.add(iNode);
					}
					currentNode = iNode;
				}


				hasVariableLength = true;
				isItSubSequence = null;
			}else if(re[i] == '{'){//TODO
				int start = ++i;
				//read until } is found
				for(;re[i] != '}';i++){
					if(!CharUtil.isDigit(re[i])){
						System.out.println("Invalid Boolean Expression");
					}
				}

				int num = Integer.parseInt(new String(CharArrayUtil.subArray(re, start, i-1)));
			}*/else if(re[index] == '\\'){//add next char as plain Node
				char c = re[++index];

				if(CharUtil.isDigit(c)){ //if backreference is \\10 but total capture groups are 9 then 0 should be treated as normal char.
					lastNode = linkNextNode(getBackReferenceNode(c));
				}else{
					lastNode = linkNextNode(getNode(c));
				}
			}else{
				lastNode = linkNextNode(getNode(re[index]));
			}

		}
		closeTheCurrentSequence();
		return this;
	}



	private void closeTheCurrentSequence() {
		if(!this.isItSubSequence) {
			markEndNode(currentNode);
		}else{
			currentNode.next.add(endNode);
			endNode.last.add(currentNode);
		}
	}

	private void markEndNode(Node parentNode) {
		parentNode.isEndNode = true;
		parentNode.resultType = expressionIdentifier;
	}

	private Pattern getMeASubSequence() {
		Pattern subsequence;
		int startIndex = index+1;

		CharStack stack = new CharStack();
		while(true){
			if(re[index] == '('){
				stack.push('(');
			}else if(re[index] == ')'){
				stack.pop();
				if(stack.size() == 0) break;
			}
			index++;
		}

		subsequence = new Pattern(CharArrayUtil.subArray(re, startIndex,index-1));
		subsequence.isItSubSequence = true;
		subsequence.shouldCaptureSubSequence = this.capture;
		return subsequence;
	}

	/**
	 * Bracket sequence can have either normal node or range node
	 * bracket : [a-zA-Z0-9%] = 4 nodes
	 * @return
	 */
	private Set<Node> generateNodesForBracketSequence() {
		Set<Node> newNodes = new HashSet<Node>();
		for(index++;re[index] != ']';index++){
			if(re[index+1]=='-'){
				newNodes.add(getRangeNode(re[index],re[index+2]));
				index=index+2;
				continue;
			}
			newNodes.add(getNode(re[index]));
		}
		return newNodes;
	}

	/**
	 * lastNode -> current => lastNode ->current; lastNode->joint; current->joint
	 * @param lastNode
	 * @return
	 */
	private void linkJointNode(Node lastNode) {
		NormalNode jointNode = new NormalNode(); //blank node

		link(currentNode,jointNode);
		link(lastNode,jointNode);

		currentNode = jointNode;//go forward
	}

	private void link(Node current, Node next) {
		current.next.add(next);
		next.last.add(current);
	}


	/**
	 * 1. Sets currentNode as last Node of newNode.<br/>
	 * 2. Mark currentNode as lastNode<br/>
	 * 3. Mark newNode as currentNode
	 * @param newNode
	 * @return
	 */
	private Node linkNextNode(Node newNode) {
		link(currentNode,newNode);

		//go forward
		Node lastNode = currentNode;
		currentNode = newNode;

		return lastNode;
	}


	private Node linkNextNode(Set<Node> newNodes) {
		return linkNextNode(newNodes, new NormalNode());
	}


	private Node linkNextNode(Set<Node> newNodes, Node jointNode) {
		currentNode.next.addAll(newNodes);
		for (Node node : newNodes) {
			//if(!node.isJointNode())
			node.last.add(currentNode);
			node.next.add(jointNode);
			jointNode.last.add(node);
		}

		//go forward
		Node lastNode = currentNode;
		currentNode = jointNode;

		return lastNode;
	}

	//TODO: fix when multidigit backreference or when total capture groups are less
	private BackReferenceNode getBackReferenceNode(char c) {
		hasVariableLength = true;
		BackReferenceNode lazynode = new BackReferenceNode(c);
		int position = Integer.parseInt(c+"");
		lazynode.source(matchedSequenceList.get(position-1));//where to take the input from
		return lazynode;
	}

	/*private IterationNode getIterationNode(Node node, int min, int max, Node startNode) {
		IterationNode iterationNode = new IterationNode(node);
		iterationNode.setIterationRange(min, max);
		iterationNode.setStartingNode(startNode);
		return iterationNode;
	}*/

	private Node getAnyNode() {
		if(shouldCaptureSubSequence) {
			Node node= new AnyNode(){
				@Override
				public boolean match(char[] ch, Counter index) {
					if(super.match(ch, index)){
						matchedSequence.add(ch[index.counter]);
						return true;
					}
					return false;
				}
			};
			return node;
		}
		return new AnyNode() ;
	}

	private Node getRangeNode(char from, char to) {
		if(shouldCaptureSubSequence) {
			RangeNode node = new RangeNode(from,to){
				@Override
				public boolean match(char[] ch, Counter index) {
					if(super.match(ch, index)){
						matchedSequence.add(ch[index.counter]);
						return true;
					}
					return false;
				}
			};
			return node;
		}
		return new RangeNode(from,to);
	}

	private Node getNode(char ch){
		if(shouldCaptureSubSequence) {
			NormalNode node = new NormalNode(ch){
				@Override
				public boolean match(char[] ch, Counter index) {
					if(super.match(ch, index)){
						matchedSequence.add(ch[index.counter]);
						return true;
					}
					return false;
				}
			};

			return node;
		}
		return new NormalNode(ch);
	}
	/**
	 * Removes blank nodes
	 */
	public Pattern minimize(){
		Node parentNode = this.startNode;
		removeBlankNodes(parentNode);
		Util.mergeAllDuplicateNodes(parentNode);
		System.gc();
		updatePathLength();
		return this;
	}

	private void removeBlankNodes(Node parentNode) {
		Set<Node> toRemove = new HashSet<Node>();

		for(Node node : parentNode.next){
			if(!node.next.isEmpty())
				removeBlankNodes(node);

			if(node.isJointNode()){
				if(node.isEndNode){
					markEndNode(parentNode);
					toRemove.add(node);
				}else{
					toRemove.add(node);
				}
			}
		}
		parentNode.next.removeAll(toRemove);
		for(Node node : toRemove){
			parentNode.next.addAll(node.next);
			for(Node nextNode : node.next){
				nextNode.last.remove(node);
				nextNode.last.addAll(node.last);
			}
		}
		Util.mergeDuplicateNodes(parentNode.next);
	}


	public int minPathLength;
	public int maxPathLength;
	
	public void updatePathLength(){
		SequenceLength depth = Util.calculateDepth(this);
		minPathLength = depth.min;
		maxPathLength = depth.max;
	}
	
	public Pattern merge(Pattern sequence){
		Util.mergeSequences(this, sequence);
		return this;
	}
	
	public CoreMatcher getCoreMatcher(){
		return new CoreMatcher(this);
	}
	
	public ProgressiveMatcher getProgressiveMatcher(){
		return new ProgressiveMatcher(this);
	}
}

