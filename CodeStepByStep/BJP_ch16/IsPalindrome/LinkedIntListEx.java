package BJP_ch16.IsPalindrome;

import BJP_ch16.LinkedIntList;

public class LinkedIntListEx extends LinkedIntList {
    public LinkedIntListEx(int... elements) {
        super("front", elements);
    }

    // YOUR CODE GOES HERE
    public boolean isPalindrome() {
        if (front == null) {
            return true;
        }

        // use {end} as a chasing pointer to find the middle of the list.
        // along the way reverse the first half
        ListNode mid = null;
        ListNode crt = front;
        ListNode end = front;
        front = null;
        while(end != null && end.next != null) {
            end = end.next.next;
            mid = crt;
            crt = crt.next;
            mid.next = front;
            front = mid;
        }
        // here {front} and {mid} are at the end of the first half (shorter one 
        // for odd-sized list) and all its links are reversed.
        // {crt} is at the beginning of the second half (longer one for odd-sized list)

        // reconfigure the list such that {front} gets to the beginning of the second half
        // and {end} trails {mid}. Advance {crt} one position to have the {mid} and {crt}
        // point to symmetrical elements as the walk towards their respective ends.
        front = crt;
        // if end != null => odd-sized list. Otherwise even-sized list.
        if (end != null) {
            crt = crt.next;
        }
        end = mid;

        // walk the first half (reversed) and the second half checking the node values
        // along the way. Use the {mid} and {front} to re-reverse the first half
        // again thus bringing the list to its original sequence.
        boolean palindrome = true;
        while(crt != null) {
            palindrome = palindrome && (mid.data == crt.data);
            crt = crt.next;
            mid = mid.next;
            end.next = front;
            front = end;
            end = mid;
        }

        return palindrome;
    }
}
