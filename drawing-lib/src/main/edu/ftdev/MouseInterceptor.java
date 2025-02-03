package edu.ftdev;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

/**
 * Generic MouseInterceptor class, to be used for customized mouse interaction.
 */
public class MouseInterceptor extends Thread implements MouseListener, MouseMotionListener, MouseWheelListener {
    
    // To customize mouse hooks, consuming classes need to define their own
    // functional interface and pass it to one of the setMouse**Hook() method
    // I.e:
    // MouseIterceptor.mouseHook onMClicked = (MouseEvent mouseEvent) -> {..}
    // myMouseInterceptor.setMouseHook(onMClicked);
    
    /**
     * Functional Interface for a generic mouse hooking method.
     * Users can instantiate a lambda method that can be registered with the
     * drawing engine such that custom code gets called when specific mouse events are detected.
     * @see #mouseHook(MouseEvent)
     */
    public interface MouseHook {
        /**
         * Method called when a registered mouse event is intercepted.
         * @param mouseEvent - the mouse event that was detected.
         * @see MouseEvent
         */
        public void mouseHook(MouseEvent mouseEvent) throws InterruptedException;
    }

    /**
     * Class wrapping metadata needed to execute custom mouse hooks asynchronously
     */
    private class CustomMouseHook {

        private Object _mouseEventSource;
        private MouseHook _mouseHook;
        private MouseEvent _mouseEvent;
        private Thread _runner;

        public CustomMouseHook(Object mouseEventSource, MouseHook mouseHook) {
            _mouseEventSource = mouseEventSource;
            _mouseHook = mouseHook;
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
                    _mouseHook.mouseHook(_mouseEvent);
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

    // #region: [Private] Data fields
    private HashMap<Integer, MouseHook> _sysMouseHooks = new HashMap<Integer, MouseHook>();
    private HashMap<Integer, CustomMouseHook> _customMouseHooks = new HashMap<Integer, CustomMouseHook>();
    // #endregion: [Private] Data fields
    
    // #region: [Private] Mouse hooking private helpers
    private void forwardMouseEvent(MouseEvent e) {
        int hookKey = e.getID();
        
        // handle system hooks
        if (_sysMouseHooks.containsKey(hookKey)) {
            MouseHook targetSysHook = _sysMouseHooks.get(hookKey);
            if (targetSysHook != null) {
                try {
                    targetSysHook.mouseHook(e);
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
    MouseHook getHookHook(int mouseEventId) {
        return null;
    }
    
    MouseHook setSysMouseHook(int mouseEventId, MouseHook mouseHook) {
        return (mouseHook == null)
            ? _sysMouseHooks.remove(mouseEventId)
            : _sysMouseHooks.put(mouseEventId, mouseHook);
    }

    MouseHook setCustomMouseHook(int mouseEventId, Object mouseEventSource, MouseHook mouseHook) {
        CustomMouseHook crtCustomHook = _customMouseHooks.get(mouseEventId);
        if (crtCustomHook != null) {
            crtCustomHook.stop();
            _customMouseHooks.remove(mouseEventId);
        }

        if (mouseHook != null) {
            CustomMouseHook newCustomHook = new CustomMouseHook(mouseEventSource, mouseHook);
            _customMouseHooks.put(mouseEventId, newCustomHook);
        }

        return (crtCustomHook != null) ? crtCustomHook._mouseHook : null;
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
    @Override
    public void mouseClicked(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        forwardMouseEvent(e);
    }
    // #endregion: [Public] MouseListener overrides

    // #region: [Public] MouseMotionListener overrides
    @Override
    public void mouseDragged(MouseEvent e) {
        forwardMouseEvent(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        forwardMouseEvent(e);
    }
    // #endregion: [Public] MouseMotionListener overrides

    // #region: [Public] MouseWheelListener overrides
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        forwardMouseEvent(e);
    }
    // #endregion: [Public] MouseWheelListener overrides
}