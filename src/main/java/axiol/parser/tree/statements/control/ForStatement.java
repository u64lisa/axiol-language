package axiol.parser.tree.statements.control;

import axiol.parser.tree.Expression;
import axiol.parser.tree.NodeType;
import axiol.parser.tree.Statement;
import axiol.parser.tree.statements.BodyStatement;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.reference.Reference;

import java.util.ArrayList;
import java.util.List;

public class ForStatement extends Statement {

    private final ForCondition condition;
    private final BodyStatement bodyStatement;

    public ForStatement(ForCondition condition, BodyStatement bodyStatement, TokenPosition position) {
        this.condition = condition;
        this.bodyStatement = bodyStatement;
        this.position = position;
    }

    private final TokenPosition position;

    @Override
    public TokenPosition position() {
        return position;
    }

    public ForCondition getCondition() {
        return condition;
    }

    public BodyStatement getBodyStatement() {
        return bodyStatement;
    }

    @Override
    public List<Statement> childStatements() {
        List<Statement> statements = new ArrayList<>();

        if (condition instanceof IterateCondition iterateCondition) {
            statements.add(iterateCondition.expression);
        } else {
            NumberRangeCondition rangeCondition = (NumberRangeCondition) condition;
            statements.add(rangeCondition.condition);
            statements.add(rangeCondition.statement);
            statements.add(rangeCondition.appliedAction);
        }

        statements.add(bodyStatement);
        return statements;
    }

    @Override
    public NodeType type() {
        return NodeType.FOR_STATEMENT;
    }

    public interface ForCondition { }

    public static class IterateCondition implements ForCondition {
        private final Reference reference;
        private final Expression expression;

        public IterateCondition(Reference reference, Expression expression) {
            this.reference = reference;
            this.expression = expression;
        }

        public Reference getReference() {
            return reference;
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
