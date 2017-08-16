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

import java.util.HashSet;
import java.util.Set;
import os.nushi.jfree.model.nodes.Node;
import os.nushi.jfree.model.SequenceLength;
import os.nushi.jfree.model.nodes.SequenceEndNode;

/**
 * @author Amit Gupta
 *
 */
public class Util {

	
	public static String toJson(Sequence reSequence){
		Node parentNode = reSequence.startNode;
		Set<Node> pocessedNode = new HashSet<Node>();
		return toJson(parentNode,"{", pocessedNode, false) + "}";
	}
	
	private static String toJson(Node parentNode, String jSonString, Set<Node> pocessedNode, boolean dontGoDeep){
		if(parentNode.next.isEmpty()){
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) ;
			if(parentNode.isEndNode) {
				jSonString += "\",\"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode);
			}

			jSonString += "\", \"NodeType\" : \"" + parentNode.getClass().getSimpleName() 
			+"\", \"LastNodes\" : \"" + parentNode.last.toString()	+"\"";
			if(parentNode instanceof SequenceEndNode){
				jSonString += ",\"isSeqEndNode\" : true ";
			}
			jSonString += "}";
		}else{
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) 
			+"\", \"NodeType\" : \"" + parentNode.getClass().getSimpleName()
			+"\", \"LastNodes\" : \"" + parentNode.last.toString()
			+ "\"";
			if(parentNode.isEndNode) {
				jSonString += ",\"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode)+"\"";
			}
			if(parentNode instanceof SequenceEndNode){
				jSonString += "\"isSeqEndNode\" : true ";
			}
			if(!pocessedNode.contains(parentNode)){
				jSonString += ",\"links\":[";
				if(!dontGoDeep) {
					for (Node node : parentNode.next) {
						jSonString += "{";
						if (node.equals(parentNode))
							jSonString = toJson(node, jSonString, pocessedNode, true);
						else {
							jSonString = toJson(node, jSonString, pocessedNode, false);
						}
						jSonString += "},";
					}
					jSonString = jSonString.substring(0,jSonString.length()-1);
				}else{
					jSonString += "...";
				}
				jSonString += "]";
			}
			jSonString += "}";
		}
		pocessedNode.add(parentNode);
		return jSonString;
	}

	private static String encode(String value) {
		return value.equals("\"") ? "\\\"" : value;
	}

	private static String replaceNull(Node parentNode) {
		return parentNode.resultType == null ? " " : parentNode.resultType.toString();
	}
	
	public static void mergeSequences(Sequence reSequenceL, Sequence reSequenceR){
		reSequenceL.startNode.next.addAll(reSequenceR.startNode.next);
		mergeSequences(reSequenceL.startNode.next);


		reSequenceL.minPathLength = reSequenceL.minPathLength > reSequenceR.minPathLength ? reSequenceR.minPathLength : reSequenceL.minPathLength;
		reSequenceL.maxPathLength = reSequenceL.maxPathLength > reSequenceR.maxPathLength ? reSequenceL.maxPathLength : reSequenceR.maxPathLength;
	}
	
	private static void mergeSequences(Set<Node> links) {
		Set<Node> toRemove = new HashSet<Node>();
		for (Node nodeL : links) {
			for (Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemove.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					mergeSequences(nodeL.next);
					toRemove.add(nodeR);
				}
			}
		}
		links.removeAll(toRemove);
	}

	public static void mergeNodes(Node nodeL, Node nodeR) {
		nodeL.next.addAll(nodeR.next);
		nodeL.last.addAll(nodeR.last);
		nodeL.isEndNode |= nodeR.isEndNode;
		nodeL.shouldBeCaptured |= nodeR.shouldBeCaptured;
		nodeL.getRefForMatchingCharSequences().addAll(nodeR.getRefForMatchingCharSequences());
		if(nodeL.resultType == null)
			nodeL.resultType = nodeR.resultType;

		//update references
		for (Node nextNode : nodeR.next) {
			nextNode.last.remove(nodeR);
			nextNode.last.add(nodeL);
		}
	}
	
	public static void mergeDuplicateNodes(Set<Node> links) {
		Set<Node> toRemove = new HashSet<Node>();
		for (Node nodeL : links) {
			for (Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemove.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					toRemove.add(nodeR);
				}
			}
		}
		links.removeAll(toRemove);
	}

	/**
	 *
	 * @param parentNode
	 */
	public static void mergeAllDuplicateNodes(Node parentNode){
		Util.mergeDuplicateNodes(parentNode.next);
		for(Node node : parentNode.next){
			if(!node.equals(parentNode))
				mergeAllDuplicateNodes(node);
		}
	}
	
	public static int count(Sequence reSequence){
		Node parentNode = reSequence.startNode;
		Set<Node> nodesToCount = new HashSet<Node>();
		count(parentNode,nodesToCount);
		return nodesToCount.size();
	}
	
	private static void count(Node parentNode, Set<Node> nodesToCount){
		for(Node node : parentNode.next){
			if(nodesToCount.contains(node)) continue;
			count(node,nodesToCount);
		}
		nodesToCount.addAll(parentNode.next);
	}

}
