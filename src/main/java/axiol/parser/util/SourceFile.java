package axiol.parser.util;

import java.io.File;

public class SourceFile {

    private final File folder;
    private final String fileName;
    private final String content;

    public SourceFile(File folder, String fileName, String content) {
        this.folder = folder;
        this.fileName = fileName;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public File getFolder() {
        return folder;
    }

    public String getFileName() {
        return fileName;
    }

    public File asFile() {
        return new File(folder, fileName);
    }
}
