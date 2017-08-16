package os.nushi.jfree;

import os.nushi.jfree.model.MatchingCharSequence;
import os.nushi.jfree.model.nodes.Node;
import os.nushi.jfree.model.nodes.NormalNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Sequence {

    public Node startNode;

    private MatchingCharSequence matchingCharSequence;
    public Node endNode;
    private Map<Integer, MatchingCharSequence> groups;

    public Sequence(MatchingCharSequence matchingCharSequence){
        this.matchingCharSequence = matchingCharSequence;
        this.startNode = new NormalNode();
    }



    public int minPathLength;
    public int maxPathLength;

    public Sequence merge(Sequence sequence){
        Util.mergeSequences(this, sequence);
        return this;
    }

    Set<Node> nodes = new HashSet<>();
    /**
     * All the matchers should call it before traversing through the sequence
     */
    public void reset(){
        for (Node node: nodes) {
            node.reset();
        }
    }

    public MatchingCharSequence getMatchingCharSequence() {
        return matchingCharSequence;
    }

    public void setMatchingCharSequence(MatchingCharSequence matchingCharSequence) {
        this.matchingCharSequence = matchingCharSequence;
    }

    public void setGroups(Map<Integer, MatchingCharSequence> groups) {
        this.groups = groups;
    }

    public Map<Integer, MatchingCharSequence> getGroups() {
        return groups;
    }
}
