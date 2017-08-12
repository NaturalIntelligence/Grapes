package os.nushi.jfree;

import org.junit.Assert;
import org.junit.Test;
import os.nushi.jfree.matcher.CoreMatcher;
import os.nushi.jfree.matcher.Matcher;

import static os.nushi.jfree.Result.FAILED;
import static os.nushi.jfree.Result.PASSED;

public class BackReferenceTest {

    /**
     * It should not allow back reference for the group which is not captured yet.
     * Eg: "\\1(a)" is invalid.
     */
    @Test(expected = InvalidExpression.class)
    public void testForInvalidBackReferencePosition(){
        Sequence seq = new Pattern("\\1(a)").compile();
    }

    /**
     * It should not allow invalid back reference number.
     * Eg: "(a)\\2" is invalid.
     */
    @Test(expected = InvalidExpression.class)
    public void testForInvalidBackReferenceNumber(){
        Sequence seq = new Pattern("(a)\\2").compile();
    }

    /**
     * It should automatically judge correct group on the basis of total captured groups.
     * Eg: "(a)(b)(c)(d)(e)(f)(g)(h)(i)\\10(j)(k)\\10"
     */
    @Test
    public void testForFetchingValueFromCorrectGroup(){
        Sequence seq = new Pattern("(a)(b)(c)(d)(e)(f)(g)(h)(i)\\10(j)(k)\\10").compile();

        System.out.println(Util.toJson(seq));
        Matcher matcher = new CoreMatcher(seq);

        Assert.assertEquals(PASSED,matcher.match("abcdefghia0jkj".toCharArray()));
        Assert.assertEquals(PASSED,matcher.match("abcdefghia0jkj".toCharArray()));//to test if all the counters are reset
    }
}
