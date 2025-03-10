package edu.ftdev;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.ftdev.DbgButton.BtnFace;
import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.MouseInterceptor.MouseHook;

/**
 * Encapsulates a representation of a generic drawing image frame as a window that 
 * can be interacted with. A DrawingFrame object can be created only by providing a valid
 * Drawing object as argument to its constructor. In return, the object can be used for
 * displaying the drawing image on the screen and reflecting its changes as coded
 * in the program, in an interactive manner.
 */
public class DrawingFrame implements 
    WindowListener, WindowFocusListener, DbgControls, FrameControls {
    
    private Thread _mainThread = null;
    private String _title = "Drawing Framework GUI";
    private int _padding = 4;
    private int _status_XY_width = 32;
    private int _status_Text_width = 400;
    private int _status_height = 20;

    private MouseEvent _lastMouseEvent = null;
    private Drawing _drawing = null;
    private Frame _frame = null;
    private DbgButton[] _dbgButtons = null;
    private DrawingCanvas _canvas = null;
    private TextField _statusX = null;
    private TextField _statusY = null;
    private TextField _statusText = null;

    /**
     * Flag telling if the frame is opened on the screen
     */
    private boolean _isOpened = false;

    /**
     * KeyInterceptor object to intercept key events and trigger custom actions.
     */
    protected KeyInterceptor _keyInterceptor = new KeyInterceptor();
    /**
     * MouseInterceptor object to intercept mouse events and trigger custom actions.
     */
    protected MouseInterceptor _mouseInterceptor = new MouseInterceptor();
    
    // #region: [Private] KeyInterceptor hooks
    private KeyInterceptor.KeyHook _onKeyInterceptorCtrl = (keyEvent, args) -> {
        char ch = Character.toUpperCase(keyEvent.getKeyChar());
        switch (ch) {
        case KeyEvent.VK_1: // break on {step}
            _dbgButtons[0].setFace(BtnFace.CLICKED);
            _dbgButtons[1].setFace(BtnFace.NORMAL);
            _dbgButtons[2].setFace(BtnFace.NORMAL);
            _dbgButtons[3].setFace(BtnFace.NORMAL);
            break;
        case KeyEvent.VK_2: // break on {.leap.}
            _dbgButtons[0].setFace(BtnFace.NORMAL);
            _dbgButtons[1].setFace(BtnFace.CLICKED);
            _dbgButtons[2].setFace(BtnFace.NORMAL);
            _dbgButtons[3].setFace(BtnFace.NORMAL);
            break;
        case KeyEvent.VK_3: // break on {.jump.}
            _dbgButtons[0].setFace(BtnFace.NORMAL);
            _dbgButtons[1].setFace(BtnFace.NORMAL);
            _dbgButtons[2].setFace(BtnFace.CLICKED);
            _dbgButtons[3].setFace(BtnFace.NORMAL);
            break;
        case KeyEvent.VK_SPACE: // run >ffwd
            _dbgButtons[0].setFace(BtnFace.NORMAL);
            _dbgButtons[1].setFace(BtnFace.NORMAL);
            _dbgButtons[2].setFace(BtnFace.NORMAL);
            _dbgButtons[3].setFace(BtnFace.CLICKED);
            break;
        }
    };
    // #endregion: [Private] KeyInterceptor hooks

    // #region: [Private] MouseInterceptor hooks
    private MouseInterceptor.MouseHook _onMouseClicked = (e, args) -> {
        if (e.getSource() instanceof DbgButton) {
            DbgButton dbgButton = (DbgButton)e.getSource();
            _keyInterceptor.simulateKeyTyped(dbgButton, dbgButton.getKey());
        } else {
            BufferedImage bi = _drawing.getImage();
            int x = _canvas.xScreenToCanvas(e.getX());
            int y = _canvas.yScreenToCanvas(e.getY());
            Color c = new Color(bi.getRGB(x, y));
            _statusText.setText(String.format("R:%d, G:%d, B:%d", c.getRed(), c.getGreen(), c.getBlue()));
            _canvas.repaint();
        }
    };

    private MouseInterceptor.MouseHook _onMouseDragged = (e, args) -> {
        _canvas.pan(e.getX()-_lastMouseEvent.getX(), e.getY()-_lastMouseEvent.getY());
        _lastMouseEvent = e;
    };

    private MouseInterceptor.MouseHook _onMouseMoved = (e, args) -> {
        _lastMouseEvent = e;
        _statusX.setText(""+_canvas.xScreenToCanvas(e.getX()));
        _statusY.setText(""+_canvas.yScreenToCanvas(e.getY()));
        //_statusText.setText("");
    };

    private MouseInterceptor.MouseHook _onMouseWheelMoved = (e, args) -> {
        // wheel upwards (negative rotation) => zoom in => positive level value
        int levels = -((MouseWheelEvent)e).getWheelRotation();
        _canvas.zoom(e.getX(), e.getY(), levels);
    };
    // #endregion: [Private] MouseInterceptor hooks

    // #region: [Private] DbgButtons management
    private void dbgButtonsSetup(int xAnchor, int yAnchor) {
        _dbgButtons = new DbgButton[4];

        try {
            _dbgButtons[0] = new DbgButton(
                KeyEvent.VK_1,
                xAnchor,
                yAnchor,
                "edu/ftdev/res/step_0.png", "edu/ftdev/res/step_1.png", "edu/ftdev/res/step_2.png");
            _dbgButtons[0].setFace(BtnFace.CLICKED);
            xAnchor += _dbgButtons[0].getWidth();
    
            _dbgButtons[1] = new DbgButton(
                KeyEvent.VK_2,
                xAnchor,
                yAnchor,
                "edu/ftdev/res/leap_0.png", "edu/ftdev/res/leap_1.png", "edu/ftdev/res/leap_2.png");
            xAnchor += _dbgButtons[1].getWidth();
    
            _dbgButtons[2] = new DbgButton(
                KeyEvent.VK_3,
                xAnchor,
                yAnchor,
                "edu/ftdev/res/jump_0.png", "edu/ftdev/res/jump_1.png", "edu/ftdev/res/jump_2.png");
            xAnchor += _dbgButtons[2].getWidth();
    
            _dbgButtons[3] = new DbgButton(
                KeyEvent.VK_SPACE,
                xAnchor,
                yAnchor,
                "edu/ftdev/res/run_0.png", "edu/ftdev/res/run_1.png", "edu/ftdev/res/run_2.png");
            xAnchor += _dbgButtons[2].getWidth();
        } catch (IOException e) {
            // won't happen: the jar is configured to include the resources
            e.printStackTrace();
        }
    }
    // #endregion: [Private] DbgButtons management
    
    // #region: [Private] StatusBar management
    private void statusBarSetup(int xAnchor, int yAnchor, int width) {
        int x = xAnchor;
        _statusX = new TextField();
        _statusX.setEditable(false);
        _statusX.setBackground(Color.LIGHT_GRAY);
        _statusX.setBounds(x, yAnchor, _status_XY_width, _status_height);
        x += _status_XY_width;
        _statusY = new TextField();
        _statusY.setEditable(false);
        _statusY.setBackground(Color.LIGHT_GRAY);
        _statusY.setBounds(x, yAnchor, _status_XY_width, _status_height);
        x += _status_XY_width;
        _statusText = new TextField();
        _statusText.setEditable(false);
        _statusText.setBackground(Color.LIGHT_GRAY);
        int w = Math.min(_status_Text_width, width - x);
        _statusText.setBounds(
                Math.max(x, xAnchor + width - _status_Text_width),
                yAnchor, 
                w,
                _status_height);
    }
    // #endregion: [Private] StatusBar management
    
    /**
     * Creates an instance of a DrawingFrame object encapsulating the representation of a window displaying the pixels of the given drawing object.
     * @param drawing - the drawing to be displayed by this frame.
     */
    public DrawingFrame(Drawing drawing) {
        // keep track of the creating thread
        _mainThread = Thread.currentThread();

        // setup callback methods for keyInterceptor control keys
        _keyInterceptor.setSysKeyHook(KeyEvent.VK_1, _onKeyInterceptorCtrl);
        _keyInterceptor.setSysKeyHook(KeyEvent.VK_2, _onKeyInterceptorCtrl);
        _keyInterceptor.setSysKeyHook(KeyEvent.VK_3, _onKeyInterceptorCtrl);
        _keyInterceptor.setSysKeyHook(KeyEvent.VK_SPACE, _onKeyInterceptorCtrl);
        // setup callback methods for mouseInterceptor events
        _mouseInterceptor.setSysMouseHook(MouseEvent.MOUSE_CLICKED, _onMouseClicked);
        _mouseInterceptor.setSysMouseHook(MouseEvent.MOUSE_DRAGGED, _onMouseDragged);
        _mouseInterceptor.setSysMouseHook(MouseEvent.MOUSE_MOVED, _onMouseMoved);
        _mouseInterceptor.setSysMouseHook(MouseEvent.MOUSE_WHEEL, _onMouseWheelMoved);
        
        _drawing = drawing;
        
        // create the frame and get the insets
        _frame = new Frame(_title);
        _frame.setBackground(Color.LIGHT_GRAY);
        _frame.pack();
        Insets insets = _frame.getInsets();
        
        // setup the xAnchor and yAnchor to anchor controls
        int xAnchor = insets.left + _padding;
        int yAnchor = insets.top + _padding;
        
        // create the debug buttons
        dbgButtonsSetup(xAnchor, yAnchor);
        yAnchor += _dbgButtons[0].getHeight() + _padding;
        
        // create the map canvas
        _canvas = new DrawingCanvas(xAnchor, yAnchor, _drawing);
        _canvas.setFocusTraversalKeysEnabled(false);
        _canvas.addKeyListener(_keyInterceptor);
        _canvas.addMouseMotionListener(_mouseInterceptor);
        _canvas.addMouseListener(_mouseInterceptor);
        _canvas.addMouseWheelListener(_mouseInterceptor);
        yAnchor += _canvas.getHeight() + _padding;
        
        // create the status bar indicators
        statusBarSetup(xAnchor, yAnchor, _drawing.getWidth());
        yAnchor += _status_height + _padding;
        
        // layout the frame size and attributes
        _frame.setSize(
                xAnchor + _canvas.getWidth() + _padding + insets.right,
                yAnchor + insets.bottom);
        _frame.setLayout(null);
        _frame.setLocationRelativeTo(null);
        _frame.setResizable(false);
        
        // add the controls
        for(DbgButton dbgButton : _dbgButtons) {
            dbgButton.addMouseListener(_mouseInterceptor);
            dbgButton.addKeyListener(_keyInterceptor);
            _frame.add(dbgButton);
        }
        
        _frame.add(_canvas);
        _frame.add(_statusX);
        _frame.add(_statusY);
        _frame.add(_statusText);
        
        // add the listeners
        _frame.addKeyListener(_keyInterceptor);
        _frame.addWindowListener(this);
        _frame.addWindowFocusListener(this);
    }
    
    /**
     * Gets the X canvas coordinate of a mouse click.
     * @param mouseEvent - the mouse event as given to a mouse handler.
     * @return X canvas coordinate.
     */
    public int getCanvasX(MouseEvent mouseEvent) {
        return _canvas.xScreenToCanvas(mouseEvent.getX());
    }

    /**
     * Gets the Y canvas coordinate of a mouse click.
     * @param mouseEvent - the mouse event as given to a mouse handler.
     * @return X canvas coordinate.
     */
    public int getCanvasY(MouseEvent mouseEvent) {
        return _canvas.yScreenToCanvas(mouseEvent.getY());
    }
    
    /**
     * Gets the state of visibility of the drawing frame.
     * @return true if the drawing frame is currently visible (opened) on the screen.
     */
    public boolean isOpened() {
        return _isOpened;
    }

    // #region: [Interface] DbgControls overrides
    /**
     * In "step" mode, this method pauses the execution with a default empty string message.
     * It does nothing in any other modes.
     * @returns true if execution was suspended, false otherwise.
     * @see #breakStep(String)
     */
    @Override
    public boolean breakStep() {
        return breakStep("");
    }

    /**
     * In "step" mode, this method pauses the execution. It does nothing in any other modes.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * @param breakMessage the message labeling the breaking point.
     * @returns true if execution was suspended, false otherwise.
     * @see DbgControls#breakStep(String)
     */
    @Override
    public boolean breakStep(String breakMessage) {
        return step(1, Long.MAX_VALUE, breakMessage);
    }

    /**
     * In "step" mode, this method delays execution for the given number of
     * milliseconds with a default empty string message. It does nothing in any other mode.
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @returns true if execution was suspended, false otherwise.
     * @see #breakStep(long)
     */
    @Override
    public boolean breakStep(long delay) {
        return breakStep(delay, "");
    }
    
    /**
     * In "step" mode, this method delays execution for the given number of
     * milliseconds. It does nothing in any other mode.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @param breakMessage the message labeling the breaking point.
     * @returns true if execution was suspended, false otherwise.
     * @see DbgControls#breakStep(long)
     */
    @Override
    public boolean breakStep(long delay, String breakMessage) {
        return step(1, delay, breakMessage);
    }

    /**
     * In "step" or "leap" modes, this method pauses the execution until resumed, with a
     * default empty string message. It does nothing in "jump" or "run" modes.
     * @returns true if execution was suspended, false otherwise.
     * @see #breakLeap(String)
     */
    @Override
    public boolean breakLeap() {
        return breakLeap("");
    }
    
    /**
     * In "step" or "leap" modes, this method pauses the execution until resumed.
     * It does nothing in "jump" or "run" modes.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * @param breakMessage the message labeling the breaking point.
     * @returns true if execution was suspended, false otherwise.
     * @see DbgControls#breakStep(String)
     * @see DbgControls#breakJump(String)
     */
    @Override
    public boolean breakLeap(String breakMessage) {
        return step(2, Long.MAX_VALUE, breakMessage);
    }

    /**
     * In "step", "leap" or "jump" modes, this method pauses the execution until resumed,
     * with a default empty string message
     * It does nothing in "run" mode.
     * @returns true if execution was suspended, false otherwise.
     * @see DbgControls#breakStep()
     * @see DbgControls#breakLeap()
     */
    @Override
    public boolean breakJump() {
        return breakJump("");
    }

     /**
     * In "step", "leap" or "jump" modes, this method pauses the execution until resumed.
     * It does nothing in "run" mode.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * @param breakMessage the message labeling the breaking point.
     * @returns true if execution was suspended, false otherwise.
     * @see DbgControls#breakStep()
     * @see DbgControls#breakLeap()
     */
    @Override
    public boolean breakJump(String breakMessage) {
        return step(3, Long.MAX_VALUE, breakMessage);
    }
    
    private boolean step(int level, long delay, String breakMessage) {
        // if the execution mode is at a break level which would not cause an interruption,
        // or mouse custom hooks are in effect and this is the main thread, just return instantly (no-op).
        // if mouse custom hooks are in effect, break controls are expected to be in the hooks. UI
        // actions would be ambiguous if both main thread and hook thread breaks were active, so
        // we give priority to the ones in the hooks and inhibit the ones in the main thread.
        if (!_isOpened
            || !_keyInterceptor.blocksOnLevel(level) 
            || (_mouseInterceptor.hasCustomHooks() && Thread.currentThread() == _mainThread) 
            || (_keyInterceptor.hasCustomHooks() && Thread.currentThread() == _mainThread)) {
            return false;
        }

        // From here on, we know execution is affected: either suspended or delayed.
        // If suspended, change the face of the corresponding button.
        if (delay == Long.MAX_VALUE) {
            switch(level) {
            case 1: // step()
                _dbgButtons[0].setFace(BtnFace.STOPPED);
                _dbgButtons[1].setFace(BtnFace.NORMAL);
                _dbgButtons[2].setFace(BtnFace.NORMAL);
                _dbgButtons[3].setFace(BtnFace.NORMAL);
                break;
            case 2: // leap()
                _dbgButtons[0].setFace(BtnFace.NORMAL);
                _dbgButtons[1].setFace(BtnFace.STOPPED);
                _dbgButtons[2].setFace(BtnFace.NORMAL);
                _dbgButtons[3].setFace(BtnFace.NORMAL);
                break;
            case 3: // jump()
                _dbgButtons[0].setFace(BtnFace.NORMAL);
                _dbgButtons[1].setFace(BtnFace.NORMAL);
                _dbgButtons[2].setFace(BtnFace.STOPPED);
                _dbgButtons[3].setFace(BtnFace.NORMAL);
                break;
            }                
        }

        // Repaint the canvas.
        _canvas.repaint();

        // output the current stack trace for all but step() (to give a chance for user-provided text to show in the UI)
        String crtStatusText = _statusText.getText();
        if (!breakMessage.isEmpty()) {
            _statusText.setText(breakMessage);
        } else if (crtStatusText.isEmpty()) {
            StackTraceElement stackFrame = new Throwable().getStackTrace()[1];
            _statusText.setText(String.format("%s @ %d",stackFrame.getFileName(), stackFrame.getLineNumber()));
        }

        // call below may block, depending on the step level in code and the last debug action by the user
        _keyInterceptor.step(level, delay);

        // restore the status text after potential blocking stop
        _statusText.setText(crtStatusText);

        return true;
    }
    // #endregion: [Interface] DbgControls overrides
    
    // #region: [Public] Key hooking methods
    /**
     * Sets a key hook to be called when a given key is typed.
     * @param keyEvent - the key type event to be intercepted.
     * @param keyHook - the key hook to be called when the key is typed.
     * @param args - additional arguments to be passed to the key hook when called.
     * @return - the key hook previously set for the given event, or null if none exist.
     */
    public KeyHook setKeyTypedHook(int keyEvent, KeyHook keyHook, Object... args) {
        return _keyInterceptor.setCustomKeyHook(KeyEvent.KEY_TYPED, keyEvent, null, keyHook, args);
    }
    
    /**
     * Sets a key hook to be called when a given key is pressed.
     * @param keyEvent - the key pressed event to be intercepted.
     * @param keyHook - the key hook to be called when the key is pressed.
     * @param args - additional arguments to be passed to the key hook when called.
     * @return - the key hook previously set for the given event, or null if none exist.
     */
    public KeyHook setKeyPressedHook(int keyEvent, KeyHook keyHook, Object... args) {
        return _keyInterceptor.setCustomKeyHook(KeyEvent.KEY_PRESSED, keyEvent, null, keyHook, args);
    }
    
    /**
     * Sets a key hook to be called when a given key is released.
     * @param keyEvent the key released event to be intercepted.
     * @param keyHook the key hook to be called when the key is released.
     * @param args additional arguments to be passed to the key hook when called.
     * @return the key hook previously set for the given event, or null if none exist.
     */
    public KeyHook setKeyReleasedHook(int keyEvent, KeyHook keyHook, Object... args) {
        return _keyInterceptor.setCustomKeyHook(KeyEvent.KEY_RELEASED, keyEvent, null, keyHook, args);
    }
    // #endregion: [Public] Key hooking methods
 
    // #region: [Public] Mouse hooking methods
    /**
     * Sets a mouse hook to be called when the left mouse button is clicked.
     * @param mouseHook the mouse hook to be called when the button is clicked
     * or null if the event should not be intercepted.
     * @param args additional arguments to be passed to the mouse hook when called.
     * @return the mouse hook previously set for the given event, or null if none exist.
     */
    public MouseHook setMouseClickedHook(MouseHook mouseHook, Object... args) {
        return _mouseInterceptor.setCustomMouseHook(MouseEvent.MOUSE_CLICKED, _canvas, mouseHook, args);
    }
    // #endregion: [Public] Mouse hooking methods

    // #region: [Public] FrameControls overrides
    /**
     * Gets the window title of the drawing frame.
     * @return the window title of the drawing frame.
     */
    public String getTitle() {
        return _title;
    }
    
    /**
     * Sets the window title of the drawing frame.
     * @param title the new title to be set on the drawing frame window.
     */
    public void setTitle(String title) {
        _title = title;
        _frame.setTitle(_title);
    }
    
    /**
     * Opens a window on the screen, displaying the associated Drawing
     * and the controls for interacting with it. 
     */
    @Override
    public void open() {
        // set the canvas reference in the drawing to allow subclasses to trigger repaints
        _drawing._drwCanvas = _canvas;
        _frame.setVisible(true);
        _isOpened = true;
    }
    
    /**
     * Forces a refresh of the window content such that any changes that may have been
     * operated on the associated Drawing are reflected on the screen.
     */
    @Override
    public void repaint() {
        _canvas.repaint();
    }
    
    /**
     * Prints out the given message in the status bar area, the lower right corner of
     * the drawing window.
     * @param message - message to be printed in the status bar area.
     */
    @Override
    public void setStatusMessage(String message) {
        _statusText.setText(message);
    }
    
    /**
     * Closes the window.
     */
    @Override
    public void close() {
        // close is disabled on main thread if there are mouse custom hooks in effect
        // since frame is expected to be closed via UI.
        if (_mouseInterceptor.hasCustomHooks() && Thread.currentThread() == _mainThread
           || _keyInterceptor.hasCustomHooks() && Thread.currentThread() == _mainThread) {
            _keyInterceptor.stop();
        }
        // close the frame and the drawing - the DrawingFrame should no longer be used after this.
        if (_frame != null) {
            _mouseInterceptor.close();
            _keyInterceptor.close();
            _frame.removeKeyListener(_keyInterceptor);
            _frame.removeMouseListener(_mouseInterceptor);
            _frame.removeWindowListener(this);
            _frame.setVisible(false);
            _frame.dispose();
            _frame = null;
        }
        if (_drawing != null) {
            _drawing.close();
            _drawing = null;
        }
        _isOpened = false;
    }
    // #endregion: [Public] FrameControls overrides

    // #region: [Interface] WindowListener overrides
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.close();
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    // #endregion: [Interface] WindowListener overrides

    // #region: [Interface] WindowFocusListener overrides
    @Override
    public void windowGainedFocus(WindowEvent e) {
        _canvas.requestFocus();
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
    }
    // #endregion: [Interface] WindowFocusListener overrides
}