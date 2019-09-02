import com.demo.MainTest;
import com.demo.service.Hello;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: lanxinghua
 * Date: 2019/9/2 16:02
 * Desc:
 */
@SpringBootTest(classes = MainTest.class)
@RunWith(SpringRunner.class)
public class BaseTest {
    @Autowired
    private Hello hello;

    @Test
    public void test01(){
        System.out.println("say before");
        hello.say("hello world");
        System.out.println("say after");
    }

    @Test
    public void test02() throws Exception{
        System.out.println("before");
        System.out.println(hello.sayWithReturn("hello world").get());
        System.out.println("after");


        try{
            System.out.println("before");
            System.out.println(hello.sayWithReturn("hello world").get(1, TimeUnit.SECONDS));
            System.out.println("after");
        }catch (TimeoutException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
