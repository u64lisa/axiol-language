package axiol.utils;

import java.util.HashMap;
import java.util.Map;

public class Profiler {

    private final Map<String, Long> times = new HashMap<>();

    private void printHead(String text) {
        int padding = Math.max(0, (120 - text.length()) / 2);
        int remainingPadding = 120 - text.length() - 2 * padding;

        String printedText = "-".repeat(padding) + " " + text + " " + "-".repeat(padding);
        System.out.println(printedText + "-".repeat(Math.max(0, remainingPadding)));
    }

    public void startProfilingSection(String name, String message) {
        this.printHead(message);

        this.times.put(name, System.currentTimeMillis());
    }

    public void endProfilingSection(String name, String message) {
        this.printHead(message.formatted(System.currentTimeMillis() - times.getOrDefault(name, System.currentTimeMillis())));
        times.remove(name);
    }



}
