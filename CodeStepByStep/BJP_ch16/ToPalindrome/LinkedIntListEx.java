package BJP_ch16.ToPalindrome;

import BJP_ch16.LinkedIntList;

public class LinkedIntListEx extends LinkedIntList {
    public LinkedIntListEx(int... elements) {
        super("front", elements);
    }

    // YOUR CODE GOES HERE
    public void toPalindrome() {
        if (front == null) {
            return;
        }
        ListNode revFront = null;
        ListNode crt = front;
        while(crt.next != null) {
            revFront = new ListNode(crt.data, revFront);
            crt = crt.next;
        }
        crt.next = revFront;
    }
}
