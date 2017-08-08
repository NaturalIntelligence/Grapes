package os.nushi.jfree.util;

public class CharUtil {

	public static boolean isRange(char c, int start, int end)	{
		return start <= c && c <= end; 
	}
	
	public static boolean isAlnum(char c)	{
		return isAlphabet(c) || isDigit(c);
	}
	
	public static boolean isDigit(char c)	{
		return isRange(c,'0' , '9');
	}
	
	public static boolean isAlphabet(char c)	{
		return isLowerCase(c) || isUpperCase(c);
	}
	
	public static boolean isUpperCase(char c)	{
		return isRange(c, 'A', 'Z');
	}
	
	public static boolean isLowerCase(char c)	{
		return isRange(c, 'a', 'z');
	}
	
}
