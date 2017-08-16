package os.nushi.jfree.ds.primitive;

import org.junit.Assert;
import org.junit.Test;

public class CharArrlistTest {

    @Test
    public void testToAdd(){
        CharArrList list = new CharArrList();
        list.addAll(new CharArrList(new char[]{'H','e','l','l'}));
        list.addAll(new CharArrList("o!! "));
        list.add('a');
        list.addAll("mit");
        list.add(' ');
        list.addAll(new char[]{'g','u','p','t'});
        list.add('a');

        Assert.assertEquals("Hello!! amit gupta", list.toString());

    }
}
