package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwitchStatement extends Statement {
    private final Expression condition;
    private final CaseElement[] cases;

    public SwitchStatement(Expression condition, CaseElement[] cases) {
        this.condition = condition;
        this.cases = cases;
    }

    public boolean hasDefaultCase() {
        for (CaseElement aCase : cases) {
            if (aCase.defaultState)
                return true;
        }
        return false;
    }
    public CaseElement getDefaultCase() {
        for (CaseElement aCase : cases) {
            if (aCase.defaultState)
                return aCase;
        }
        return null;
    }

    public CaseElement[] getCases() {
        return cases;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public List<Statement> childStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(condition);
        for (CaseElement aCase : cases) {
            statements.addAll(Arrays.stream(aCase.conditions).toList());
            statements.add(aCase.body);
        }

        return statements;
    }

    public static class CaseElement {
        private final boolean defaultState;
        private final Expression[] conditions;
        private final Statement body;

        public CaseElement(boolean defaultState, Expression[] conditions, Statement body) {
            this.defaultState = defaultState;
            this.conditions = conditions;
            this.body = body;
        }

        public boolean isDefaultState() {
            return defaultState;
        }

        public Expression[] getConditions() {
            return conditions;
        }

        public Statement getBody() {
            return body;
        }
    }
}
