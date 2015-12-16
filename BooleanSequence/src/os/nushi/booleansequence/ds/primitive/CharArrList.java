package os.nushi.booleansequence.ds.primitive;

/**
 * Maintains insertion order.
 * @author Amit Gupta
 *
 */
public class CharArrList
{
	private char[] data;
	private int lastElementPosition = -1;
	private int sizeToIncrement = 5;
	
	public CharArrList(){
		data = new char[sizeToIncrement];
	}
	
	public CharArrList( int initialSize){
		data = new char[initialSize];
	}
	
	public CharArrList(char... c){
		data = new char[c.length];
		if(c.length == 1){this.data = c; this.lastElementPosition = 0;}
		else{
			for(int i=0;i<=c.length;i++){
				this.add(c[i]);
			}
		}
	}
	
	public void setSizeToIncrement(int sizeToIncrement){
		this.sizeToIncrement = sizeToIncrement;
	}
	public char get(int position){
		return data[position];
	}
	
	public void add(char value){
		ensureCapacity(++lastElementPosition);
		data[lastElementPosition] = value;
	}

	public char[] getAllValues(){
		return data;
	}
	public int size(){
		return lastElementPosition + 1;
	}
	
	public char lastItemInserted(){
		if(lastElementPosition == -1) return '\u0000';
		return data[lastElementPosition];
	}
	
	public void removeLastItem() {
		//keys[lastElementPosition] = '\u0000';
		lastElementPosition -=1;
		
	}
	private void ensureCapacity(int position) {
		if (position >= data.length){
			char[] newData = new char[data.length + sizeToIncrement];
			if(data.length > 20){
				System.arraycopy(data, 0, newData, 0, data.length);
			}else{
				for(int i = 0; i < data.length; i++){
					newData[i] = data[i];
			    }
			}
			data = newData;
		}
	}
	
	/**
	 * From given index and of specified length 
	 * @param from
	 * @param length
	 * @return
	 */
	public CharArrList subList(int from, int length){
		if(length == 1){
			CharArrList singleCharList = new CharArrList(1);
			singleCharList.data[0] = this.data[0];
			singleCharList.lastElementPosition = 0;
			return singleCharList;
		}
		CharArrList newList = new CharArrList(length);
		for(int i=from;i<from+length;i++){
			newList.add(data[i]);
		}
		return newList;
	}
	
	/**
	 * From 0 and of specified length 
	 * @param length
	 * @return
	 */
	public CharArrList subList(int length){
		return subList(0,length);
	}
	
	/**
	 * From given index till end
	 * @param length
	 * @return
	 */
	public CharArrList subListFrom(int from){
		return subList(from,this.size() - from);
	}
	
	@Override
	public String toString() {
		return new String(data,0,lastElementPosition+1);
	}

	public void removeAll() {
		this.lastElementPosition = -1;
		data = new char[sizeToIncrement];
	}

	
}
