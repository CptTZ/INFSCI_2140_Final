package pitt.infsci2140.finalprj.misc;

import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;

public class Config {

    private Config() {}

    public static final String LUCENE_INDEX_PATH = "./lucene/index";

    public static final Similarity PROJECT_DEFAULT_SIM = new BM25Similarity();

    public static final String INDEXER_COMMENT_ID = "CID";
    public static final String INDEXER_SHOP_ADDRESS = "ADDR";
    public static final String INDEXER_COMMENT_TXT = "TEXT";
    public static final String INDEXER_SHOP_NAME = "NAME";

}
