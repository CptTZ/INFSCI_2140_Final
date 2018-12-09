package pitt.infsci2140.finalprj;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pitt.infsci2140.finalprj.service.PythonSentimentService;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FinalApplicationTests {

    @Test
    //@Ignore
    public void pySmTest() throws IOException {
        PythonSentimentService p = new PythonSentimentService();
        p.getPyResult();
    }

}
