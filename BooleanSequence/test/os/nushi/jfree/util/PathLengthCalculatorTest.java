package os.nushi.jfree.util;

import org.junit.Assert;
import org.junit.Test;

public class PathLengthCalculatorTest {

    @Test
    public void tesTheMinmumLengthOfNormalRE(){

        Assert.assertEquals(4,new PathLengthCalculator().length("abcd".toCharArray()).x);
        //?
        Assert.assertEquals(3,new PathLengthCalculator().length("abc?d".toCharArray()).x);
        Assert.assertEquals(2,new PathLengthCalculator().length("ab?c?d".toCharArray()).x);
        Assert.assertEquals(0,new PathLengthCalculator().length("a?".toCharArray()).x);

        //+
        Assert.assertEquals(4,new PathLengthCalculator().length("ab+cd".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("ab?c+d".toCharArray()).x);
        Assert.assertEquals(2,new PathLengthCalculator().length("ab?c+d*".toCharArray()).x);

        //(),*
        Assert.assertEquals(4,new PathLengthCalculator().length("a(bcd)".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("a(b?cd)".toCharArray()).x);
        Assert.assertEquals(1,new PathLengthCalculator().length("a(bcd)?".toCharArray()).x);
        Assert.assertEquals(1,new PathLengthCalculator().length("a(bcd)*".toCharArray()).x);
        Assert.assertEquals(1,new PathLengthCalculator().length("a(bc*d+)?".toCharArray()).x);
        Assert.assertEquals(2,new PathLengthCalculator().length("ab+(cd)?".toCharArray()).x);

        //\\n
        Assert.assertEquals(7,new PathLengthCalculator().length("a(bcd)\\1".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a(b?cd)\\1".toCharArray()).x);
        Assert.assertEquals(1,new PathLengthCalculator().length("a(bcd)?\\1".toCharArray()).x);
        Assert.assertEquals(1,new PathLengthCalculator().length("a(bcd)*\\1".toCharArray()).x);
        Assert.assertEquals(1,new PathLengthCalculator().length("a(bc*d+)?\\1".toCharArray()).x);
        Assert.assertEquals(2,new PathLengthCalculator().length("ab+(cd)?\\1".toCharArray()).x);


        //{}
        Assert.assertEquals(4,new PathLengthCalculator().length("a(bc{1,3}d)".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a{3,}(b?cd)".toCharArray()).x);
        Assert.assertEquals(2,new PathLengthCalculator().length("a{2,4}(bcd)?".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a{5}(bcd)*".toCharArray()).x);


        Assert.assertEquals(4,new PathLengthCalculator().length("a(bcd){1}".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a(b?cd){2}".toCharArray()).x);
        Assert.assertEquals(7,new PathLengthCalculator().length("a(bcd){2,3}".toCharArray()).x);
        Assert.assertEquals(7,new PathLengthCalculator().length("a(bcd){2,}".toCharArray()).x);

        //[]
        Assert.assertEquals(5,new PathLengthCalculator().length("a[bcd]s [a-z]".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("a[\\]\\[-]{2}".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("a[.\\.a-s-]{2,3}".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("[.\\.a-s-]er".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a([.\\.a-s-]1){2,3}".toCharArray()).x);


        //?!,?:,?=, ?<=, ?<!
        Assert.assertEquals(6,new PathLengthCalculator().length("a(?=bc{1,3}d)ef".toCharArray()).x);
        Assert.assertEquals(7,new PathLengthCalculator().length("a{3,}(?<=b?cd)ef".toCharArray()).x);
        Assert.assertEquals(7,new PathLengthCalculator().length("a{2,4}(?<!bcd)3r".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a{5}(?:bcd)*".toCharArray()).x);
        Assert.assertEquals(5,new PathLengthCalculator().length("a{5}(\\?!bcd)*".toCharArray()).x);


        Assert.assertEquals(2,new PathLengthCalculator().length("ab|cde".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("a(b|cd)e".toCharArray()).x);
        Assert.assertEquals(3,new PathLengthCalculator().length("(bgh|cd?)ef".toCharArray()).x);

        Assert.assertEquals(5,new PathLengthCalculator().length("a(b(c))\\1".toCharArray()).x);

    }

    @Test
    public void tesTheMaximumLengthOfNormalRE(){

        Assert.assertEquals(4,new PathLengthCalculator().length("abcd".toCharArray()).y);
        //?
        Assert.assertEquals(4,new PathLengthCalculator().length("abc?d".toCharArray()).y);
        Assert.assertEquals(4,new PathLengthCalculator().length("ab?c?d".toCharArray()).y);
        Assert.assertEquals(1,new PathLengthCalculator().length("a?".toCharArray()).y);

        //+
        Assert.assertEquals(-1,new PathLengthCalculator().length("ab+cd".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("ab?c+d".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("ab?c+d*".toCharArray()).y);

        //(),*
        Assert.assertEquals(4,new PathLengthCalculator().length("a(bcd)".toCharArray()).y);
        Assert.assertEquals(4,new PathLengthCalculator().length("a(b?cd)".toCharArray()).y);
        Assert.assertEquals(4,new PathLengthCalculator().length("a(bcd)?".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a(bcd)*".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a(bc*d+)?".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("ab+(cd)?".toCharArray()).y);

        //\\n
        Assert.assertEquals(7,new PathLengthCalculator().length("a(bcd)\\1".toCharArray()).y);
        Assert.assertEquals(7,new PathLengthCalculator().length("a(b?cd)\\1".toCharArray()).y);
        Assert.assertEquals(7,new PathLengthCalculator().length("a(bcd)?\\1".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a(bcd)*\\1".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a(bc*d+)?\\1".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("ab+(cd)?\\1".toCharArray()).y);


        //{}
        Assert.assertEquals(6,new PathLengthCalculator().length("a(bc{1,3}d)".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a{3,}(b?cd)".toCharArray()).y);
        Assert.assertEquals(7,new PathLengthCalculator().length("a{2,4}(bcd)?".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a{5}(bcd)*".toCharArray()).y);


        Assert.assertEquals(4,new PathLengthCalculator().length("a(bcd){1}".toCharArray()).y);
        Assert.assertEquals(7,new PathLengthCalculator().length("a(b?cd){2}".toCharArray()).y);
        Assert.assertEquals(10,new PathLengthCalculator().length("a(bcd){2,3}".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a(bcd){2,}".toCharArray()).y);

        //[]
        Assert.assertEquals(5,new PathLengthCalculator().length("a[bcd]s [a-z]".toCharArray()).y);
        Assert.assertEquals(3,new PathLengthCalculator().length("a[\\]\\[-]{2}".toCharArray()).y);
        Assert.assertEquals(4,new PathLengthCalculator().length("a[.\\.a-s-]{2,3}".toCharArray()).y);
        Assert.assertEquals(3,new PathLengthCalculator().length("[.\\.a-s-]er".toCharArray()).y);
        Assert.assertEquals(7,new PathLengthCalculator().length("a([.\\.a-s-]1){2,3}".toCharArray()).y);


        //?!,?:,?=, ?<=, ?<!
        Assert.assertEquals(8,new PathLengthCalculator().length("a(?=bc{1,3}d)ef".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a{3,}(?<=b?cd)ef".toCharArray()).y);
        Assert.assertEquals(9,new PathLengthCalculator().length("a{2,4}(?<!bcd)3r".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a{5}(?:bcd)*".toCharArray()).y);
        Assert.assertEquals(-1,new PathLengthCalculator().length("a{5}(\\?!bcd)*".toCharArray()).y);

        Assert.assertEquals(3,new PathLengthCalculator().length("ab|cde".toCharArray()).y);
        Assert.assertEquals(4,new PathLengthCalculator().length("a(b|cd)e".toCharArray()).y);
        Assert.assertEquals(5,new PathLengthCalculator().length("(bgh|cd?)ef".toCharArray()).y);


        Assert.assertEquals(5,new PathLengthCalculator().length("a(b(c))\\1".toCharArray()).y);
    }
}
