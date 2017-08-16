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
import os.nushi.jfree.model.MatchingCharSequence;
import os.nushi.jfree.model.nodes.*;
import os.nushi.jfree.util.CharArrayUtil;
import os.nushi.jfree.util.CharUtil;
import os.nushi.jfree.util.Pair;
import os.nushi.jfree.util.PathLengthCalculator;

import java.util.*;

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

	public Sequence compile(){
	    try {
            List<CharArrList> matchingGroups = new ArrayList<>();
            Sequence sequence = compile(re, false, false,null);
            //sequence.setGroups(groups);
            //System.out.println(Util.toJson(sequence));
            minimize(sequence);
            Pair<Integer> pathLength = new PathLengthCalculator().length(re);
            sequence.minPathLength = pathLength.x;
            sequence.maxPathLength = pathLength.y;

            return sequence;
        }catch (Exception e){
	        throw new InvalidExpression("Kindly check the regular expression once again");
        }
	}
	private int seqCounter;
    //private Map<Integer,MatchingCharSequence> groups = new HashMap<>();

	Sequence compile(char[] re,boolean isItSubSequence, boolean shouldBeCaptured,MatchingCharSequence mcs){
		Node lastNode = null;

		//endNode = new NormalNode();
		//index = 0;
		Sequence sequence = new Sequence(mcs);
		Node currentNode = sequence.startNode;

		for (int index=0; index< re.length; index++) {

			if(re[index] == '('){
				char[] groupStr = extractGroupString(re,index);
                seqCounter++;//every open parenthis from left to right
                int localSeqCounter = seqCounter;
                MatchingCharSequence matchingCharSequence = new MatchingCharSequence(sequence.getMatchingCharSequence());
                //groups.put(localSeqCounter,matchingCharSequence);
                Sequence subSequence = compile(groupStr,true,true,matchingCharSequence);
                SequenceEndNode seqEndNode = new SequenceEndNode();
                seqEndNode.setSeqNumber(localSeqCounter );
                seqEndNode.setMatchingSeqence(matchingCharSequence);
                link(subSequence.endNode,seqEndNode);

                //start node of any sequence is blank. So it can be merged to current node i.e last node of parent sequence.
				Util.mergeNodes(currentNode, subSequence.startNode);

				//go forward
				lastNode = currentNode;
				currentNode = seqEndNode;
				index+=groupStr.length+1;
			}else if(re[index] == '+'){ // ->b->b*->c
                NormalNode blankLinkNode = new NormalNode();
                currentNode.next.add(currentNode); //b*: b->b
                link(currentNode,blankLinkNode);
                //link(lastNode,blankLinkNode);

                //go forward
                lastNode = currentNode;
                currentNode = blankLinkNode;
			}else if(re[index] == '*'){//a->b*->c;a->c

                NormalNode blankLinkNode = new NormalNode();
                currentNode.next.add(currentNode); //b*: b->b
                link(currentNode,blankLinkNode);
                link(lastNode,blankLinkNode);

                //go forward
                lastNode = currentNode;
                currentNode = blankLinkNode;

			}else if(re[index] == '|'){
				sequence.endNode = closeTheCurrentSequence(currentNode,isItSubSequence);
				//Start new sequence
				currentNode = sequence.startNode;
			}else if(re[index] == '['){

                char[] bracketSeq = extractBracketsequence(re,index);
                index+=bracketSeq.length+1;


				Set<Node> nodes = generateNodesForBracketSequence(bracketSeq,shouldBeCaptured, sequence.getMatchingCharSequence());
				//lastNode = linkNextNode(currentNode,nodes);
                Node jointNode = new NormalNode();
                currentNode.next.addAll(nodes);
                for (Node node : nodes) {
                    //if(!node.isJointNode())
                    node.last.add(currentNode);
                    node.next.add(jointNode);
                    jointNode.last.add(node);
                }

                //go forward
                lastNode = currentNode;
                currentNode = jointNode;

			}else if(re[index] == '?'){
                NormalNode jointNode = new NormalNode(); //blank node

                link(currentNode,jointNode);
                link(lastNode,jointNode);

                currentNode = jointNode;//go forward

			}else if(re[index] == '.'){
				Node newNode = new AnyNode(shouldBeCaptured,sequence.getMatchingCharSequence());
				//lastNode = linkNextNode(currentNode,newNode);

                link(currentNode,newNode);

                //go forward
                lastNode = currentNode;
                currentNode = newNode;

			}else if(re[index] == '\\'){
				char c = re[++index];

				if(CharUtil.isDigit(c)){
					int len = (seqCounter+"").length();
					String backRefNum = c+"";
					for(int i=1;i<len;i++){
						char nextDigit = re[i+index];
						if(CharUtil.isDigit(nextDigit)){
							backRefNum += nextDigit;
						}else{
							break;
						}
					}
					if(Integer.parseInt(backRefNum) > seqCounter && backRefNum.length() > 1){
						backRefNum = backRefNum.substring(0,backRefNum.length()-2);
					}

					index +=backRefNum.length()-1;

					Node newNode = new BackReferenceNode(Integer.parseInt(backRefNum),seqCounter);
					//lastNode = linkNextNode(currentNode,newNode);

                    link(currentNode,newNode);

                    //go forward
                    lastNode = currentNode;
                    currentNode = newNode;

				}else{
                    Node newNode = new NormalNode(c,shouldBeCaptured,sequence.getMatchingCharSequence());
					//lastNode = linkNextNode(currentNode,newNode);
                    link(currentNode,newNode);

                    //go forward
                    lastNode = currentNode;
                    currentNode = newNode;
				}
			}else{
			    Node newNode = new NormalNode(re[index],shouldBeCaptured,sequence.getMatchingCharSequence());
				//lastNode = linkNextNode(currentNode,newNode);
                link(currentNode,newNode);

                //go forward
                lastNode = currentNode;
                currentNode = newNode;
			}

		}

		if(sequence.endNode != null){
            Node jointNode = new NormalNode();
            Node anotherEndNode = closeTheCurrentSequence(currentNode,isItSubSequence);

            link(sequence.endNode,jointNode);
            link(anotherEndNode,jointNode);

            //go forward
            lastNode = currentNode;
            currentNode = jointNode;
            sequence.endNode = jointNode;
        }else{
            sequence.endNode = closeTheCurrentSequence(currentNode,isItSubSequence);
        }
		return sequence;
	}

    private char[] extractBracketsequence(char[] re, int index) {
        int j=index;
        for (; re[j] != ']' ;j++){
            if(re[j] == '\\' && re[j+1] == ']'){
                j++;
                continue;
            }else if(re[j+1]=='-' && re[j+2] != ']'){
                j+=2;
            }
        }

        return CharArrayUtil.subArray(re, index+1, j -1);
    }

    /**
	 * Bracket sequence can have either normal node or range node
	 * bracket : [a-zA-Z0-9%] = 4 traversedNodes
	 * @return
	 */
	//TODO: test for [a\\]]
	private Set<Node> generateNodesForBracketSequence(char[] re, boolean shouldBeCaptured, MatchingCharSequence matchingCharSequence) {
		Set<Node> newNodes = new HashSet<Node>();
		for(int index=0; index< re.length && re[index] != ']'; index++){
		    if(re[index]=='\\'){
		        if(re[index+1]==']'){
                    newNodes.add(new NormalNode(']',shouldBeCaptured,matchingCharSequence));
		            index++;
                }
            }else if(index < re.length -1 && re[index+1]=='-'){
				newNodes.add(new RangeNode(re[index], re[index+2],shouldBeCaptured,matchingCharSequence));
				index=index+2;
			}else {
                newNodes.add(new NormalNode(re[index], shouldBeCaptured, matchingCharSequence));
            }
		}
		return newNodes;
	}

	private Node closeTheCurrentSequence(Node currentNode,boolean isItSubSequence) {
		if(!isItSubSequence) {
			/*if(currentNode instanceof SequenceEndNode){
                findAndMarkEndNode(currentNode);
            }else{
                markEndNode(currentNode);
            }*/
            markEndNode(currentNode);
		}
        return currentNode;
    }

	private void markEndNode(Node parentNode) {
		parentNode.isEndNode = true;
		parentNode.resultType = resultIdentifier;
	}

	/**
	 * Fetch string/group between ()
	 * @return
     * @param re
     * @param index
	 */
	private char[] extractGroupString(char[] re, Integer index) {
		int startIndex = index +1;

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

		return CharArrayUtil.subArray(re, startIndex, index -1);
	}



	/**
	 * lastNode -> current => lastNode ->current; lastNode->joint; current->joint
	 * @param lastNode
	 * @return
	 */
	private void linkJointNode(Node lastNode,Node currentNode) {
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
	/*private Node linkNextNode(Node currentNode,Node newNode) {

		link(currentNode,newNode);

		//go forward
		Node lastNode = currentNode;
		currentNode = newNode;

		return lastNode;
	}*/


	/*private Node linkNextNode(Node currentNode,Set<Node> newNodes) {
		return linkNextNode(currentNode,newNodes, new NormalNode());
	}


	private Node linkNextNode(Node currentNode,Set<Node> newNodes, Node jointNode) {
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
	}*/



	/*private IterationNode getIterationNode(Node node, int min, int max, Node startNode) {
		IterationNode iterationNode = new IterationNode(node);
		iterationNode.setIterationRange(min, max);
		iterationNode.setStartingNode(startNode);
		return iterationNode;
	}*/



	/**
	 * Removes blank traversedNodes
	 */
	public void minimize(Sequence sequence){
		Node parentNode = sequence.startNode;
		removeBlankNodes(parentNode);
		//merge remaining traversedNodes
        sequence.nodes = traversedNodes;
        Util.mergeAllDuplicateNodes(parentNode);
		System.gc();

	}

	Set<Node> traversedNodes = new HashSet<>();
	/**
	 * Remove jointing traversedNodes and merge the links for last and next traversedNodes.
	 * @param parentNode
	 */
	private void removeBlankNodes(Node parentNode) {
		Set<Node> toRemove = new HashSet<Node>();

		for(Node node : parentNode.next){
			if(!node.next.isEmpty()) {
                /*if(!traversedNodes.contains(node)) {
                    traversedNodes.add(node);
					removeBlankNodes(node);
				}*/
                if(!node.equals(parentNode)){
                    removeBlankNodes(node);
                }
			}

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

		if(toRemove.size() > 0){
			removeBlankNodes(parentNode);
		}
		//if (!parentNode.next.contains(parentNode)) {
			Util.mergeDuplicateNodes(parentNode.next);
		//}
	}

}

