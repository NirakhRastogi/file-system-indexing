package com.fsindexer;

import com.fsindexer.lucene.IndexCreator;
import com.fsindexer.lucene.ResultParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final String FILE_TO_INDEX = "D:\\Learning\\FSIndexer\\data\\index.data";

    public static void main(String[] args) throws IOException, ParseException {


        IndexCreator indexCreator = new IndexCreator();
        indexCreator.indexFile(FILE_TO_INDEX);



    }
}
