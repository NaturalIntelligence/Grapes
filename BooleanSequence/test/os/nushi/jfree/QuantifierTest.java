package os.nushi.jfree;

import org.junit.Assert;
import org.junit.Test;
import os.nushi.jfree.matcher.CoreMatcher;

public class QuantifierTest {

    /**
     * Eg a*
     */
    @Test
    public void testForZeroOrMoreQuantifier(){
        Sequence seq = new Pattern("ab*c").compile();

        System.out.println(Util.toJson(seq));

        CoreMatcher matcher = new CoreMatcher(seq);

        Assert.assertEquals(Result.PASSED, matcher.match("ac".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abc".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abbc".toCharArray()));
    }

    /**
     * Eg a*a
     */
    @Test
    public void testForZeroOrMoreQuantifierWhenNextNodeIsSame(){
        Sequence seq = new Pattern("ab*bc").compile();

        System.out.println(Util.toJson(seq));

        CoreMatcher matcher = new CoreMatcher(seq);

        Assert.assertEquals(Result.FAILED, matcher.match("ac".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abc".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abbbc".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abbc".toCharArray()));
    }



    /**
     * Eg a*
     */
    @Test
    public void testForOneOrMoreQuantifier(){
        Sequence seq = new Pattern("ab+c").compile();

        System.out.println(Util.toJson(seq));

        CoreMatcher matcher = new CoreMatcher(seq);

        Assert.assertEquals(Result.FAILED, matcher.match("ac".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abc".toCharArray()));
        Assert.assertEquals(Result.PASSED, matcher.match("abbc".toCharArray()));
    }
}
