package os.nushi.booleansequence;

import java.util.HashSet;
import java.util.Set;

import os.nushi.booleansequence.model.SequenceLength;
import os.nushi.booleansequence.model.nodes.Node;

public class RESequenceUtil {

	public static String toJson(BooleanSequence reSequence){
		Node parentNode = reSequence.startNode;
		Set<Node> pocessedNode = new HashSet<Node>();
		return toJson(parentNode,"{", pocessedNode) + "}";
	}
	
	private static String toJson(Node parentNode, String jSonString, Set<Node> pocessedNode){
		if(parentNode.links.isEmpty()){
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) + "\", \"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode)+"\" }";
		}else{
			jSonString += "\"node\" :{ \"id\" : \""+parentNode.hashCode()+"\", \"value\" : \"" + encode(parentNode.toString()) + "\"";
			if(parentNode.isEndNode) {
				jSonString += ",\"isEndNode\" : true , \"ExpressionType\" : \""+replaceNull(parentNode)+"\"";
			}
			if(!pocessedNode.contains(parentNode)){
				jSonString += ",\"links\":[";
				for(Node node : parentNode.links){
					jSonString += "{";
					jSonString = toJson(node,jSonString,pocessedNode);
					jSonString += "},";
				}
				jSonString += "\b";
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
		reSequenceL.startNode.links.addAll(reSequenceR.startNode.links);
		mergeSequences(reSequenceL.startNode.links);
		reSequenceL.updatePathLength();
	}
	
	private static void mergeSequences(Set<Node> links) {
		Set<Node> toRemov = new HashSet<Node>();
		for (Node nodeL : links) {
			for (Node nodeR : links) {
				if(nodeL != nodeR && nodeL.equals(nodeR) && !toRemov.contains(nodeL)){
					mergeNodes(nodeL, nodeR);
					mergeSequences(nodeL.links);
					toRemov.add(nodeR);
				}
			}
		}
		links.removeAll(toRemov);
	}

	public static void mergeNodes(Node nodeL, Node nodeR) {
		nodeL.links.addAll(nodeR.links);
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
		for(Node node : parentNode.links){
			if(nodesToCount.contains(node)) continue;
			count(node,nodesToCount);
		}
		nodesToCount.addAll(parentNode.links);
	}
	

	public static SequenceLength calculateDepth(BooleanSequence reSequence){
		SequenceLength sequenceLength = new SequenceLength(-1,-1);
		pathLength(reSequence.startNode, 0, sequenceLength);
		return sequenceLength;
	}
	
	private static void pathLength(Node parentNode, int counter,SequenceLength sequenceLength){
		for(Node node : parentNode.links){
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

}
