
import org.apache.commons.lang.time.FastDateFormat;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @{USER}
 * @{DATE}
 */
public class Whatever {

    @Test
    public void test02(){
        FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-mm");
        String format = dateFormat.format(new Date());
        System.out.println(format);
    }

    public void test01(){
        Map map = new HashMap();

    }
}
