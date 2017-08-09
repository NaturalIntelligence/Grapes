package os.nushi.jfree;

import org.junit.Test;
import os.nushi.jfree.matcher.CoreMatcher;

import java.util.regex.Matcher;

public class PerformanceTest {

    @Test
    public void perfTest(){
        for(int i = 0 ; i < 100000; i++);

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[abc](ab|c?d)?ef$");
        Sequence seq = new Pattern("[abc](ab|c?d)?ef").compile();
        long startTime = 0L;
        System.gc();
        for(int i = 0 ; i < 100000; i++);
        Matcher matcher2 = pattern.matcher("");

        startTime = System.nanoTime();
        CoreMatcher matcher = new CoreMatcher(seq);
        for(int i = 0 ; i < 100000; i++){
            matcher.match("cabef".toCharArray());//long
            matcher.match("cef".toCharArray());//short
            matcher.match("ce".toCharArray());//invalid
            //matcher.match("cabefasde".toCharArray());//invalid long
        }
        System.out.println((System.nanoTime() - startTime)/100000);

        System.gc();
        for(int i = 0 ; i < 100000; i++);
        startTime = System.nanoTime();
        for(int i = 0 ; i < 100000; i++){
            matcher2.reset("cabef");//long
            matcher2.lookingAt();
            matcher2.reset("cef"); //short
            matcher2.lookingAt();
            matcher2.reset("ce"); //invalid
            matcher2.lookingAt();
        }
        System.out.println((System.nanoTime() - startTime)/100000);

        System.gc();

    }

    /*@Test
    public void tempTest(){
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("a(b)\\{10}");
        Matcher matcher = pattern.matcher("abb0");
        System.out.println(matcher.find());


        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\1(a)(b)(c)(d)(e)(f)(g)(h)\\10(i)(j)(k)(l)(m)(n)(o)(p)\\10");
        Matcher matcher = pattern.matcher("abcdefgha0iklmnopj");
        System.out.println(matcher.find());

    }*/
}
