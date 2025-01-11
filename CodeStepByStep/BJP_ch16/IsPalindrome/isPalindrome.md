Write a method **isPalindrome** that determines if a list is a palindrome: its sequence of numbers from the front to the end is the same as the sequence read backwards, from the end to the front.

For example, if a variable _list_ is set to any of the following sequences:

```
[]
[3]
[3, 8, 3]
[3, 8, 8, 3]
```

The line of code:

`bool palindrome = list.isPalindrome();`

Will set the variable _palindrome_ to **true**.<br>If the variable _list_ is set to any of the following sequences:
```
[1, 2]
[1, 2, 3]
[1, 2, 3, 2]
[1, 2, 3, 2, 1, 0]
```

The same line of code will set the variable _palindrome_ to **false**.

Assume that you are adding this method to the LinkedIntList class as defined below:

```
public class LinkedIntList {
    private ListNode front;   // null for an empty list
    ...
    
    // your code goes here
}
```

The best solution runs in O(N) time, or linear time, and O(1) memory, or constant memory. In other words you don't need to allocate any new ListNode objects and the number of iterations in the loops in your code is directly proportional to the size of the list.