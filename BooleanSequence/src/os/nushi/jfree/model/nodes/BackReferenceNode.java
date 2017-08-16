package os.nushi.jfree.model.nodes;

import os.nushi.jfree.Result;
import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.MatchingCharSequence;

import java.util.List;
import java.util.Map;

/**
 * \\n
 * <br/> n should be less than or equal to total number of captures group yet.
 * <br/> Eg: invalid RE : "a(b)\\2(c)"; valid RE: "a(b)(c)\\2"
 * <br/> if n is 2 digit but total number of captured groups are 1 digit then 2nd digit will be treated as normal char.
 * <br/> Eg: RE "(a)(b)(c)(d)(e)(f)(g)(h)(i)\\10(j)(k)(l)(m)(n)(o)(p)\\10" will match "abcdefghi<b>a0</b>jklmnop<b>j</b>"
 * @author Amit Gupta
 */
public class BackReferenceNode extends Node {

	private Map<Integer, MatchingCharSequence> groups;
	private final int seqCounter;
	int val;

	public BackReferenceNode(int v,int seqCounter) {
		super('0');
		this.val = v;
		this.seqCounter = seqCounter;
	}

	int cnt=0;
	CharArrList groupVal;

	@Override
	public Result match(char[] ch, Counter index) {
		if(cnt==0){
			if(seqCounter >= this.val) {
				groupVal = groups.get(this.val).getMatchingSequence();
				if(groupVal.get(cnt) == ch[index.counter]){
					cnt++;
					if(cnt == groupVal.size()) {
						reset();
						return Result.PASSED;
					}
					else
						return Result.MATCHED;
				}
			}
		}else{
			if(groupVal.get(cnt) == ch[index.counter]){
				cnt++;
				if(cnt == groupVal.size()) {
					reset();
					return Result.PASSED;
				}
				else
					return Result.MATCHED;
			}
		}
		reset();
		return Result.FAILED;
	}

	public void setGroups(Map<Integer, MatchingCharSequence> groups){
		this.groups = groups;
	}

	public void reset(){
		cnt=0;
		groupVal=null;
		this.groups = null;
	}
}
