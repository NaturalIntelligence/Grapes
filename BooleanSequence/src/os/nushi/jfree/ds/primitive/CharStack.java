/***
 * The MIT License (MIT)

Copyright (c) 2015 NaturalIntelligence

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

package os.nushi.jfree.ds.primitive;

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
