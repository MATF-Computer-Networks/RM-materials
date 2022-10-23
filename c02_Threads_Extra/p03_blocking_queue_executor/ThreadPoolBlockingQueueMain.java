package p03_blocking_queue_executor;

import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

final class ThreadPoolBlockingQueueMain {
    private static final int WORK_QUEUE_SIZE = 10;
    private static final int THREADS_NUM = 5;

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter base dir:");
        String dir = sc.nextLine();
        System.out.println("Enter keyword:");
        String keyword = sc.nextLine();
        sc.close();

        // queue our executor will pull work from. we've renamed the FILE_QUEUE_SIZE to WORK_QUEUE_SIZE, but it's the same thing.
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(WORK_QUEUE_SIZE);

        ExecutorService executor = new ThreadPoolExecutor(THREADS_NUM, THREADS_NUM, // min and max number of threads in executor
                0, TimeUnit.SECONDS, // how long extra threads wait without work before they die (useless if min==max, as we have)
                workQueue, // the queue our executor uses for work
                new ThreadPoolExecutor.CallerRunsPolicy() // what happens when queue is full, and all threads in the thread pool
                // are busy. Here we're telling it to run tasks on the thread calling submit(). This is a bit different from the
                // original problem solution, which just blocked. While there are ways to do that with executor as well, there are no
                // _good_ ways to do that: https://stackoverflow.com/questions/3446011/threadpoolexecutor-block-when-queue-is-full/3518588#3518588
        );

        // we're only submitting a FileTreeWalker—it will submit Runnables for searching files, and executor will handle
        // the BlockingQueue part. our new FileTreeWalker now needs to accept executor as well.
        // we also assign the returned Future to a variable, so we can know when it's done.
        Future<?> fileTreeWalkerFuture = executor.submit(new FileTreeWalkerExecutorRunnable(Paths.get(dir), keyword, executor));

        try {
            //get(long, TimeUnit) will wait for Future to finish, or throw an exception if it times out
            fileTreeWalkerFuture.get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.err.println("FileTreeWalker went on for over 30s. Cancelling it and finishing the execution.");
            fileTreeWalkerFuture.cancel(true);
        }

        // after FileTreeWalker is done, tell the executor to stop accepting new tasks with shutdown(),
        // and give it some time to finish existing ones with awaitTermination(long, TimeUnit)
        // calling awaitTermination without calling shutdown has no effect, as executors don't really ever "finish",
        // unless they are shut down.
        executor.shutdown();
        if(!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            System.err.println("Executor seems to be still working after 30s of waiting. Shutting down forcefully...");
            // shutdownNow will interrupt all executing threads and return immediately, allowing us to finish our program cleanly
            List<Runnable> runnables = executor.shutdownNow();
            System.err.printf("There were %d unfinished tasks.\n", runnables.size());
        }

        System.out.println("Program finished!");
    }
}
