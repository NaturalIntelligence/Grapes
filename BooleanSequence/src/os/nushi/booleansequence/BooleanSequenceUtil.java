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

import java.util.HashSet;
import java.util.Set;

import os.nushi.booleansequence.model.SequenceLength;
import os.nushi.booleansequence.model.nodes.Node;

/**
 * @author Amit Gupta
 *
 */
public class BooleanSequenceUtil {

	
	public static String toJson(BooleanSequence reSequence){
		Node parentNode = reSequence.startNode;
		Set<Node> pocessedNode = new HashSet<Node>();
		return toJson(parentNode,"{", pocessedNode) + "}";
	}
	
	private static String toJson(Node parentNode, String jSonString, Set<Node> pocessedNode){
		if(parentNode.next.isEmpty()){
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) 
			+ "\", \"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode)
			+"\", \"NodeType\" : \"" + parentNode.getClass().getCanonicalName() +"\"}";
		}else{
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) 
			+"\", \"NodeType\" : \"" + parentNode.getClass().getCanonicalName()
			+ "\"";
			if(parentNode.isEndNode) {
				jSonString += ",\"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode)+"\"";
			}
			if(!pocessedNode.contains(parentNode)){
				jSonString += ",\"links\":[";
				for(Node node : parentNode.next){
					jSonString += "{";
					jSonString = toJson(node,jSonString,pocessedNode);
					jSonString += "},";
				}
				jSonString = jSonString.substring(0,jSonString.length()-1);
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
	
	public static void mergeSequences(BooleanSequence reSequenceL,BooleanSequence reSequenceR){
		reSequenceL.startNode.next.addAll(reSequenceR.startNode.next);
		mergeSequences(reSequenceL.startNode.next);
		reSequenceL.updatePathLength();
	}
	
	private static void mergeSequences(Set<Node> links) {
		Set<Node> toRemov = new HashSet<Node>();
		for (Node nodeL : links) {
			for (Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemov.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					mergeSequences(nodeL.next);
					toRemov.add(nodeR);
				}
			}
		}
		links.removeAll(toRemov);
	}

	public static void mergeNodes(Node nodeL, Node nodeR) {
		nodeL.next.addAll(nodeR.next);
		nodeL.isEndNode |= nodeR.isEndNode;
		if(nodeL.resultType == null)
			nodeL.resultType = nodeR.resultType;
	}
	
	public static void mergeDuplicateNodes(Set<Node> links) {
		Set<Node> toRemov = new HashSet<Node>();
		for (Node nodeL : links) {
			for (Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemov.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					toRemov.add(nodeR);
				}
			}
		}
		links.removeAll(toRemov);
	}
	
	public static int count(BooleanSequence reSequence){
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
	

	public static SequenceLength calculateDepth(BooleanSequence reSequence){
		SequenceLength sequenceLength = new SequenceLength(-1,-1);
		pathLength(reSequence.startNode, 0, sequenceLength);
		return sequenceLength;
	}
	
	private static void pathLength(Node parentNode, int counter,SequenceLength sequenceLength){
		for(Node node : parentNode.next){
			pathLength(node, counter+1,sequenceLength);
		}
		if(parentNode.isEndNode) {
			calculateMinMax(counter,sequenceLength);
		}
		
	}
	
	private static void calculateMinMax(int counter, SequenceLength sequenceLength) {
		if(sequenceLength.min == -1 && sequenceLength.max == -1){
			sequenceLength.min = counter;
			sequenceLength.max = counter;
		}else{
			if(counter < sequenceLength.min) sequenceLength.min = counter;
			else if(counter > sequenceLength.max) sequenceLength.max = counter;
		}
		
	}

	public static Set<Node> getEndNodes(BooleanSequence reSequence){
		Set<Node> nodes = new HashSet<Node>();
		collectEndNodes(reSequence.startNode, nodes);
		return nodes;
	}
	
	private static void collectEndNodes(Node parentNode, Set<Node> nodes) {
		for(Node node : parentNode.next){
			collectEndNodes(node,nodes);
		}
		if(parentNode.isEndNode) {
			nodes.add(parentNode);
		}
	}
}
