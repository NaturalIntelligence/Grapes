package os.nushi.jfree;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.nodes.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeFactory {

    public static Node getAnyNode(boolean shouldBeCaptured, CharArrList matchingCharSequence) {
        if(shouldBeCaptured) {
            Node node= new AnyNode(matchingCharSequence){
                @Override
                public Result match(char[] ch, Counter index) {
                    if(super.match(ch, index) == Result.PASSED){
                        refForMatchingCharSeq.add(ch[index.counter]);
                        return Result.PASSED;
                    }
                    return Result.FAILED;
                }
            };
            return node;
        }
        return new AnyNode() ;
    }

    public static Node getRangeNode(char from, char to,boolean shouldBeCaptured,CharArrList matchingCharSequence) {
        if(shouldBeCaptured) {
            RangeNode node = new RangeNode(from,to,matchingCharSequence){
                @Override
                public Result match(char[] ch, Counter index) {
                    if(super.match(ch, index) == Result.PASSED){
                        refForMatchingCharSeq.add(ch[index.counter]);
                        return Result.PASSED;
                    }
                    return Result.FAILED;
                }
            };
            return node;
        }
        return new RangeNode(from,to,null);
    }

    public static Node getNode(char ch,boolean shouldBeCaptured,CharArrList matchingCharSequence){
        if(shouldBeCaptured) {
            NormalNode node = new NormalNode(ch,matchingCharSequence){
                @Override
                public Result match(char[] ch, Counter index) {
                    if(super.match(ch, index) == Result.PASSED){
                        refForMatchingCharSeq.add(ch[index.counter]);
                        return Result.PASSED;
                    }
                    return Result.FAILED;
                }
            };

            return node;
        }
        return new NormalNode(ch,null);
    }


    //TODO: fix when multidigit backreference or when total capture groups are less
    public static BackReferenceNode getBackReferenceNode(int c, List<CharArrList> matchingGroups, int seqCounter) {
        BackReferenceNode node = new BackReferenceNode(c,matchingGroups,seqCounter);
        return node;
    }
}
