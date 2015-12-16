package os.nushi.booleansequence.model.nodes;

import os.nushi.booleansequence.ds.primitive.CharArrList;
import os.nushi.booleansequence.model.Counter;

public class LazyNode extends Node {

	private CharArrList list;

	public LazyNode(char c) {
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
