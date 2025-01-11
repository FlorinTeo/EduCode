Write a method **reverse** that reverses all the links in a list: the new front of the list points to the former last node, which is linked all the way back to the former front node, which has its next field set to **null**.

For example, if a variable called _list_ contains the following sequence:

`[3, 8, 10, 11, 5, 12]`

And we make the following call:

`list.reverse();`

After the line gets executed the _list_ stores the following values:

`[12, 5, 11, 10, 8, 3]`

If the list does not contain any nodes, reversing it results in the same empty list.

Assume that you are adding this method to the LinkedIntList class as defined below:

```
public class LinkedIntList {
    private ListNode front;   // null for an empty list
    ...
    
    // your code goes here
}
```

You are not allowed to create any new ListNode objects in your code or modify the data values in the existent ones.