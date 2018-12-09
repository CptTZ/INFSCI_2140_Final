package pitt.infsci2140.finalprj.misc;

import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.springframework.boot.system.ApplicationTemp;
import pitt.infsci2140.finalprj.FinalApplication;

import java.io.File;

public class Config {

    private Config() {}

    public static final String LUCENE_ORIGINAL_INDEX_PATH = "./lucene/index_orig";
    public static final String LUCENE_NLP_INDEX_PATH = "./lucene/index_nlp";
    public static final ApplicationTemp APP_TMP = new ApplicationTemp(FinalApplication.class);
    public static final File IR_TMP_PATH = APP_TMP.getDir("ir");

    public static final Similarity PROJECT_DEFAULT_SIM = new BM25Similarity();

    public static final String INDEXER_COMMENT_ID = "CID";
    public static final String INDEXER_COMMENT_TXT = "TEXT";
    public static final String INDEXER_SHOP_ADDRESS = "ADDR";
    public static final String INDEXER_SHOP_NAME = "NAME";
    public static final String INDEXER_NUM_USEFUL = "C_USEFUL";
    public static final String INDEXER_NUM_FUN = "C_FUN";
    public static final String INDEXER_NUM_COOL = "C_COOL";
    public static final String INDEXER_NUM_STAR = "C_STAR";

}
