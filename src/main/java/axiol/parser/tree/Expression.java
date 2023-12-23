package axiol.parser.tree;

import axiol.types.SimpleType;

public abstract class Expression extends Statement {

    public abstract SimpleType valuedType();

}
