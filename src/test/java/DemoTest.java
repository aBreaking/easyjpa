import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @{USER}
 * @{DATE}
 */
public class DemoTest {

    @Test
    public void test01(){
        Map<String,Integer> identityHashMap= new HashMap<>();
        identityHashMap.put(new String("a"),1);
        identityHashMap.put(new String("a"),1);
        System.out.println(identityHashMap);
        Integer a = identityHashMap.get(new String("a"));
        System.out.println(a);
    }
}
