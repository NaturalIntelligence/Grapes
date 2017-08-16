package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.MatchingCharSequence;

public class SequenceEndNode extends Node{

    private int seqNumber;
    private MatchingCharSequence matchingSeqence;

    public SequenceEndNode(){
        super('\u0001');
    }

    public MatchingCharSequence getMatchingSequence() {
        return matchingSeqence;
    }

    @Override
    public Result match(char[] ch, Counter index) {
        return null;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public void setMatchingSeqence(MatchingCharSequence matchingSeqence) {
        this.matchingSeqence = matchingSeqence;
    }

}
