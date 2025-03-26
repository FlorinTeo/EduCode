import org.junit.Test;

import edu.ftdev.STL.STLPrism;
import edu.ftdev.STL.STLPoint;

public class STL_tests {
    @Test
    public void basicTest() {
        STLPrism prism = new STLPrism(new STLPoint(50, 50, 0), 20, 30, 10);
        System.out.println(prism.serialize());
    }
    
}
