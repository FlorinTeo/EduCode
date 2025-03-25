import org.junit.Test;

import edu.ftdev.STL.STLPrism;
import javafx.geometry.Point3D;

public class STLModel_tests {
    @Test
    public void basicTEst() {
        STLPrism prism = new STLPrism(new Point3D(50, 50, 0), 20, 30, 10);
        System.out.println(prism.serialize());
    }
    
}
