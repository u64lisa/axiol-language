package axiol.parser.tree.expressions.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.control.SwitchStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchExpression extends Expression {
    private final Expression condition;
    private final MatchExpression.CaseElement[] cases;

    public MatchExpression(Expression condition, MatchExpression.CaseElement[] cases) {
        this.condition = condition;
        this.cases = cases;
    }

    public boolean hasDefaultCase() {
        for (MatchExpression.CaseElement aCase : cases) {
            if (aCase.defaultState)
                return true;
        }
        return false;
    }
    public MatchExpression.CaseElement getDefaultCase() {
        for (MatchExpression.CaseElement aCase : cases) {
            if (aCase.defaultState)
                return aCase;
        }
        return null;
    }

    @Override
    public List<Statement> childStatements() {
        List<Statement> statements = new ArrayList<>();
        statements.add(condition);
        for (MatchExpression.CaseElement aCase : cases) {
            statements.addAll(Arrays.stream(aCase.conditions).toList());
            statements.add(aCase.body);
        }

        return statements;
    }

    public MatchExpression.CaseElement[] getCases() {
        return cases;
    }

    public Expression getCondition() {
        return condition;
    }

    public static class CaseElement {
        private final boolean defaultState;
        private final Expression[] conditions;
        private final Expression body;

        public CaseElement(boolean defaultState, Expression[] conditions, Expression body) {
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

        public Expression getBody() {
            return body;
        }
    }
}
