package pitt.infsci2140.finalprj.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pitt.infsci2140.finalprj.controller.search.vo.SearchResultBean;
import pitt.infsci2140.finalprj.misc.Config;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
public class NlpSearchService extends OriginalSearchService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IndexSearcher nlpIndexSearcher;
    private DirectoryReader nlpReader;

    @Autowired
    public NlpSearchService(BusinessService bs) {
        super(bs);
    }

    public List<SearchResultBean> queryNlpByTerm(String term, int businessLimit) {
        if (term == null || term.isEmpty()) return new ArrayList<>(0);
        try {
            TopDocs topDocs = searchNlpTerm(term, businessLimit * 50);
            if (topDocs.totalHits == 0L) return new ArrayList<>(0);

            String taskUuid = UUID.randomUUID().toString();
            File tmpCsvPath = new File(Config.IR_TMP_PATH, taskUuid + ".csv");
            logger.debug("Temp CSV path: {}", tmpCsvPath.getAbsolutePath());

            // Bridge to Python
            writeOutCsvForPyNLP(topDocs.scoreDocs, tmpCsvPath);
            String pythonOutput = getPythonSentimentResult(taskUuid, tmpCsvPath, term);
            JsonNode root = Config.JSON_MAPPER.readTree(pythonOutput);
            int totalHits = root.size();
            int listSize = businessLimit > totalHits ? totalHits : businessLimit;

            // Build results
            ArrayList<SearchResultBean> res = new ArrayList<>(listSize);
            int count = 0;
            for (Iterator<String> it = root.fieldNames(); it.hasNext() && count < businessLimit; count++) {
                String bid = it.next();
                res.add(bidToSearchResultBean(bid, (float) root.get(bid).asDouble()));
            }

            tmpCsvPath.deleteOnExit();
            return res;
        } catch (Exception e) {
            logger.error("Search NLP failed", e);
            return new ArrayList<>(0);
        }
    }

    private void prepSearch() throws IOException {
        if (!isSearcherNotReady()) return;
        this.nlpReader = DirectoryReader.open(FSDirectory.open(Paths.get(Config.LUCENE_NLP_INDEX_PATH)));
        this.nlpIndexSearcher = genIdxSearcher(this.nlpReader, Config.PROJECT_DEFAULT_SIM);
    }

    private boolean isSearcherNotReady() {
        return (this.nlpReader == null || this.nlpIndexSearcher == null);
    }

    private TopDocs searchNlpTerm(String term, int limit) throws ParseException, IOException {
        if (isSearcherNotReady()) prepSearch();
        TopDocs tops = this.nlpIndexSearcher.search(queryParser.parse(term), limit);

        logger.debug("Term {}, result#: {}", term, tops.totalHits);
        return tops;
    }

    private String getReviewTextByDocid(int docid) throws IOException {
        return this.nlpIndexSearcher.doc(docid).get(Config.INDEXER_COMMENT_TXT);
    }

    private String getBusinessIdByDocId(int docid) throws IOException {
        return this.nlpIndexSearcher.doc(docid).get(Config.INDEXER_BUSS_ID);
    }

    private void writeOutCsvForPyNLP(ScoreDoc[] docs, File tmpCsvPath) throws IOException {
        try (CSVPrinter p = new CSVPrinter(new FileWriter(tmpCsvPath), CSVFormat.RFC4180)) {
            p.printRecord("business_id", "review");
            for (ScoreDoc doc : docs) {
                p.printRecord(getBusinessIdByDocId(doc.doc), getReviewTextByDocid(doc.doc));
            }
        }
    }

    private String getPythonSentimentResult(String taskUuid, File csvPath, String queryTerm) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(Config.NLP_PYTHON_PATH, "scripts/IR-getSentiRank.py",
                csvPath.getAbsolutePath(), queryTerm);
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
            // no close because they all does nothing
        } catch (InterruptedException e) {
            logger.error("'{}' Interrupted!", taskUuid);
            return "";
        }
        logger.error("'{}' error stream: {}", taskUuid, swE.toString());
        return sw.toString();
    }

}
