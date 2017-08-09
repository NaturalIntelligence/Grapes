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

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.ds.primitive.CharStack;
import os.nushi.jfree.model.nodes.Node;
import os.nushi.jfree.model.nodes.NormalNode;
import os.nushi.jfree.util.CharArrayUtil;
import os.nushi.jfree.util.CharUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static os.nushi.jfree.Result.PASSED;

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
	private ResultIdentifier resultIdentifier;

	public Pattern(String str) {
		this(str.toCharArray());
	}

	public Pattern(String str, ResultIdentifier resultIdentifier) {
		this(str.toCharArray(), resultIdentifier);
	}

	public Pattern(char[] re) {
		this(re, PASSED);
	}

	public Pattern(char[] re, ResultIdentifier resultIdentifier) {
		this.re = re;
		this.resultIdentifier = resultIdentifier;
	}

	Integer index;

	public Sequence compile(){
		List<CharArrList> matchingGroups = new ArrayList<>();
		Sequence sequence = compile(re,false,false, matchingGroups);
		minimize(sequence);

		return sequence;

	}
	Node currentNode,endNode;
	//TODO: make wrapper compile and call comppile();minimize();
	Sequence compile(char[] re,boolean isItSubSequence, boolean shouldBeCaptured,List<CharArrList> mG ){
		Node lastNode = null;

		endNode = new NormalNode();
		index = 0;
		Sequence sequence = new Sequence(mG,shouldBeCaptured);
		currentNode = sequence.startNode;

		for (index=0; index< re.length; index++) {

			if(re[index] == '('){
				char[] groupStr = extractGroupString();
				Pattern subPattern = new Pattern(groupStr);
				Sequence subSequence = subPattern.compile(groupStr,true,true,mG);

				Util.mergeNodes(currentNode, subSequence.startNode);

				//go forward
				lastNode = currentNode;
				currentNode = subPattern.endNode;
			}else if(re[index] == '|'){
				closeTheCurrentSequence(isItSubSequence);
				//Start new sequence
				currentNode = sequence.startNode;
			}else if(re[index] == '['){
				Set<Node> nodes = generateNodesForBracketSequence(shouldBeCaptured, sequence.matchingCharSequence);
				lastNode = linkNextNode(nodes);
			}else if(re[index] == '?'){
				linkJointNode(lastNode);
			}else if(re[index] == '.'){
				Node node = NodeFactory.getAnyNode(shouldBeCaptured,sequence.matchingCharSequence);
				lastNode = linkNextNode(node);
			}else if(re[index] == '\\'){
				char c = re[++index];

				if(CharUtil.isDigit(c)){
					Node node = NodeFactory.getBackReferenceNode(c,mG);
					lastNode = linkNextNode(node);
				}else{
					lastNode = linkNextNode(NodeFactory.getNode(c,shouldBeCaptured,sequence.matchingCharSequence));
				}
			}else{
				lastNode = linkNextNode(NodeFactory.getNode(re[index],shouldBeCaptured,sequence.matchingCharSequence));
			}

		}
		closeTheCurrentSequence(isItSubSequence);
		return sequence;
	}

	/**
	 * Bracket sequence can have either normal node or range node
	 * bracket : [a-zA-Z0-9%] = 4 nodes
	 * @return
	 */
	private Set<Node> generateNodesForBracketSequence(boolean shouldBeCaptured,CharArrList matchingCharSequence) {
		Set<Node> newNodes = new HashSet<Node>();
		for(index++;re[index] != ']';index++){
			if(re[index+1]=='-'){
				newNodes.add(NodeFactory.getRangeNode(re[index],re[index+2],shouldBeCaptured,matchingCharSequence));
				index=index+2;
				continue;
			}
			newNodes.add(NodeFactory.getNode(re[index],shouldBeCaptured,matchingCharSequence));
		}
		return newNodes;
	}

	private void closeTheCurrentSequence(boolean isItSubSequence) {
		if(!isItSubSequence) {
			markEndNode(currentNode);
		}else{
			link(currentNode,endNode);
		}
	}

	private void markEndNode(Node parentNode) {
		parentNode.isEndNode = true;
		parentNode.resultType = resultIdentifier;
	}

	/**
	 * Fetch string/group between ()
	 * @return
	 */
	private char[] extractGroupString() {
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

		return CharArrayUtil.subArray(re, startIndex,index-1);
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

	/**
	 * Set current.next = next
	 * <br/>next.last=current
	 * @param current
	 * @param next
	 */
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



	/*private IterationNode getIterationNode(Node node, int min, int max, Node startNode) {
		IterationNode iterationNode = new IterationNode(node);
		iterationNode.setIterationRange(min, max);
		iterationNode.setStartingNode(startNode);
		return iterationNode;
	}*/


	/**
	 * Removes blank nodes
	 */
	public void minimize(Sequence sequence){
		Node parentNode = sequence.startNode;
		removeBlankNodes(parentNode);
		Util.mergeAllDuplicateNodes(parentNode);
		System.gc();
		sequence.updatePathLength();
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

	/*public CoreMatcher getCoreMatcher(){
		return new CoreMatcher(this);
	}
	
	public ProgressiveMatcher getProgressiveMatcher(){
		return new ProgressiveMatcher(this);
	}*/
}

