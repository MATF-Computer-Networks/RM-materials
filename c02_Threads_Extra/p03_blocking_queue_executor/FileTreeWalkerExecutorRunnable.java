package p03_blocking_queue_executor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

final class FileTreeWalkerExecutorRunnable implements Runnable {
    private final Path startingDir;
    private final String keyword;
    private final ExecutorService executor;

    FileTreeWalkerExecutorRunnable(Path startingDir, String keyword, ExecutorService executor) {
        this.startingDir = startingDir;
        this.keyword = keyword;
        this.executor = executor;
    }

    @Override
    public void run() {
        // Files.walk(Path) will walk through all the files for us.
        // We're using it in try-with-resources so the files are properly closed afterwards.
        try(Stream<Path> filesStream = Files.walk(startingDir)) {
            filesStream.filter(Files::isRegularFile) // we want only regular files...
                    .forEach(path -> executor.submit(makeSearchFileRunnable(path, keyword))); //... and for each file we
                                                                                              // submit some work to executor
            // we could also have a separate SearchFileRunnable class, or use a lambda.
            // because executor uses a BlockingQueue underneath, it takes care not too many tasks are submitted
            // we also don't need to handle end of work specially
        } catch (IOException e) {
            System.err.printf("Failed to walk through path %s. Unexpected %s occurred: %s\n", startingDir, e.getClass().getSimpleName(), e.getMessage());
        }
    }

    // this is Java's version of a higher-order function (kind of), i.e. a function returning a (kind of) function
    // calling this method won't execute anything—it just returns a Runnable, to be executed sometime later
    private static Runnable makeSearchFileRunnable(Path path, String keyword) {
        return () -> {
            // this is equivalent to the logic in SearchFileRunnable, which used Scanner to read file line-by-line
            // if we didn't care about reading huge files all at once, we could've used Files.readLines(path) and iterateld over lines
            // if we didn't care about reading huge files and line numbers, we could've used just Files.readString(path).contains(keyword);
            try(Stream<String> linesStream = Files.lines(path)) {
                int[] keywordPositions = linesStream.mapToInt(line -> line.contains(keyword) ? 1 : 0).toArray();
                for(int i=0; i<keywordPositions.length; i++) {
                    if(keywordPositions[i] == 1) {
                        System.out.printf("%s:%d\n", path.getFileName(), i);
                    }
                }
            } catch (IOException e) {
                System.err.printf("Failed to process path %s. Unexpected %s occurred: %s\n", path, e.getClass().getSimpleName(), e.getMessage());
            }
        };
    }
}
