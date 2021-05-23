package com.fsindexer.lucene;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ResultParser {

    @Value("${index.directory}")
    private String indexDirectory;
    @Value("${index.max-top-docs}")
    private int maxTopDocs;

    public List<Document> parseQuery(String inField, String queryString) throws ParseException, IOException {
        LOGGER.info("Replacing non readable characters....");
        queryString = queryString.replaceAll("^[/|\\\\]+$", "_#@").replace(":", "@@__");
        LOGGER.info("Non readable characters replaced successfully....");
        LOGGER.info("Creating query parse for field {} and query {}", inField, queryString);
        Query query = new QueryParser(inField, new StandardAnalyzer())
                .parse(queryString);
        LOGGER.info("Opening index directory...");
        Directory indexDirectory = FSDirectory
                .open(Paths.get(this.indexDirectory));
        LOGGER.info("Directory opened successfully...");
        LOGGER.info("Reading index for result...");
        IndexReader indexReader = DirectoryReader
                .open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, maxTopDocs);
        LOGGER.info("Indexes read and result received successfully...");

        return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    try {
                        return searcher.doc(scoreDoc.doc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

}

