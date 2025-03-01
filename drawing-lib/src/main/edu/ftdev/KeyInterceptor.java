package edu.ftdev;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

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
     * Functional Interface for a generic key hooking method.
     * Users can instantiate a lambda method that can be registered with the
     * drawing engine such that custom code gets called when specific key is pressed.
     * @see #keyHook(KeyEvent, Object[])
     */
    public interface KeyHook {
        /**
         * Method called when a registered key event is intercepted.
         * @param keyEvent - the key event that as detected.
         * @param args - optional arguments to be passed to the hook when event occurs.
         * @see KeyEvent
         */
        public void keyHook(KeyEvent keyEvent, Object[] args);
    }
    
    // #region: [Private] Data fields
    private Object _sync = new Object();
    private Integer _keyStepLevel = Integer.MIN_VALUE;
    private HashMap<Integer, KeyHookContext> _keyTypedHooks = new HashMap<Integer, KeyHookContext>();
    private HashMap<Integer, KeyHookContext> _keyPressedHooks = new HashMap<Integer, KeyHookContext>();
    private HashMap<Integer, KeyHookContext> _keyReleasedHooks = new HashMap<Integer, KeyHookContext>();
    // #endregion: [Private] Data fields
    
    // #region: [Private] Key hooking private helpers
    private KeyHook getKeyHook(int keyEventKey) {
        KeyHookContext keyHookContext = _keyTypedHooks.get(keyEventKey);
        return keyHookContext != null ? keyHookContext._keyHook : null;
    }

    private KeyHook setKeyHook(int keyEventKey, KeyHook keyHook, Object... args) {
        KeyHookContext prevContext;
        keyEventKey = Character.toUpperCase(keyEventKey);
        if (keyHook == null) {
            prevContext = _keyTypedHooks.remove(keyEventKey);
        } else {
            KeyHookContext newContext = new KeyHookContext(keyHook, args);
            prevContext = _keyTypedHooks.put(keyEventKey, newContext);
        }
        return prevContext != null ? prevContext._keyHook : null;
    }

    private void forwardKeyEvent(KeyEvent e, HashMap<Integer, KeyHookContext> keyHookContexts) {
        int hookKey = e.getKeyCode();
        if (hookKey == KeyEvent.VK_UNDEFINED) {
            hookKey = Character.toUpperCase(e.getKeyChar());
        }
        
        if (keyHookContexts.containsKey(hookKey)) {
            KeyHookContext targetKeyHookContext = keyHookContexts.get(hookKey);
            if (targetKeyHookContext != null) {
                targetKeyHookContext._keyHook.keyHook(e, targetKeyHookContext._args);
            }
        }
    }
    // #endregion: [Private] Key hooking private helpers
    
    // #region: [Internal] Keys hooking methods
    KeyHook getKeyTypedHook(int keyEventKey) {
        return getKeyHook(keyEventKey);
    }
    
    KeyHook setKeyTypedHook(int keyEventKey, KeyHook keyHook, Object... args) {
        return setKeyHook(keyEventKey, keyHook, args);
    }
    
    KeyHook getKeyPressedHook(int keyEventKey) {
        return getKeyHook(keyEventKey);
    }
    
    KeyHook setKeyPressedHook(int keyEventKey, KeyHook keyHook, Object... args) {
        return setKeyHook(keyEventKey, keyHook, args);
    }
    
    KeyHook getKeyReleasedHook(int keyEventKey) {
        return getKeyHook(keyEventKey);
    }
    
    KeyHook setKeyReleasedHook(int keyEventKey, KeyHook keyHook, Object... args) {
        return setKeyHook(keyEventKey, keyHook, args);
    }
    // #endregion: [Internal] Keys hooking methods
    
    // #region: [Public] KeyListener overrides
    @Override
    public void keyTyped(KeyEvent keyEvent) {
        synchronized (_sync) {
            char ch = keyEvent.getKeyChar();
            switch (Character.toUpperCase(ch)) {
            case '1':
                // Continue execution. Ignore all step(0) or lesser,
                // break on next step(1) or greater. 
                _keyStepLevel = 1;
                _sync.notifyAll();
                break;
            case '2':
                // Continue execution. Ignore all step(1) or lesser,
                // break on next step(2) or greater. 
                _keyStepLevel = 2;
                _sync.notifyAll();
                break;
            case '3':
                // Continue execution. Ignore all step(1) or lesser,
                // break on next step(2) or greater. 
                _keyStepLevel = 3;
                _sync.notifyAll();
                break;
            case ' ':
                // Fast-forward the execution, ignore all code step() calls. 
                _keyStepLevel = Integer.MAX_VALUE;
                _sync.notifyAll();
                break;
            }
            forwardKeyEvent(keyEvent, _keyTypedHooks);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        synchronized (_sync) {
            switch (keyEvent.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            default:
                forwardKeyEvent(keyEvent, _keyPressedHooks);
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent keyEvent) {
        forwardKeyEvent(keyEvent, _keyReleasedHooks);
    }
    // #endregion: [Public] KeyListener overrides
    
    // #region: [Internal] Control methods
    boolean blocksOnLevel(int level) {
        return (level >= _keyStepLevel) && (_keyStepLevel != Integer.MAX_VALUE);
    }

    void step(int level) {
        step(level, Long.MAX_VALUE);
    }
    
    void step(int level, long delay) {
        synchronized (_sync) {
            try {
                // if the level of the step says we should block..
                if (blocksOnLevel(level)) {
                    // ..if is definite pause..
                    if (delay == Long.MAX_VALUE) {
                        // ..wait for user action..
                        _sync.wait();
                    } else {
                        // ..otherwise just delay..
                        Thread.sleep(delay);
                    }
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
    
    void simulateKeyTyped(Component source, int keyEventKey) {
        KeyEvent keyEvent = new KeyEvent(
                source,
                0,
                0,
                0,
                keyEventKey,
                (char)keyEventKey);
        keyTyped(keyEvent);
    }
    // #endregion: [Internal] Control methods
}