package axiol.parser.util;

public class SourceFile {

    private final String filePath;
    private final String content;

    public SourceFile(String filePath, String content) {
        this.filePath = filePath;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getFilePath() {
        return filePath;
    }
}
