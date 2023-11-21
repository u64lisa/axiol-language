package axiol.parser;

import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.expressions.BinaryExpression;
import axiol.parser.tree.expressions.UnaryExpression;

public class ExpressionParser {

    private final LanguageParser languageParser;

    public ExpressionParser(LanguageParser languageParser) {
        this.languageParser = languageParser;
    }

    public Expression parseExpression(int priority) {
        Operator[] operators = Operator.getOperatorsByPriority(priority);

        if (priority == 0) {
            // atomics
        }

        int operatorCycles = 0;
        Expression leftAssociated = null;

        // unary
        for (Operator operator : operators) {
            if (!operator.isUnary() || operator.isLeftAssociated() ||
                    this.languageParser.getTokenStream().matches(operator.getType()))
                continue;
            this.languageParser.getTokenStream().advance();

            leftAssociated = new UnaryExpression(operator, this.parseExpression(priority));
        }
        if (leftAssociated == null) {
            leftAssociated = this.parseExpression(priority - 1);
        }

        // append last lef-associated Expression
        while (operatorCycles == 0) {
            operatorCycles = 1;

            for (Operator operator : operators) {
                if (!operator.isUnary() || !operator.isLeftAssociated() ||
                        this.languageParser.getTokenStream().matches(operator.getType()))
                    continue;

                languageParser.getTokenStream().advance();
                operatorCycles = 0;

                leftAssociated = new UnaryExpression(operator, leftAssociated);
            }
        }
        // reset for next loop
        operatorCycles = 0;

        // binary expression from past unary left
        while (operatorCycles == 0) {
            operatorCycles = 1;

            for (Operator operator : operators) {
                if (operator.isUnary() || languageParser.getTokenStream().matches(operator.getType())) {
                    continue;
                }

                languageParser.getTokenStream().advance();
                operatorCycles = 0;

                Expression right = operator.isLeftAssociated() ?
                        parseExpression(priority - 1) : parseExpression(priority);

                leftAssociated = new BinaryExpression(operator, leftAssociated, right);
            }
        }


        return leftAssociated;
    }

}
