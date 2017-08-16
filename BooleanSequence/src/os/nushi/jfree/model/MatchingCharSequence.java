package os.nushi.jfree.model;

import os.nushi.jfree.ds.primitive.CharArrList;

public class MatchingCharSequence {
    private CharArrList seq;
    private MatchingCharSequence parent;

    public MatchingCharSequence(MatchingCharSequence parent){
        seq= new CharArrList();
        this.parent = parent;
    }

    public void append(char c){
        seq.add(c);
        if(parent != null) parent.append(c);
    }

    public CharArrList getMatchingSequence(){
        return seq;
    }

    public MatchingCharSequence getParent() {
        return parent;
    }

    public String toString(){
        return seq.toString();
    }

    public String value(){
        return seq.toString();
    }
}
