package os.nushi.jfree;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.nodes.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeFactory {

    public static Node getAnyNode(boolean shouldCaptureSubSequence, CharArrList matchingCharSequence) {
        if(shouldCaptureSubSequence) {
            Node node= new AnyNode(matchingCharSequence){
                @Override
                public boolean match(char[] ch, Counter index) {
                    if(super.match(ch, index)){
                        refForMatchingCharSeq.add(ch[index.counter]);
                        return true;
                    }
                    return false;
                }
            };
            return node;
        }
        return new AnyNode() ;
    }

    public static Node getRangeNode(char from, char to,boolean shouldCaptureSubSequence,CharArrList matchingCharSequence) {
        if(shouldCaptureSubSequence) {
            RangeNode node = new RangeNode(from,to,matchingCharSequence){
                @Override
                public boolean match(char[] ch, Counter index) {
                    if(super.match(ch, index)){
                        refForMatchingCharSeq.add(ch[index.counter]);
                        return true;
                    }
                    return false;
                }
            };
            return node;
        }
        return new RangeNode(from,to,null);
    }

    public static Node getNode(char ch,boolean shouldCaptureSubSequence,CharArrList matchingCharSequence){
        if(shouldCaptureSubSequence) {
            NormalNode node = new NormalNode(ch,matchingCharSequence){
                @Override
                public boolean match(char[] ch, Counter index) {
                    if(super.match(ch, index)){
                        refForMatchingCharSeq.add(ch[index.counter]);
                        return true;
                    }
                    return false;
                }
            };

            return node;
        }
        return new NormalNode(ch,null);
    }

    /**
     * Bracket sequence can have either normal node or range node
     * bracket : [a-zA-Z0-9%] = 4 nodes
     * @return
     */
    public static Set<Node> generateNodesForBracketSequence(char[] re,Integer index,boolean shouldCaptureSubSequence,CharArrList matchingCharSequence) {
        Set<Node> newNodes = new HashSet<Node>();
        for(index++;re[index] != ']';index++){
            if(re[index+1]=='-'){
                newNodes.add(NodeFactory.getRangeNode(re[index],re[index+2],shouldCaptureSubSequence,matchingCharSequence));
                index=index+2;
                continue;
            }
            newNodes.add(NodeFactory.getNode(re[index],shouldCaptureSubSequence,matchingCharSequence));
        }
        return newNodes;
    }

    //TODO: fix when multidigit backreference or when total capture groups are less
    public static BackReferenceNode getBackReferenceNode(char c, List<CharArrList> matchingGroups) {
        BackReferenceNode node = new BackReferenceNode(c);
        int position = Integer.parseInt(c+"");
        node.source(matchingGroups.get(position-1));//where to take the input from
        return node;
    }
}
