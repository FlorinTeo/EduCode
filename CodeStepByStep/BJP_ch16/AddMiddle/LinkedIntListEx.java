package BJP_ch16.AddMiddle;

import BJP_ch16.LinkedIntList;

public class LinkedIntListEx extends LinkedIntList {
    public LinkedIntListEx(int... elements) {
        super("front", elements);
    }

    // YOUR CODE GOES HERE
    public boolean addMiddle(int n) {
        ListNode prv = null;
        ListNode crt = front;
        ListNode tail = front;
        while(tail != null) {
            tail = tail.next;
            if (tail == null) {
                return false;
            }
            prv = crt;
            crt = crt.next;
            tail = tail.next;
        }
        if (prv == null) {
            front = new ListNode(n);
        } else {
            prv.next = new ListNode(n, prv.next);
        }
        return true;
    }
}
