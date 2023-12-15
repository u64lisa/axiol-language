package axiol.instructuion.value;

import axiol.instructuion.InstructionElement;
import axiol.types.PrimitiveTypes;
import axiol.types.Type;
import axiol.types.TypeCollection;

public class StringInstructionElement extends InstructionElement {
    private final String value;

    public StringInstructionElement(String value) {
        this.value = value;
    }

    @Override
    public Type size() {
        return TypeCollection.STRING;
    }

    public String getValue() {
        return value;
    }
}
