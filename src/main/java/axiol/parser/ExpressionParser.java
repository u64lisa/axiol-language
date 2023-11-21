package axiol.parser;

import axiol.lexer.TokenType;
import axiol.parser.LanguageParser;
import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.expressions.UnaryExpression;

import java.util.List;

public class ExpressionParser {

    private final LanguageParser languageParser;

    public ExpressionParser(LanguageParser languageParser) {
        this.languageParser = languageParser;
    }

    public Expression parseExpression(int priority) {
        Operator[] operators = Operator.getOperatorsByPriority(priority);

        Expression leftAssociated = null;
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

        return null;
    }

    // binary
    // unary
    // num
    // string
    // call

}
