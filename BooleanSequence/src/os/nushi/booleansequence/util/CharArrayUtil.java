package os.nushi.booleansequence.util;

public class CharArrayUtil {
	
	
	/**
	 * Best for arrays less than 20 cells
	 * @param source
	 * @param from inclusive
	 * @param to inclusive
	 * @return
	 */
	public static char[] subArray(final char[] source, int from, int to){
		char[] target = new char[to-from+1];
		for(int i = 0; i < target.length; i++){
			target[i] = source[from+i]; 
	    }
		return target;
	}
	
	/**
	 * Best for arrays less than 20 cells.
	 * @param source
	 * @param length
	 * @return sub-array from 0 of desire length
	 */
	public static char[] subArray(final char[] source, int length){
		return subArray(source, 0, length - 1);
	}
	
	public static boolean equals(char[] left, char right){
		if(left.length == 1 ) return left[0] == right;
		return false;
	}
	
	public static boolean equals(char[] left, char... right){
		if(left.length != right.length ) return false;
		for (int i=0; i<left.length; i++)
            if (left[i] != right[i])
                return false;
		return true;
	}

	public static boolean hasAny(char[] charArray, char c) {
		for(char ch : charArray){
			if(ch == c) return true;
		}
		return false;
	}
	
	public static boolean hasAny(char[] left, char... right) {
		for(char ch : left)
			for(char c : right)
				if(ch == c) return true;

		return false;
	}
}
