package utb.fai;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App {
    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        URI startingURI;
        int depthLimit = 2;
        int verbosity = 0;

        try {
            if (args.length == 0) {
                System.err.println("Missing starting URL.");
                return;
            }

            startingURI = new URI(args[0] + "/");
            if (args.length > 1) depthLimit = Integer.parseInt(args[1]);
            if (args.length > 2) verbosity = Integer.parseInt(args[2]);

            var callback = new ParserCallback(startingURI, depthLimit, verbosity);

            while (callback.hasMoreWork()) {
                var currentInfo = callback.uriQueue.poll();

                if (currentInfo != null && callback.visited.add(currentInfo.uri)) {
                    var task = threadPool.submit(() -> callback.process(currentInfo));
                    callback.runningTasks.add(task);
                } else {
                    Thread.sleep(100);
                }

                callback.runningTasks.removeIf(Future::isDone);
            }

            threadPool.shutdown();
            callback.displayWordCounts();

            if (verbosity > 1) callback.visited.forEach(System.out::println);
            if (verbosity > 0) System.err.println("Visited URIs: " + callback.visited.size());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
