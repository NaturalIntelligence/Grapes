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

/**
 * @author Amit Gupta
 *
 */
public class Util {

	
	public static String toJson(Pattern reSequence){
		os.nushi.jfree.model.nodes.Node parentNode = reSequence.startNode;
		Set<os.nushi.jfree.model.nodes.Node> pocessedNode = new HashSet<os.nushi.jfree.model.nodes.Node>();
		return toJson(parentNode,"{", pocessedNode) + "}";
	}
	
	private static String toJson(os.nushi.jfree.model.nodes.Node parentNode, String jSonString, Set<os.nushi.jfree.model.nodes.Node> pocessedNode){
		if(parentNode.next.isEmpty()){
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) ;
			if(parentNode.isEndNode) {
				jSonString += "\",\"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode);
			}
			jSonString += "\", \"NodeType\" : \"" + parentNode.getClass().getSimpleName() 
			+"\", \"LastNodes\" : \"" + parentNode.last.toString()
			+"\"}";
		}else{
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) 
			+"\", \"NodeType\" : \"" + parentNode.getClass().getSimpleName()
			+"\", \"LastNodes\" : \"" + parentNode.last.toString()
			+ "\"";
			if(parentNode.isEndNode) {
				jSonString += ",\"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode)+"\"";
			}
			if(!pocessedNode.contains(parentNode)){
				jSonString += ",\"links\":[";
				for(os.nushi.jfree.model.nodes.Node node : parentNode.next){
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

	private static String replaceNull(os.nushi.jfree.model.nodes.Node parentNode) {
		return parentNode.resultType == null ? " " : parentNode.resultType.toString();
	}
	
	public static void mergeSequences(Pattern reSequenceL, Pattern reSequenceR){
		reSequenceL.startNode.next.addAll(reSequenceR.startNode.next);
		mergeSequences(reSequenceL.startNode.next);
		reSequenceL.updatePathLength();
	}
	
	private static void mergeSequences(Set<os.nushi.jfree.model.nodes.Node> links) {
		Set<os.nushi.jfree.model.nodes.Node> toRemov = new HashSet<os.nushi.jfree.model.nodes.Node>();
		for (os.nushi.jfree.model.nodes.Node nodeL : links) {
			for (os.nushi.jfree.model.nodes.Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemov.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					mergeSequences(nodeL.next);
					toRemov.add(nodeR);
				}
			}
		}
		links.removeAll(toRemov);
	}

	public static void mergeNodes(os.nushi.jfree.model.nodes.Node nodeL, os.nushi.jfree.model.nodes.Node nodeR) {
		nodeL.next.addAll(nodeR.next);
		nodeL.last.addAll(nodeR.last);
			nodeL.isEndNode |= nodeR.isEndNode;
			if(nodeL.resultType == null)
				nodeL.resultType = nodeR.resultType;	

		//update references
		for (os.nushi.jfree.model.nodes.Node nextNode : nodeR.next) {
			nextNode.last.remove(nodeR);
			nextNode.last.add(nodeL);
		}
	}
	
	public static void mergeDuplicateNodes(Set<os.nushi.jfree.model.nodes.Node> links) {
		Set<os.nushi.jfree.model.nodes.Node> toRemov = new HashSet<os.nushi.jfree.model.nodes.Node>();
		for (os.nushi.jfree.model.nodes.Node nodeL : links) {
			for (os.nushi.jfree.model.nodes.Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemov.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					toRemov.add(nodeR);
				}
			}
		}
		links.removeAll(toRemov);
	}
	
	public static void mergeAllDuplicateNodes(os.nushi.jfree.model.nodes.Node parentNode){
		Util.mergeDuplicateNodes(parentNode.next);
		for(os.nushi.jfree.model.nodes.Node node : parentNode.next){
			mergeAllDuplicateNodes(node);
		}
	}
	
	public static int count(Pattern reSequence){
		os.nushi.jfree.model.nodes.Node parentNode = reSequence.startNode;
		Set<os.nushi.jfree.model.nodes.Node> nodesToCount = new HashSet<os.nushi.jfree.model.nodes.Node>();
		count(parentNode,nodesToCount);
		return nodesToCount.size();
	}
	
	private static void count(os.nushi.jfree.model.nodes.Node parentNode, Set<os.nushi.jfree.model.nodes.Node> nodesToCount){
		for(os.nushi.jfree.model.nodes.Node node : parentNode.next){
			if(nodesToCount.contains(node)) continue;
			count(node,nodesToCount);
		}
		nodesToCount.addAll(parentNode.next);
	}
	

	public static os.nushi.jfree.model.SequenceLength calculateDepth(Pattern reSequence){
		os.nushi.jfree.model.SequenceLength sequenceLength = new os.nushi.jfree.model.SequenceLength(-1,-1);
		pathLength(reSequence.startNode, 0, sequenceLength);
		return sequenceLength;
	}
	
	private static void pathLength(os.nushi.jfree.model.nodes.Node parentNode, int counter, os.nushi.jfree.model.SequenceLength sequenceLength){
		for(os.nushi.jfree.model.nodes.Node node : parentNode.next){
			pathLength(node, counter+1,sequenceLength);
		}
		if(parentNode.isEndNode) {
			calculateMinMax(counter,sequenceLength);
		}
		
	}
	
	private static void calculateMinMax(int counter, os.nushi.jfree.model.SequenceLength sequenceLength) {
		if(sequenceLength.min == -1 && sequenceLength.max == -1){
			sequenceLength.min = counter;
			sequenceLength.max = counter;
		}else{
			if(counter < sequenceLength.min) sequenceLength.min = counter;
			else if(counter > sequenceLength.max) sequenceLength.max = counter;
		}
		
	}

	public static Set<os.nushi.jfree.model.nodes.Node> getEndNodes(Pattern reSequence){
		Set<os.nushi.jfree.model.nodes.Node> nodes = new HashSet<os.nushi.jfree.model.nodes.Node>();
		collectEndNodes(reSequence.startNode, nodes);
		return nodes;
	}
	
	private static void collectEndNodes(os.nushi.jfree.model.nodes.Node parentNode, Set<os.nushi.jfree.model.nodes.Node> nodes) {
		for(os.nushi.jfree.model.nodes.Node node : parentNode.next){
			collectEndNodes(node,nodes);
		}
		if(parentNode.isEndNode) {
			nodes.add(parentNode);
		}
	}
}
