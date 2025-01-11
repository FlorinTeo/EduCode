package BJP_ch16.AddMiddle;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class AddMiddleTests {
    @Test
    public void testExampleTrue() {
        LinkedIntListEx list = new LinkedIntListEx(3, 8, 10, 11, 5, 12);
        boolean success = list.addMiddle(20);
        assertTrue(success);
        assertEquals("[3, 8, 10, 20, 11, 5, 12]", list.toStringShort());
    }
    
    @Test
    public void testExampleFalse() {
        LinkedIntListEx list = new LinkedIntListEx(3, 8, 10, 11, 5);
        boolean success = list.addMiddle(20);
        assertFalse(success);
        assertEquals("[3, 8, 10, 11, 5]", list.toStringShort());
    }

    @Test
    public void testEmpty() {
        LinkedIntListEx list = new LinkedIntListEx();
        boolean success = list.addMiddle(20);
        assertTrue(success);
        assertEquals("[20]", list.toStringShort());
    }

    @Test
    public void testOne() {
        LinkedIntListEx list = new LinkedIntListEx(-12);
        boolean success = list.addMiddle(20);
        assertFalse(success);
        assertEquals("[-12]", list.toStringShort());
    }

    @Test
    public void testTwo() {
        LinkedIntListEx list = new LinkedIntListEx(-12, 12);
        boolean success = list.addMiddle(20);
        assertTrue(success);
        assertEquals("[-12, 20, 12]", list.toStringShort());
    }

    @Test
    public void testLargeEven() {
        LinkedIntListEx list = new LinkedIntListEx();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        boolean success = list.addMiddle(20);
        assertTrue(success);
        assertEquals(1001, list.size());
        for (int i = 0; i < 500; i++) {
            assertEquals(i, list.get(i));
        }
        assertEquals(20, list.get(500));
        for (int i = 501; i < 1001; i++) {
            assertEquals(i-1, list.get(i));
        }
    }

    @Test
    public void testLargeOdd() {
        LinkedIntListEx list = new LinkedIntListEx();
        for (int i = 0; i < 1001; i++) {
            list.add(i);
        }
        boolean success = list.addMiddle(20);
        assertFalse(success);
        assertEquals(1001, list.size());
        for (int i = 0; i < 1001; i++) {
            assertEquals(i, list.get(i));
        }
    }
}