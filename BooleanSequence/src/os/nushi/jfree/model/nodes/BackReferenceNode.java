package os.nushi.jfree.model.nodes;

import os.nushi.jfree.ds.primitive.CharArrList;
import os.nushi.jfree.model.Counter;
import os.nushi.jfree.model.nodes.Node;

/**
 * \\n
 * <br/> n should be less than or equal to total number of captures group yet.
 * <br/> Eg: invalid RE : "a(b)\\2(c)"; valid RE: "a(b)(c)\\2"
 * <br/> if n is 2 digit but total number of captured groups are 1 digit then 2nd digit will be treated as normal char.
 * <br/> Eg: RE "(a)(b)(c)(d)(e)(f)(g)(h)(i)\\10(j)(k)(l)(m)(n)(o)(p)\\10" will match "abcdefghi<b>a0</b>jklmnop<b>j</b>"
 * @author Amit Gupta
 */
public class BackReferenceNode extends Node {

	private CharArrList list;

	public BackReferenceNode(char c) {
		super(c);
	}

	public void source(CharArrList list){
		this.list = list;
	}
	
	@Override
	public boolean match(char[] ch, Counter index) {
		try{
			if(list.size() == 1) return list.get(0) == ch[index.counter];
			for(int i=0;i<list.size();i++){
				if(list.get(i) != ch[index.counter++]) return false;
			}
			index.counter--;
			return true;				
		}catch(Exception e){}
		
		return false;
	}

}
