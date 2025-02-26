/**
 * This package contains a library of classes for drawing and displaying images in a variety of educational projects.
 * The main abstractions in this library are the <i>Drawing</i>, <i>DrawingFrame</i> and <i>DrawingFactory</i> classes.
 * <ul>
 * <li> <i>Drawing</i> class is used to create and manipulate drawings.</li>
 * <li> <i>DrawingFrame</i> class is used to display the drawings on the screen.</li>
 * <li> <i>DrawingFactory</i> class is grouping together an image and its frame to simplify operations in specific projects.</li>
 * </ul>
 * Additionally, <i>KeyInterceptor</i> and <i>MouseInterceptor</i> classes are used to simplify the user interaction with
 * the interface by means of key strokes or mouse clicks.
 * <ul>
 * <li> <i>KeyInterceptor</i> is used to register implementations of <i>KeyInterceptor.KeyHook</i> to be executed when a specific key is pressed.</li>
 * <li> <i>MouseInterceptor</i> is used to register implementations of <i>MouseInterceptor.MouseHook</i> to be executed when a specific mouse action is performed.</li>
 * </ul>
 * Lastly, <i>DbgControls</i> and <i>FrameControls</i> interfaces are used to consolidate the common functionality any subclasses handling specific scenarios.
 */
package edu.ftdev;
