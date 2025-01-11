package BJP_ch16.IsPalindrome;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

public class IsPalindromeTests {
    @Test
    public void testExample1() {
        LinkedIntListEx list = new LinkedIntListEx();
        boolean palindrome = list.isPalindrome();
        assertTrue(palindrome);
        assertEquals("[]", list.toStringShort());
    }

    @Test
    public void testExample2() {
        LinkedIntListEx list = new LinkedIntListEx(3);
        boolean palindrome = list.isPalindrome();
        assertTrue(palindrome);
        assertEquals("[3]", list.toStringShort());
    }

    @Test
    public void testExample3() {
        LinkedIntListEx list = new LinkedIntListEx(3, 8, 3);
        boolean palindrome = list.isPalindrome();
        assertTrue(palindrome);
        assertEquals("[3, 8, 3]", list.toStringShort());
    }

    @Test
    public void testExample4() {
        LinkedIntListEx list = new LinkedIntListEx(3, 8, 8, 3);
        boolean palindrome = list.isPalindrome();
        assertTrue(palindrome);
        assertEquals("[3, 8, 8, 3]", list.toStringShort());
    }

    @Test
    public void testExample5() {
        LinkedIntListEx list = new LinkedIntListEx(1, 2);
        boolean palindrome = list.isPalindrome();
        assertFalse(palindrome);
        assertEquals("[1, 2]", list.toStringShort());
    }

    @Test
    public void testExample6() {
        LinkedIntListEx list = new LinkedIntListEx(1, 2, 3);
        boolean palindrome = list.isPalindrome();
        assertFalse(palindrome);
        assertEquals("[1, 2, 3]", list.toStringShort());
    }

    @Test
    public void testExample7() {
        LinkedIntListEx list = new LinkedIntListEx(1, 2, 3, 2);
        boolean palindrome = list.isPalindrome();
        assertFalse(palindrome);
        assertEquals("[1, 2, 3, 2]", list.toStringShort());
    }

    @Test
    public void testExample8() {
        LinkedIntListEx list = new LinkedIntListEx(1, 2, 3, 3, 2);
        boolean palindrome = list.isPalindrome();
        assertFalse(palindrome);
        assertEquals("[1, 2, 3, 3, 2]", list.toStringShort());
    }

    @Test
    public void testLongPalindromeEven() {
        LinkedIntListEx list = new LinkedIntListEx();
        int n = 100;
        for (int i = 0; i < 2 * n; i++) {
            list.add(i < n ? i : 2 * n - i - 1);
        }
        boolean palindrome = list.isPalindrome();
        assertTrue(palindrome);
        for (int i = 0; i < 2 * n; i++) {
            assertEquals(i < n ? i : 2 * n - i - 1, list.get(i));
        }
    }
    @Test
    public void testLongPalindromeOdd() {
        LinkedIntListEx list = new LinkedIntListEx();
        int n = 100;
        for (int i = 0; i < 2 * n + 1; i++) {
            list.add(i <= n ? i : 2 * n - i);
        }
        boolean palindrome = list.isPalindrome();
        assertTrue(palindrome);
        for (int i = 0; i < 2 * n; i++) {
            assertEquals(i <= n ? i : 2 * n - i, list.get(i));
        }
    }

    @Test
    public void testLongNonPalindrome() {
        LinkedIntListEx list = new LinkedIntListEx();
        int n = 100;
        for (int i = 0; i < 2 * n; i++) {
            list.add(i <= n ? i : 2 * n - i);
        }
        boolean palindrome = list.isPalindrome();
        assertFalse(palindrome);
        for (int i = 0; i < 2 * n; i++) {
            assertEquals(i <= n ? i : 2 * n - i, list.get(i));
        }
    }
}