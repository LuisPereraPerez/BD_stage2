package com.example;

import com.example.control.*;
import com.example.interfaces.*;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class BenchmarkCrawler {

    private static final int NUM_BOOKS = 10;
    private static final String SAVE_DIR = "datalake/books";
    private static final int MAX_DOWNLOAD_ATTEMPTS = 3;

    private MetadataWriter metadataWriter;
    private LastIdManager lastIdManager;
    private BookManager bookManager;
    private GutenbergCrawler crawler;
    private BookDownloader downloader;
    private MetadataExtractor metadataExtractor;
    private BookProcessor bookProcessor;

    @Setup(Level.Trial)
    public void setup() {
        downloader = new GutenbergBookDownloader(SAVE_DIR);
        metadataExtractor = new GutenbergMetadataExtractor();
        bookProcessor = new GutenbergBookProcessor();
        metadataWriter = new CSVMetadataWriter();
        lastIdManager = new FileLastIdManager();

        bookManager = new BookManager(downloader, metadataExtractor, bookProcessor, metadataWriter, SAVE_DIR);
        crawler = new GutenbergCrawlerSequential(bookManager, lastIdManager, MAX_DOWNLOAD_ATTEMPTS);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 1)
    public void testCrawlingPerformance() {
        crawler.startCrawling(NUM_BOOKS);
    }
}
