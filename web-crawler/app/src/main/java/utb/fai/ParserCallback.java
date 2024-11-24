package utb.fai;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

class ParserCallback extends HTMLEditorKit.ParserCallback {
    LinkedBlockingQueue<URIinfo> uriQueue = new LinkedBlockingQueue<>();
    Set<URI> visited = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap<String, Integer> wordCounts = new ConcurrentHashMap<>();
    final int depthLimit;
    final int verbosity;
    Set<Future<?>> runningTasks = ConcurrentHashMap.newKeySet();

    ParserCallback(URI startURI, int maxDepth, int debugLevel) {
        uriQueue.add(new URIinfo(startURI, 0));
        depthLimit = maxDepth;
        verbosity = debugLevel;
    }

    void process(URIinfo info) {
        try {
            if (verbosity > 0) System.err.println("Processing: " + info.uri);

            var document = Jsoup.connect(info.uri.toString()).get();
            extractWords(document.body().text());

            if (info.depth < depthLimit) findLinks(document, info.depth + 1);

        } catch (IOException e) {
            if (verbosity > 1) e.printStackTrace();
        }
    }

    private void extractWords(String text) {
        String[] tokens = text.replace("?", "").toLowerCase().split("[\\s,.;!?()\\[\\]{}\\-]+");

        Map<String, Integer> localCounts = new HashMap<>();
        for (String word : tokens) {
            if (!word.isEmpty()) localCounts.merge(word, 1, Integer::sum);
        }
        localCounts.forEach((word, count) -> wordCounts.merge(word, count, Integer::sum));
    }

    private void findLinks(Document document, int nextDepth) {
        Elements anchors = document.select("a[href], frame[src], iframe[src]");
        anchors.stream()
                .map(el -> el.hasAttr("href") ? el.attr("href") : el.attr("src"))
                .filter(this::isValidLink)
                .distinct()
                .forEach(link -> uriQueue.add(new URIinfo(link, nextDepth)));

        if (verbosity > 1) System.err.println("Links found: " + anchors.size());
    }

    private boolean isValidLink(String link) {
        return link != null && !link.isBlank() && Pattern.matches("^https://[\\w.-]+.*$", link);
    }

    boolean hasMoreWork() {
        return !uriQueue.isEmpty() || !runningTasks.isEmpty();
    }

    void displayWordCounts() {
        wordCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(20)
                .forEach(entry -> System.out.println(entry.getKey() + ";" + entry.getValue()));
    }
}
