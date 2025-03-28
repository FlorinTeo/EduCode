package edu.ftdev;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic KeyInterceptor class, to be used for customized key interaction.
 * By default, the interceptor is handling the keys '1', '3', ' ' and &lt;ESC&gt;
 * implementing UI level debugging on 2 levels, Fast-Fwd and Quit.
 */
public class KeyInterceptor implements KeyListener {
    
    // To customize key hooks, consuming classes need to define their own
    // functional interface and pass it to one of the setKey**Hook() method
    // along with the KeyEvent.VK_ or Character to trigger upon.
    // I.e:
    // KeyIterceptor.KeyHook onSTyped = (KeyEvent keyEvent) -> {..}
    // myKeyInterceptor.setKeyTypedHook('S', onSTyped)

    enum DbgLevel {
        STEP(1),
        LEAP(2),
        JUMP(3),
        RUN(Integer.MAX_VALUE);

        private final int _level;

        DbgLevel(int level) {
            _level = level;
        }

        int value() {
            return _level;
        }
    };

    /**
     * Functional Interface for a generic key hooking method.
     * Users can instantiate a lambda method that can be registered with the
     * drawing engine such that custom code gets called when specific key is pressed.
     * @see #keyHook(KeyEvent, Object[])
     */
    public interface KeyHook {
        /**
         * Method called when a registered key event is intercepted.
         * @param keyEvent the key event that as detected.
         * @param args optional arguments to be passed to the hook when event occurs.
         * @see KeyEvent
         */
        public void keyHook(KeyEvent keyEvent, Object[] args);
    }

    /**
     * Creates a new instance of the KeyInterceptor class.
     */
    public KeyInterceptor() {
        super();
    }

    // #region: [Private] classes and interfaces
     /**
     * private class to hold the key hook and the arguments to be passed
     * to the hook when the key event occurs. 
     */
    private class KeyHookContext {
        private KeyHook _keyHook;
        private Object[] _args;

        private KeyHookContext(KeyHook keyHook, Object... args) {
            _keyHook = keyHook;
            _args = args;
        }
    }

    /**
     * Class wrapping metadata needed to execute custom key hooks asynchronously
     */
    private class CustomKeyHook {

        private Object _keyEventSource;
        private KeyHookContext _keyHookContext;
        private KeyEvent _keyEvent;
        private Thread _runner;

        public CustomKeyHook(Object keyEventSource, KeyHookContext keyHookContext) {
            _keyEventSource = keyEventSource;
            _keyHookContext = keyHookContext;
            _keyEvent = null;
            _runner = null;
        }

