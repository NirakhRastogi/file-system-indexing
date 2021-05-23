package com.fsindexer.lucene;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class IndexCreator {

    @Value("${index.directory}")
    private String indexDirectory;

    public void indexFile(String filePath) throws IOException {
        LOGGER.info("Clearing already build indexes at {}", indexDirectory);
        clearIndexDirectory();
        LOGGER.info("Existing indexes cleared...");

        Path dataFilePath = Paths.get(filePath);
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();

        LOGGER.info("Reading index data file at path {}", filePath);
        Stream<String> paths = Files.lines(dataFilePath);
        List<Document> documents = paths.map(path -> {
            Document document = new Document();
            document.add(
                    new TextField("contents", new InputStreamReader(new ByteArrayInputStream(path.replaceAll("^[/|\\\\]+$", "_#@").replace(":", "@@__").
                            getBytes(StandardCharsets.UTF_8)))));
            document.add(
                    new StringField("path", path, Field.Store.YES));
            return document;
        }).collect(Collectors.toList());

        LOGGER.info("Documents build successfully....");
        LOGGER.info("Creating lucene indexes");
        Directory indexDirectory = FSDirectory.open(Paths.get(this.indexDirectory));
        IndexWriter indexWriter = new IndexWriter(
                indexDirectory, indexWriterConfig);
        indexWriter.addDocuments(documents);
        indexWriter.close();
        indexDirectory.close();
        LOGGER.info("Lucene indexes created successfully....");

    }

    private void clearIndexDirectory() throws IOException {

        if (!Files.exists(Paths.get(indexDirectory))) {
            return;
        }

        Files.list(Paths.get(indexDirectory))
                .forEach(filePath -> {
                    try {
                        Files.delete(filePath);
                    } catch (IOException e) {
                        LOGGER.info("Unable to delete following index file, reason {}", e.getMessage());
                    }
                });

    }

}
