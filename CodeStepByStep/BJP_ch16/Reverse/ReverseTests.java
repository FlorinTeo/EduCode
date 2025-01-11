package BJP_ch16.Reverse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class ReverseTests {
    @Test
    public void testExample() {
        LinkedIntListEx list = new LinkedIntListEx(3, 8, 10, 11, 5, 12);
        list.reverse();
        assertEquals("[12, 5, 11, 10, 8, 3]", list.toStringShort());
    }

    @Test
    public void testEmpty() {
        LinkedIntListEx list = new LinkedIntListEx();
        list.reverse();
        assertEquals(0, list.size());
    }

    @Test
    public void testOne() {
        LinkedIntListEx list = new LinkedIntListEx(1);
        list.reverse();
        assertEquals("[1]", list.toStringShort());
    }

    @Test
    public void testLongList() {
        LinkedIntListEx list = new LinkedIntListEx();
        int n = 100;
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        list.reverse();
        for (int i = 0; i < n; i++) {
            assertEquals(n - i - 1, list.get(i));
        }
    }
}
