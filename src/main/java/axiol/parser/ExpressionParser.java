package axiol.parser;

import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.expressions.BinaryExpression;
import axiol.parser.tree.expressions.UnaryExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.util.stream.TokenStream;

import java.util.Arrays;

public class ExpressionParser {

    private final TokenType[] valueContainingTypes = {
            // chars strings
            TokenType.STRING, TokenType.CHAR,
            // true false
            TokenType.BOOLEAN,
            // numbers
            TokenType.INT, TokenType.LONG, TokenType.DOUBLE,
            TokenType.FLOAT, TokenType.HEX_NUM,
            // other
            TokenType.LITERAL
    };
    private final TokenType[] numberContainingTypes = {
            TokenType.INT, TokenType.LONG, TokenType.DOUBLE,
            TokenType.FLOAT, TokenType.HEX_NUM,
    };

    private final LanguageParser languageParser;
    private final TokenStream tokenStream;

    public ExpressionParser(LanguageParser languageParser) {
        this.languageParser = languageParser;

        this.tokenStream = languageParser.getTokenStream();
    }

    public Expression parseExpression(int priority) {
        Operator[] operators = Operator.getOperatorsByPriority(priority).toArray(new Operator[0]);

        if (priority == 0) {
            if (Arrays.stream(valueContainingTypes)
                    .anyMatch(type -> type.equals(tokenStream.current().getType()))) {
                return parseTypeExpression();
            }
            if (tokenStream.matches(TokenType.L_PAREN)) {
                this.tokenStream.advance();
                Expression expression = parseExpression(Operator.MAX_PRIORITY);
                if (tokenStream.matches(TokenType.R_PAREN)) {
                    this.tokenStream.advance();
                } else {
                    this.languageParser.createSyntaxError(
                            "expected closing parenthesis but got '%s'",
                            tokenStream.current().getValue());
                }
                return expression;
            }
            // atomics
        }

        int operatorCycles = 0;
        Expression leftAssociated = null;

        // unary
        for (Operator operator : operators) {
            if (!operator.isUnary() || operator.isLeftAssociated() ||
                    !this.tokenStream.matches(operator.getType()))
                continue;

            this.tokenStream.advance();

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
                        !this.tokenStream.matches(operator.getType()))
                    continue;

                tokenStream.advance();
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
                if (operator.isUnary() || !tokenStream.matches(operator.getType())) {
                    continue;
                }

                tokenStream.advance();
                operatorCycles = 0;

                Expression right = operator.isLeftAssociated() ?
                        parseExpression(priority - 1) : parseExpression(priority);

                leftAssociated = new BinaryExpression(operator, leftAssociated, right);
            }
        }


        return leftAssociated;
    }

    private Expression parseTypeExpression() {
        if (Arrays.stream(numberContainingTypes)
                .anyMatch(type -> type.equals(tokenStream.current().getType()))) {

            String tokenValue = this.tokenStream.current().getValue();
            double value = 0; // default init 0
            boolean signed = true;

            if (this.tokenStream.matches(TokenType.HEX_NUM)) {
                value = Long.parseUnsignedLong(tokenValue.substring(2), 16);
            } else {
                if (tokenValue.charAt(tokenValue.length() - 1) == 'u' ||
                        tokenValue.charAt(tokenValue.length() - 1) == 'U') {
                    signed = false;

                    tokenValue = tokenValue.substring(0, tokenValue.length() - 1);
                }

                value = Double.parseDouble(tokenValue);
            }
            NumberExpression numberExpression = new NumberExpression(this.tokenStream.current().getPosition(), value);
            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.BOOLEAN)) {
            BooleanExpression expression = new BooleanExpression(tokenStream.current().getPosition(),
                    tokenStream.current().getValue().equals("true"));

            this.tokenStream.advance();
            return expression;
        }

        this.languageParser.createSyntaxError(
                "invalid token for expression parsing: '%s'",
                this.tokenStream.current().getType());
        return null;
    }

}
