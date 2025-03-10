package edu.ftdev;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

class DbgButton extends Canvas {

    enum BtnFace {
        NORMAL,
        CLICKED,
        STOPPED
    };
    
    private static final long serialVersionUID = 1L;
    private int _btnKey;
    private BufferedImage[] _btnFaces;
    private BtnFace _idxFace;
    
    public DbgButton(int btnKey, int xAnchor, int yAnchor, String... btnFaceFiles) throws IOException {
        ClassLoader cldr = this.getClass().getClassLoader();
        _btnKey = btnKey;
        _btnFaces = new BufferedImage[btnFaceFiles.length];
        for(int i = 0; i < _btnFaces.length; i++) {
            URL url = cldr.getResource(btnFaceFiles[i]);
            _btnFaces[i] = ImageIO.read(url);
        }
        _idxFace = BtnFace.NORMAL;
        this.setBounds(
                xAnchor, yAnchor,
                _btnFaces[_idxFace.ordinal()].getWidth(), _btnFaces[_idxFace.ordinal()].getHeight());
    }
    
    // #region: [Public] Canvas overrides
    @Override
    public void paint(Graphics g) {
        g.drawImage(_btnFaces[_idxFace.ordinal()], 0, 0, null);
    }
    // #endregion: [Public] Canvas overrides
    
    // #region: [Internal] Accessors and mutators
    int getKey() {
        return _btnKey;
    }

    void setFace(BtnFace idxFace) {
        _idxFace = idxFace;
        repaint();
    }

    BtnFace getFace() {
        return _idxFace;
    }
    // #endregion: [Internal] Accessors and mutators
}
