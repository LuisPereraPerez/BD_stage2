package com.example;

import com.example.control.IndexerControl;
import org.openjdk.jmh.annotations.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class Indexer1Benchmark {

    private IndexerControl indexer;

    @Setup(Level.Trial)
    public void setup() {
        indexer = new IndexerControl();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1)
    @Warmup(iterations = 1)
    public void testIndexingPerformance() throws IOException {
        indexer.executeIndexing();
    }
}
