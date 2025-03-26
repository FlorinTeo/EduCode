import org.junit.Test;

import edu.ftdev.STL.STLPrism;
import edu.ftdev.STL.STLModel;
import edu.ftdev.STL.STLPoint;

public class STL_tests {
    @Test
    public void basicTest() {
        STLModel model = new STLModel();
        model.add(
            new STLPrism(new STLPoint(-5, -5, 0), 30, 40, 20),
            new STLPrism(new STLPoint(0, 0, 20), 20, 30, 10));
        System.out.println(model.serialize());
    }
    
}
