package os.nushi.jfree;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.SequenceLength;
import os.nushi.jfree.model.nodes.Node;
import os.nushi.jfree.model.nodes.NormalNode;

import java.util.ArrayList;
import java.util.List;

public class Sequence {

    public Node startNode;

    public CharArrList matchingCharSequence;
    public List<CharArrList> matchingGroups;

    public Sequence(List<CharArrList> matchingGroups, boolean shouldCapture){
        this.startNode = new NormalNode();
        //This will store each matching char while traversing through a group
        //so that once the group traversing is finished, it's value can be assigned to matchingGroups.
        this.matchingGroups = matchingGroups;
        if(shouldCapture){
            this.matchingCharSequence = new CharArrList();
            this.matchingGroups.add(matchingCharSequence);//It'll store the result of captured sequence
        }
    }



    public int minPathLength;
    public int maxPathLength;

    public void updatePathLength(){
        SequenceLength depth = Util.calculateDepth(this);
        minPathLength = depth.min;
        maxPathLength = depth.max;
    }

    public Sequence merge(Sequence sequence){
        Util.mergeSequences(this, sequence);
        return this;
    }
}
