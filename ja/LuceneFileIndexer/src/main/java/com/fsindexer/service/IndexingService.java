package com.fsindexer.service;

import com.fsindexer.lucene.IndexCreator;
import com.fsindexer.lucene.ResultParser;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndexingService {

    @Value("${index.data.file}")
    private String dataFile;

    private final IndexCreator indexCreator;
    private final ResultParser resultParser;


    public boolean startIndexing() {
        try {
            indexCreator.indexFile(dataFile);
        } catch (IOException e) {
            throw new RuntimeException("Unable to index file, " + dataFile, e.getCause());
        }
        return true;
    }

    public List<String> getQueryPaths(String query) {
        List<String> output = null;
        try {
            List<Document> results = resultParser.parseQuery("contents", query);
            output = results.stream().map(result -> result.get("path")).collect(Collectors.toList());
        } catch (ParseException e) {
            throw new RuntimeException("Unable to parse query, " + query, e.getCause());
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while reading index, " + query + ", FS might not be indexed. Please run indexing again.", e.getCause());
        }
        return output;
    }

}
