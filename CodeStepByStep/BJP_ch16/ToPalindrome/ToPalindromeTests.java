package BJP_ch16.ToPalindrome;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class ToPalindromeTests {
    @Test
    public void testExample() {
        LinkedIntListEx list = new LinkedIntListEx(3, 8, 10, 11);
        list.toPalindrome();
        assertEquals("[3, 8, 10, 11, 10, 8, 3]", list.toStringShort());
    }

    @Test
    public void testEmpty() {
        LinkedIntListEx list = new LinkedIntListEx();
        list.toPalindrome();
        assertEquals(0, list.size());
    }

    @Test
    public void testOne() {
        LinkedIntListEx list = new LinkedIntListEx(1);
        list.toPalindrome();
        assertEquals("[1]", list.toStringShort());
    }

    @Test
    public void testTwo() {
        LinkedIntListEx list = new LinkedIntListEx(1, 2);
        list.toPalindrome();
        assertEquals("[1, 2, 1]", list.toStringShort());
    }

    @Test
    public void testLarge() {
        LinkedIntListEx list = new LinkedIntListEx();
        int n = 100;
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        list.toPalindrome();
        assertEquals(2 * n - 1, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(i < n ? i : list.size() - i - 1, list.get(i));
        }
    }
}