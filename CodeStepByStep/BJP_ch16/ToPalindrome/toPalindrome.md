Write a method **toPalindrome** that appends to a list, in reverse order, all its nodes preceding the last one. This results in the list becoming a palindrome: its sequence of numbers from front to end becomes the same as the sequence read backwards, from end to front.

For example, if a variable called _list_ contains the following sequence:

`[3, 8, 10, 11]`

And we make the following call:

`list.toPalindrome();`

After the line gets executed _list_ stores the following values:

`[3, 8, 10, 11, 10, 8, 3]`

If the list is originally empty or contains only one node, executing the method does not change it.

Assume that you are adding this method to the LinkedIntList class as defined below:

```
public class LinkedIntList {
    private ListNode front;   // null for an empty list
    ...
    
    // your code goes here
}
```