package com.fsindexer.controller;

import com.fsindexer.request.QueryRequest;
import com.fsindexer.service.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class IndexingController {

    private final IndexingService indexingService;

    @GetMapping("/reindex")
    public ResponseEntity<Boolean> startReindexing() {
        return ResponseEntity.ok(this.indexingService.startIndexing());
    }

    @PostMapping("/query/index")
    public ResponseEntity<List<String>> queryIndex(@RequestBody QueryRequest query) {
        return ResponseEntity.ok(this.indexingService.getQueryPaths(query.getQuery()));
    }


}
