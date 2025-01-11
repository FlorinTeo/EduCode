Write a method **addMiddle** that takes an integer _n_ as a parameter and inserts a node with that value in the middle of the original list. The method returns **true** if the insertion succeeds, and **false** otherwise. The insertion succeeds only if the original list has an even number of elements otherwise it fails and the list does not get modified.

For example, if a variable called _list_ contains the following sequence:

`[3, 8, 10, 11, 5, 12]`

And we make the following call:

`boolean success = list.addMiddle(20);`

After the line gets executed the variable _success_ is set to **true** and _list_ stores the following values:

`[3, 8, 10, 20, 11, 5, 12]`

However if the variable _list_ contains the following sequence:

`[3, 8, 10, 11, 5]`

The same line of code results in the variable _success_ set to **false** and _list_ remains unchanged.

Assume that you are adding this method to the LinkedIntList class as defined below:

```
public class LinkedIntList {
    private ListNode front;   // null for an empty list
    ...
    
    // your code goes here
}
```