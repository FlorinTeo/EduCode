package edu.ftdev;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

/**
 * Generic KeyInterceptor class, to be used for customized key interaction.
 * By default, the interceptor is handling the keys '1', '2', ' ' and <Escape>
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
     * Functional Interface for a generic key hooking method.
     * Users can instantiate a lambda method that can be registered with the
     * drawing engine such that custom code gets called when specific key is pressed.
     * @see #keyHook(KeyEvent)
     */
    public interface KeyHook {
        /**
         * Method called when a registered key event is intercepted.
         * @param keyEvent - the key event that as detected.
         * @see KeyEvent
         */
        public void keyHook(KeyEvent keyEvent);
    }
    
    // Region: [Private] Data fields
    private Object _sync = new Object();
    private Integer _keyStepLevel = Integer.MIN_VALUE;
    private HashMap<Integer, KeyHook> _keyTypedHooks = new HashMap<Integer, KeyHook>();
    private HashMap<Integer, KeyHook> _keyPressedHooks = new HashMap<Integer, KeyHook>();
    private HashMap<Integer, KeyHook> _keyReleasedHooks = new HashMap<Integer, KeyHook>();
    // EndRegion: [Private] Data fields
    
    // Region: [Private] Key hooking private helpers
    private void forwardKeyEvent(KeyEvent e, HashMap<Integer, KeyHook> keyHooks) {
        int hookKey = e.getKeyCode();
        if (hookKey == KeyEvent.VK_UNDEFINED) {
            hookKey = Character.toUpperCase(e.getKeyChar());
        }
        
        if (keyHooks.containsKey(hookKey)) {
            KeyHook targetKeyHook = keyHooks.get(hookKey);
            if (targetKeyHook != null) {
                targetKeyHook.keyHook(e);
            }
        }
    }
    // EndRegion: [Private] Key hooking private helpers
    
    // Region: [Internal] Keys hooking methods
    KeyHook getKeyTypedHook(int keyEventKey) {
        return _keyTypedHooks.get(keyEventKey);
    }
    
    KeyHook setKeyTypedHook(int keyEventKey, KeyHook keyHook) {
        keyEventKey = Character.toUpperCase(keyEventKey);
        return (keyHook == null)
            ? _keyTypedHooks.remove(keyEventKey)
            : _keyTypedHooks.put(keyEventKey, keyHook);
    }
    
    KeyHook getKeyPressedHook(int keyEventKey) {
        return _keyPressedHooks.get(keyEventKey);
    }
    
    KeyHook setKeyPressedHook(int keyEventKey, KeyHook keyHook) {
        return (keyHook == null)
                ? _keyPressedHooks.remove(keyEventKey)
                : _keyPressedHooks.put(keyEventKey, keyHook);
    }
    
    KeyHook getKeyReleasedHook(int keyEventKey) {
        return _keyReleasedHooks.get(keyEventKey);
    }
    
    KeyHook setKeyReleasedHook(int keyEventKey, KeyHook keyHook) {
        return (keyHook == null)
                ? _keyReleasedHooks.remove(keyEventKey)
                : _keyReleasedHooks.put(keyEventKey, keyHook);
    }
    // EndRegion: [Internal] Keys hooking methods
    
    // Region: [Public] KeyListener overrides
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
    // EndRegion: [Public] KeyListener overrides
    
    // Region: [Internal] Control methods
    boolean blocksOnLevel(int level) {
        return (level >= _keyStepLevel);
    }
    
    boolean isFastFwd() {
        return (_keyStepLevel == Integer.MAX_VALUE);
    }
    
    void step(int level) {
        step(level, 0);
    }
    
    void step(int level, long delay) {
        synchronized (_sync) {
            try {
                // block if level is same or greater than the key-typed level.
                // (i.e step_0 won't block if user typed 2)
                if (blocksOnLevel(level)) {
                    _sync.wait();
                } else if (!isFastFwd() && delay > 0) {
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
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
    // EndRegion: [Internal] Control methods
}
