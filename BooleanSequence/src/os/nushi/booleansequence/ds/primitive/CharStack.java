package os.nushi.booleansequence.ds.primitive;

public class CharStack {

	private CharArrList _list;
	private int sizeToIncrement = 5;
	
	public CharStack(){
		_list = new CharArrList(sizeToIncrement);
	}
	
	public CharStack( int initialSize){
		_list = new CharArrList(initialSize);
	}
	
	public void setSizeToIncrement(int sizeToIncrement){
		_list.setSizeToIncrement(sizeToIncrement);
	}
	
	public int size(){
		return _list.size();
	}
		
    public void push( char val ) {
        _list.add( val );
    }

    public char pop() {
    	char c = _list.lastItemInserted();
        _list.removeLastItem();
        return c;
    }

    public char peek() {
        return _list.lastItemInserted();
    }
    
    public void clear() {
        _list.removeAll();
    }
}
