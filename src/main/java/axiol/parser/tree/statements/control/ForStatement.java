package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.types.ParsedType;

public class ForStatement extends Statement {

    private final ForCondition condition;
    private final BodyStatement bodyStatement;

    public ForStatement(ForCondition condition, BodyStatement bodyStatement) {
        this.condition = condition;
        this.bodyStatement = bodyStatement;
    }

    public ForCondition getCondition() {
        return condition;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }

    public interface ForCondition { }

    public static class IterateCondition implements ForCondition {
        private final ParsedType type;
        private final String name;
        private final Expression expression;

        public IterateCondition(ParsedType type, String name, Expression expression) {
            this.type = type;
            this.name = name;
            this.expression = expression;
        }

        public ParsedType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public Expression getExpression() {
            return expression;
        }
    }

    public static class NumberRangeCondition implements ForCondition {
        private final Statement statement;
        private final Expression condition, appliedAction;

        public NumberRangeCondition(Statement statement, Expression condition, Expression appliedAction) {
            this.statement = statement;
            this.condition = condition;
            this.appliedAction = appliedAction;
        }

        public Statement getStatement() {
            return statement;
        }

        public Expression getCondition() {
            return condition;
        }

        public Expression getAppliedAction() {
            return appliedAction;
        }
    }

}
