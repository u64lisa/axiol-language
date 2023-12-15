package axiol.parser;

import axiol.parser.tree.RootNode;
import axiol.parser.tree.statements.LinkedNoticeStatement;
import axiol.parser.util.SourceFile;

public interface RootNodeProcessor<T> {

    RootNode process(RootNode rootNode);
    T processNewReturn(RootNode rootNode);

}
