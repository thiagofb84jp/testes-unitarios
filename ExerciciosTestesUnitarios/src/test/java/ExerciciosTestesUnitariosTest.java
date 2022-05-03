import org.junit.Assert;
import org.junit.Test;

public class ExerciciosTestesUnitariosTest {

    @Test
    public void test() {
        int a = 10;
        int b = 20;

        int sum = a + b;

        Assert.assertEquals(sum, 30);
    }
}
