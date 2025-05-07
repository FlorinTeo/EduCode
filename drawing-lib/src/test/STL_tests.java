import org.junit.Test;

import edu.ftdev.STL.STLPrism;
import edu.ftdev.STL.STLModel;
import edu.ftdev.STL.STLPoint;

public class STL_tests {
    @Test
    public void basicTest() {
        STLModel model = new STLModel();
        model.add(
            new STLPrism(new STLPoint(-2, -2, 0), 84, 64, 5),
            new STLPrism(new STLPoint(0, 0, 5), 80, 60, 2));
        System.out.println(model.serialize());
    }
}
