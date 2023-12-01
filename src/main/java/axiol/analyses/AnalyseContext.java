package axiol.analyses;

import java.util.ArrayList;
import java.util.List;

public class AnalyseContext {

    private final List<String> mangelElements = new ArrayList<>();

    public boolean checkMangel(String mangel) {
        if (this.mangelElements.contains(mangel)) {
            return false;
        }
        this.mangelElements.add(mangel);
        return true;
    }

}
