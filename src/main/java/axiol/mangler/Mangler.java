package axiol.mangler;

import axiol.parser.statement.Parameter;
import axiol.parser.tree.statements.VariableStatement;
import axiol.parser.tree.statements.oop.ClassTypeStatement;
import axiol.parser.tree.statements.oop.ConstructStatement;
import axiol.parser.tree.statements.oop.FunctionStatement;
import axiol.parser.tree.statements.oop.StructTypeStatement;
import axiol.types.SimpleType;
import axiol.types.Type;

import java.util.List;

public class Mangler {

    private final String FLAG_FORMAT = "_%s(%s)";

    // functional
    public String mangelFunctionGlobalScope(FunctionStatement functionStatement) {
        return mangelFunction(FLAG_FORMAT.formatted("scope", "global"), functionStatement);
    }

    public String mangelFunction(String leading, FunctionStatement functionStatement) {
        return FLAG_FORMAT.formatted("fun", leading) +
                FLAG_FORMAT.formatted("name", functionStatement.getName()) +
                FLAG_FORMAT.formatted("parameters", mangelParameters(functionStatement.getParameters())) +
                FLAG_FORMAT.formatted("ret_t", mangelParseType(functionStatement.getReturnType()));
    }

    public String mangelConstruct(String leading, ConstructStatement constructStatement) {
        return FLAG_FORMAT.formatted("construct", leading) +
                FLAG_FORMAT.formatted("parameters", this.mangelParameters(constructStatement.getParameters()));
    }
    // functional

    // other
    public String mangelVariableGlobalScope(VariableStatement variableStatement) {
        return mangelVariable(FLAG_FORMAT.formatted("scope", "global"), variableStatement);
    }
    public String mangelVariable(String leading, VariableStatement variableStatement) {
        return FLAG_FORMAT.formatted("var", leading) +
                FLAG_FORMAT.formatted("name", variableStatement.getName()) +
                FLAG_FORMAT.formatted("type", this.mangelParseType(variableStatement.getType()));
    }

    public String mangelParameters(List<Parameter> parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < parameters.size(); i++) {
            Parameter parameter = parameters.get(i);

            stringBuilder.append(FLAG_FORMAT.formatted("p_" + i, this.mangelParameter(parameter)));
        }

        return stringBuilder.toString();
    }

    public String mangelParameter(Parameter parameter) {
        return FLAG_FORMAT.formatted("name", parameter.getName()) +
                FLAG_FORMAT.formatted("p_type", this.mangelParseType(parameter.getParsedType())) +
                FLAG_FORMAT.formatted("pointer", parameter.isPointer()) +
                FLAG_FORMAT.formatted("ref", parameter.isReferenced())
                ;
    }

    public String mangelParseType(SimpleType type) {
        return FLAG_FORMAT.formatted("type", mangelType(type.getType())) +
                FLAG_FORMAT.formatted("a_dep", type.getArrayDepth()) +
                FLAG_FORMAT.formatted("p_dep", type.getPointerDepth());
    }

    public String mangelType(Type type) {
        return FLAG_FORMAT.formatted("name", type.getName()) +
                FLAG_FORMAT.formatted("primitive", type.getPrimitiveTypes().name());
    }
    // other

    // udt
    public String mangelClassGlobalScope(ClassTypeStatement classTypeStatement) {
        return mangelClass(FLAG_FORMAT.formatted("scope", "global"), classTypeStatement);
    }

    public String mangelClass(String leading, ClassTypeStatement classTypeStatement) {
        return FLAG_FORMAT.formatted("class", leading) +
                FLAG_FORMAT.formatted("name", classTypeStatement.getName()) +
                FLAG_FORMAT.formatted("parent", classTypeStatement.getParent());

    }

    public String mangelStructGlobalScope(StructTypeStatement structTypeStatement) {
        return mangelStruct(FLAG_FORMAT.formatted("scope", "global"), structTypeStatement);
    }

    public String mangelStruct(String leading, StructTypeStatement structTypeStatement) {
        return FLAG_FORMAT.formatted("struct", leading) +
                FLAG_FORMAT.formatted("name", structTypeStatement.getName()) +
                FLAG_FORMAT.formatted("parameters", mangelParameters(structTypeStatement.getEntries()));
    }
    // udt
}
