package edu.ftdev;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic MouseInterceptor class, to be used for customized mouse interaction.
 */
public class MouseInterceptor implements MouseListener, MouseMotionListener, MouseWheelListener {
    
    // To customize mouse hooks, consuming classes need to define their own
    // functional interface and pass it to one of the setMouse**Hook() method
    // I.e:
    // MouseIterceptor.mouseHook onMClicked = (MouseEvent mouseEvent) -> {..}
    // myMouseInterceptor.setMouseHook(onMClicked);

    /**
     * Functional Interface for a generic mouse hooking method.
     * Users can instantiate a lambda method that can be registered with the
     * drawing engine such that custom code gets called when specific mouse events are detected.
     * @see #mouseHook(MouseEvent, Object[])
     */
    public interface MouseHook {
        /**
         * Method called when a registered mouse event is intercepted.
         * @param mouseEvent the mouse event that was detected.
         * @param args optional arguments to be passed to the hook when event occurs.
         * @see MouseEvent
         */
        public void mouseHook(MouseEvent mouseEvent, Object[] args);
    }

    /**
     * Creates a new instance of the MouseInterceptor class.
     */
    public MouseInterceptor() {
        super();
    }

    // #region: [Private] classes and interfaces
    /**
     * Private class to hold the mouse hook and the arguments to be passed
     * to the hook when the mouse event occurs. 
     */
    private class MouseHookContext {
        private MouseHook _mouseHook;
        private Object[] _args;

        private MouseHookContext(MouseHook mouseHook, Object... args) {
            _mouseHook = mouseHook;
            _args = args;
        }
    }

    /**
     * Class wrapping metadata needed to execute custom mouse hooks asynchronously
     */
    private class CustomMouseHook {

        private Object _mouseEventSource;
        private MouseHookContext _mouseHookContext;
        private MouseEvent _mouseEvent;
        private Thread _runner;

        public CustomMouseHook(Object mouseEventSource, MouseHookContext mouseHookContext) {
            _mouseEventSource = mouseEventSource;
            _mouseHookContext = mouseHookContext;
            _mouseEvent = null;
            _runner = null;
        }

        public boolean start(MouseEvent mouseEvent) {
            if (_runner != null) {
                return false;
            }
            _mouseEvent = mouseEvent;
            _runner = new Thread(() -> {
                try {
                    _mouseHookContext._mouseHook.mouseHook(_mouseEvent, _mouseHookContext._args);
                } catch(Exception exc) {
                    System.out.println(exc.getStackTrace());
                }
                _mouseEvent = null;
                _runner = null;
                synchronized(this) {
                    this.notifyAll();
                }
            });
            _runner.start();
            return true;
        }

        public boolean stop() {
            if (_runner != null) {
                _runner.interrupt();
                return true;
            }
            return false;
        }
    }
    // #endregion: [Private] classes and interfaces

    // #region: [Private] Data fields
    private Map<Integer, MouseHookContext> _sysMouseHooks = new HashMap<Integer, MouseHookContext>();
    private Map<Integer, CustomMouseHook> _customMouseHooks = new HashMap<Integer, CustomMouseHook>();
    // #endregion: [Private] Data fields
    
    // #region: [Private] Mouse hooking private helpers
    private void forwardMouseEvent(MouseEvent e) {
        int hookKey = e.getID();
        
        // handle system hooks
        if (_sysMouseHooks.containsKey(hookKey)) {
            MouseHookContext targetSysHook = _sysMouseHooks.get(hookKey);
            if (targetSysHook != null) {
                try {
                    targetSysHook._mouseHook.mouseHook(e, targetSysHook._args);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }

        // handle custom hooks
        CustomMouseHook customHook = _customMouseHooks.get(hookKey);
        if (customHook != null && customHook._mouseEventSource == e.getSource()) {
            customHook.start(e);
        }
    }
    // #endregion: [Private] Mouse hooking private helpers

    // #region: [Internal] Mouse hooking methods
    MouseHook setSysMouseHook(int mouseEventId, MouseHook mouseHook, Object... args) {
        MouseHookContext prevMouseHookContext;
        if (mouseHook != null) {
            // wire the given event to the new, non-null hook
            MouseHookContext newSysHook = new MouseHookContext(mouseHook, args);
            prevMouseHookContext = _sysMouseHooks.put(mouseEventId, newSysHook);
        } else {
            // remove the previous hook (if any) for the given event
            prevMouseHookContext = _sysMouseHooks.remove(mouseEventId);
        }
        return (prevMouseHookContext != null) ? prevMouseHookContext._mouseHook : null;
    }

    MouseHook setCustomMouseHook(int mouseEventId, Object mouseEventSource, MouseHook mouseHook, Object... args) {
        CustomMouseHook crtCustomHook = _customMouseHooks.get(mouseEventId);
        if (crtCustomHook != null) {
            crtCustomHook.stop();
            _customMouseHooks.remove(mouseEventId);
        }

        if (mouseHook != null) {
            MouseHookContext newMouseHookContext = new MouseHookContext(mouseHook, args);
            CustomMouseHook newCustomHook = new CustomMouseHook(mouseEventSource, newMouseHookContext);
            _customMouseHooks.put(mouseEventId, newCustomHook);
        }

        return (crtCustomHook != null) ? crtCustomHook._mouseHookContext._mouseHook : null;
    }

    boolean hasCustomHooks() {
        return _customMouseHooks.size() > 0;
    }

    void close() {
        for(CustomMouseHook customHook : _customMouseHooks.values()) {
            customHook.stop();
        }
    }
    // #endregion: [Internal] Mouse hooking methods

    // #region: [Public] MouseListener overrides
    /**
     * Forwards the mouse click event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        forwardMouseEvent(e);
    }

    /**
     * Forwards the mouse press event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        forwardMouseEvent(e);
    }

    /**
     * Forwards the mouse release event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        forwardMouseEvent(e);
    }

    /**
     * Forwards the mouse enter event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        forwardMouseEvent(e);
    }

    /**
     * Forwards the mouse exit event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        forwardMouseEvent(e);
    }
    // #endregion: [Public] MouseListener overrides

    // #region: [Public] MouseMotionListener overrides
    /**
     * Forwards the mouse drag event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        forwardMouseEvent(e);
    }

    /**
     * Forwards the mouse move event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        forwardMouseEvent(e);
    }
    // #endregion: [Public] MouseMotionListener overrides

    // #region: [Public] MouseWheelListener overrides
    /**
     * Forwards the mouse wheel event to the registered hooks.
     * @param e the mouse event to be forwarded.
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        forwardMouseEvent(e);
    }
    // #endregion: [Public] MouseWheelListener overrides
}