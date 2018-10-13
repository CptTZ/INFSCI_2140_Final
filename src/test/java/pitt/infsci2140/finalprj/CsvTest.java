package pitt.infsci2140.finalprj;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.junit.Test;

import java.io.FileReader;
import java.io.Reader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class CsvTest {

    @Test
    public void testParse() throws Exception {
        // Change the path to pgh_review.csv
        Reader in = new FileReader("./pgh_review.csv");
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
        long i = 1;
        try {
            for (CSVRecord record : records) {
                i++;
                String comment = record.get("comment_text");
                String usefulCount = record.get("comment_useful");
                String starCount = record.get("comment_star");
                try {
                    assertFalse(String.format("Error at line <%d>", i), Integer.parseInt(usefulCount) < 0);
                    assertFalse(String.format("Error at line <%d>", i), Double.parseDouble(starCount) < 0);
                } catch (NumberFormatException n) {
                    fail("Line " + i);
                }
            }
        } catch (Exception e) {
            fail("Line " + i + ". Msg: " + e.getLocalizedMessage());
        }
        in.close();
    }

}
