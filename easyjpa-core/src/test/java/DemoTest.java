import com.abreaking.easyjpa.User;
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

    @Test
    public void test02() throws ClassNotFoundException {
        Class c1 = User.class;
        User u2 = new User();
        Class c2 = u2.getClass();

        Class c3 = Class.forName("com.abreaking.easyjpa.User");
        System.out.println(c1 ==c2);
        System.out.println(c1 ==c3);
    }
}
