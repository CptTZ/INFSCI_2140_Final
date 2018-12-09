package pitt.infsci2140.finalprj.service;

import org.springframework.stereotype.Service;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Service
public class PythonSentimentService {

    public String getPyResult() throws IOException {
        // PY path searchTerm
        ProcessBuilder pb = new ProcessBuilder(Config.NLP_PYTHON_PATH, "scripts/IR-getSentiRank.py", "./pgh_review_nlp.csv", "Chicken");
        Process p = pb.directory(new File(System.getProperty("user.dir"))).start();
        StringWriter sw = new StringWriter(), swE = new StringWriter();
        InputStream in = p.getInputStream();
        InputStream inE = p.getErrorStream();
        int data;
        while ((data = in.read()) != -1) {
            sw.write(data);
        }
        int dataE;
        while ((dataE = inE.read()) != -1) {
            swE.write(dataE);
        }
        try {
            p.waitFor();
        } catch (InterruptedException e) {
            return "";
        }
        return sw.toString();
    }

}