        public boolean start(KeyEvent keyEvent) {
            if (_runner != null) {
                return false;
            }
            _keyEvent = keyEvent;
            _runner = new Thread(() -> {
                try {
                    _keyHookContext._keyHook.keyHook(_keyEvent, _keyHookContext._args);
                } catch(Exception exc) {
                    System.out.println(exc.getStackTrace());
                }
                _keyEvent = null;
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
    private Object _sync = new Object();
    private Object _end = new Object();
    private Integer _keyStepLevel = Integer.MIN_VALUE;
    private Map<Integer, KeyHookContext> _sysKeyHooks = new HashMap<Integer, KeyHookContext>();
    private Map<Integer, CustomKeyHook> _customKeyTypedHooks = new HashMap<Integer, CustomKeyHook>();
    private Map<Integer, CustomKeyHook> _customKeyPressedHooks = new HashMap<Integer, CustomKeyHook>();
    private Map<Integer, CustomKeyHook> _customKeyReleasedHooks = new HashMap<Integer, CustomKeyHook>();
    // #endregion: [Private] Data fields
    
    // #region: [Private] Key hooking private helpers
    private void forwardKeyEvent(KeyEvent e, Map<Integer, CustomKeyHook> customKeyHooks) {
        int hookKey = e.getKeyCode();
        if (hookKey == KeyEvent.VK_UNDEFINED) {
            hookKey = Character.toUpperCase(e.getKeyChar());
        }
        
        // handle system hooks
        if (_sysKeyHooks.containsKey(hookKey) && e.getID() == KeyEvent.KEY_TYPED) {
            KeyHookContext targetSysHook = _sysKeyHooks.get(hookKey);
            if (targetSysHook != null) {
                try {
                    targetSysHook._keyHook.keyHook(e, targetSysHook._args);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }

        // handle custom hooks
        if (customKeyHooks != null) {
            CustomKeyHook customHook = customKeyHooks.get(hookKey);
            if (customHook != null && (customHook._keyEventSource == null || customHook._keyEventSource == e.getSource())) {
                customHook.start(e);
            }
        }
    }
    // #endregion [Private] Key hooking private helpers

    // #region: [Internal] Key hooking methods
    KeyHook setSysKeyHook(int keyEventId, KeyHook keyHook, Object... args) {
        KeyHookContext prevKeyHookContext;
        if (keyHook != null) {
            // wire the given event to the new non-null hook
            KeyHookContext newSysHook = new KeyHookContext(keyHook, args);
            prevKeyHookContext = _sysKeyHooks.put(keyEventId, newSysHook);
        } else {
            prevKeyHookContext = _sysKeyHooks.remove(keyEventId);
        }
        return (prevKeyHookContext != null) ? prevKeyHookContext._keyHook : null;
    }

    KeyHook setCustomKeyHook(int keyEventKind, int keyEventId, Object keyEventSource, KeyHook keyHook, Object... args) {
        if ( keyEventId == KeyEvent.VK_ESCAPE || keyEventId == KeyEvent.VK_END || _sysKeyHooks.containsKey(keyEventId)) {
            throw new IllegalArgumentException("Cannot override system keys!");
        }

        Map<Integer, CustomKeyHook> customKeyHooks;
        switch(keyEventKind) {
            case KeyEvent.KEY_TYPED:
                customKeyHooks = _customKeyTypedHooks;
                break;
            case KeyEvent.KEY_PRESSED:
                customKeyHooks = _customKeyPressedHooks;
                break;
            case KeyEvent.KEY_RELEASED:
                customKeyHooks = _customKeyReleasedHooks;
                break;
            default:
                throw new IllegalArgumentException("Unrecognized key event kind!");
        }

        CustomKeyHook prevCustomKeyHook;
        //KeyHookContext prevContext;
        keyEventId = Character.toUpperCase(keyEventId);
        if (keyHook == null) {
            prevCustomKeyHook = customKeyHooks.remove(keyEventId);
        } else {
            KeyHookContext newKeyHookContext = new KeyHookContext(keyHook, args);
            CustomKeyHook newCustomKeyHook = new CustomKeyHook(keyEventSource, newKeyHookContext);
            prevCustomKeyHook = customKeyHooks.put(keyEventId, newCustomKeyHook);
        }
        return prevCustomKeyHook != null ? prevCustomKeyHook._keyHookContext._keyHook : null;
    }
    
    boolean hasCustomHooks() {
        return (_customKeyTypedHooks.size() + _customKeyPressedHooks.size() + _customKeyReleasedHooks.size()) > 0;
    }

    void close() {
        for(CustomKeyHook customHook : _customKeyTypedHooks.values()) {
            customHook.stop();
        }
        for(CustomKeyHook customHook : _customKeyPressedHooks.values()) {
            customHook.stop();
        }
        for(CustomKeyHook customHook : _customKeyReleasedHooks.values()) {
            customHook.stop();
        }
    }
    // #endregion: [Internal] Keys hooking methods
    
    // #region: [Public] KeyListener overrides
    /**
     * Forwards the key typed event to the registered hooks.
     * @param keyEvent the key event to be forwarded.
     */
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        synchronized (_sync) {
            char ch = keyEvent.getKeyChar();
            switch (Character.toUpperCase(ch)) {
            case KeyEvent.VK_1:
                // Continue execution. Ignore all step(0) or lesser,
                // break on next step(1) or greater. 
                _keyStepLevel = DbgLevel.STEP.value();
                _sync.notifyAll();
                forwardKeyEvent(keyEvent, null);
                break;
            case KeyEvent.VK_2:
                // Continue execution. Ignore all step(1) or lesser,
                // break on next step(2) or greater. 
                _keyStepLevel = DbgLevel.LEAP.value();
                _sync.notifyAll();
                forwardKeyEvent(keyEvent, null);
                break;
            case KeyEvent.VK_3:
                // Continue execution. Ignore all step(1) or lesser,
                // break on next step(2) or greater. 
                _keyStepLevel = DbgLevel.JUMP.value();
                _sync.notifyAll();
                forwardKeyEvent(keyEvent, null);
                break;
            case KeyEvent.VK_SPACE:
                // Fast-forward the execution, ignore all code step() calls. 
                _keyStepLevel = DbgLevel.RUN.value();
                _sync.notifyAll();
                forwardKeyEvent(keyEvent, null);
                break;
            default: 
                forwardKeyEvent(keyEvent, _customKeyTypedHooks);
                break;
            }
        }
    }
    
    /**
     * Forwards the key pressed event to the registered hooks.
     * @param keyEvent the key event to be forwarded.
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        synchronized (_sync) {
            switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_END:
                synchronized(_end) {
                    _end.notifyAll();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            default:
                forwardKeyEvent(keyEvent, _customKeyPressedHooks);
            }
        }
    }
    
    /**
     * Forwards the key released event to the registered hooks.
     * @param keyEvent the key event to be forwarded.
     */
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        forwardKeyEvent(keyEvent, _customKeyReleasedHooks);
    }
    // #endregion: [Public] KeyListener overrides
    
    // #region: [Internal] Control methods
    boolean blocksOnLevel(int level) {
        return (level >= _keyStepLevel) && (_keyStepLevel != Integer.MAX_VALUE);
    }

    boolean sleepOnLevel(long delay, int level) {
        return (delay != Long.MAX_VALUE)
            && (level == DbgLevel.STEP.value())
            && (_keyStepLevel == DbgLevel.LEAP.value());
    }

    void step(int level) {
        step(level, Long.MAX_VALUE, null);
    }
    
    void step(int level, long delay, Runnable preWaitTask) {
        synchronized (_sync) {
            try {
                // if the level of the step says we should block..
                if (blocksOnLevel(level)) {
                    // ..wait for user action..
                    if (preWaitTask != null) {
                        preWaitTask.run(); // run the pre-wait task if provided
                    }
                    _sync.wait();
                } else if (sleepOnLevel(delay, level)){
                    // ..otherwise just delay..
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }

    void stop() {
        synchronized(_end) {
            try {
                _end.wait();
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
        }
    }
    
    void simulateKeyTyped(Component source, int keyEventKey) {
        KeyEvent keyEvent = new KeyEvent(
                source,
                KeyEvent.KEY_TYPED,
                0,
                0,
                KeyEvent.VK_UNDEFINED,
                (char)keyEventKey);
        keyTyped(keyEvent);
    }
    // #endregion: [Internal] Control methods
}