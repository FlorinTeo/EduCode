package BJP_ch16.Reverse;

import BJP_ch16.LinkedIntList;

public class LinkedIntListEx extends LinkedIntList {
    public LinkedIntListEx(int... elements) {
        super("front", elements);
    }

    // YOUR CODE GOES HERE
    public void reverse() {
        ListNode crt = front;
        front = null;
        while(crt != null) {
            ListNode temp = crt;
            crt = crt.next;
            temp.next = front;
            front = temp;
        }
    }
}
