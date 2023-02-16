package com.codeloam.memory.store;

import com.microsoft.gctoolkit.GCToolKit;
import com.microsoft.gctoolkit.io.SingleGCLogFile;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author jinyu.li
 * @since 1.0
 */
public class GCLogAnalyzer {
    public static void main(String[] args) throws IOException {
        var path = Path.of("D:\\Projects\\book_projects\\in-memory-data-store\\gc_big.log");
        var logFile = new SingleGCLogFile(path);
        var gcToolKit = new GCToolKit();
        var jvm = gcToolKit.analyze(logFile);
//        var results = jvm.getAggregation(HeapOccupancyAfterCollectionSummary.class);
//        System.out.println(results.toString());
    }
}
