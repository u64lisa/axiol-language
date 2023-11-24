package axiol.parser;

import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.expressions.ArrayInitExpression;
import axiol.parser.tree.expressions.BinaryExpression;
import axiol.parser.tree.expressions.UnaryExpression;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.control.TernaryExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.tree.expressions.sub.StringExpression;
import axiol.parser.util.error.Position;
import axiol.parser.util.stream.TokenStream;

import javax.swing.text.ViewFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /* todo
     * x array = {expr, expr}
     * x array = [10];
     * x array = [_];
     *
     * x var = expr ? expr : expr;
     *
     * - ref = &expression;
     *
     * - var = test.t;
     * - var = test.*t;
     **/
    public Expression parseExpression(int priority) {
        if (priority < 0)
            priority = 0;

        Operator[] operators = Operator.getOperatorsByPriority(priority).toArray(new Operator[0]);

        if (priority == 0) {
            // expr ? expr : expr;
            if (tokenStream.matches(TokenType.QUESTION)) {
                this.tokenStream.advance();

                Expression ifTrue = this.parseExpression(Operator.MAX_PRIORITY);

                if (!this.languageParser.expected(TokenType.COLON)) {
                    return null;
                }
                tokenStream.advance();

                Expression ifFalse = this.parseExpression(Operator.MAX_PRIORITY);

                return new TernaryExpression(ifTrue, ifFalse);
            }
            // [_] empty array 0 elements
            // [expression] sized empty array
            if (tokenStream.matches(TokenType.L_SQUARE)) {
                this.tokenStream.advance();

                if (this.tokenStream.matches(TokenType.UNDERSCORE)) {
                    this.tokenStream.advance();

                    this.languageParser.expected(TokenType.R_SQUARE);
                    Token current = this.tokenStream.current();
                    this.tokenStream.advance();

                    return new ArrayInitExpression(new ArrayList<>(), new NumberExpression(
                            current.getPosition(), 0, true));
                }
                Expression expression = this.parseExpression(0);

                this.languageParser.expected(TokenType.R_SQUARE);
                this.tokenStream.advance();

                return new ArrayInitExpression(new ArrayList<>(), expression);
            }
            // {expr, expr, expr, ...}
            if (tokenStream.matches(TokenType.L_CURLY)) {
                this.tokenStream.advance();

                List<Expression> expressions = new ArrayList<>();

                while (!tokenStream.matches(TokenType.R_CURLY)) {
                    Expression element = this.parseExpression(0);
                    expressions.add(element);

                    if (this.tokenStream.matches(TokenType.R_CURLY))
                        continue;

                    if (!this.languageParser.expected(TokenType.COMMA)) {
                        return null;
                    }
                    tokenStream.advance();
                }
                this.languageParser.expected(TokenType.R_CURLY);
                this.tokenStream.advance();

                return new ArrayInitExpression(expressions, new NumberExpression(
                        tokenStream.prev().getPosition(), expressions.size(), true));
            }
            if (Arrays.stream(valueContainingTypes)
                    .anyMatch(type -> type.equals(this.tokenStream.current().getType()))) {
                return parseTypeExpression();
            }
            if (tokenStream.matches(TokenType.AND)) {
                this.tokenStream.advance();

                // todo parse assignment of pointer address
                return null;
            }
            if (tokenStream.matches(TokenType.MATCH)) {
                return this.parseMatchExpression();
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
            NumberExpression numberExpression = new NumberExpression(
                    this.tokenStream.current().getPosition(), value, signed);

            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.CHAR)) {
            String singletonChar = this.tokenStream.current().getValue()
                    .substring(1, tokenStream.current().getValue().length() - 1);

            int value = singletonChar.charAt(0);

            NumberExpression numberExpression = new NumberExpression(
                    this.tokenStream.current().getPosition(), value, false);

            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.STRING)) {
            String singletonString = this.tokenStream.current().getValue()
                    .substring(1, tokenStream.current().getValue().length() - 1);

            StringExpression stringExpression = new StringExpression(
                    this.tokenStream.current().getPosition(), singletonString);

            this.tokenStream.advance();
            return stringExpression;
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

    private MatchExpression parseMatchExpression() {
        this.tokenStream.advance();

        Token start = this.tokenStream.current();

        if (!languageParser.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression expression = parseExpression(Operator.MAX_PRIORITY);

        if (!languageParser.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!languageParser.expected(TokenType.L_CURLY))
            return null;
        this.tokenStream.advance();

        List<MatchExpression.CaseElement> caseElements = new ArrayList<>();

        while (this.tokenStream.matches(TokenType.R_CURLY)) {
            if (this.tokenStream.current().getType().equals(TokenType.DEFAULT) ||
                    this.tokenStream.current().getType().equals(TokenType.CASE)) {
                List<Expression> conditions = new ArrayList<>();
                boolean defaultState = false;

                if (this.tokenStream.matches(TokenType.CASE)) {
                    this.tokenStream.advance();

                    while (!this.tokenStream.current().getType().equals(TokenType.LAMBDA) &&
                            !this.tokenStream.current().getType().equals(TokenType.COLON)) {
                        conditions.add(parseExpression(Operator.MAX_PRIORITY));

                        if (!this.tokenStream.current().getType().equals(TokenType.LAMBDA) &&
                                !this.tokenStream.current().getType().equals(TokenType.COLON)) {
                            if (!languageParser.expected(TokenType.COMMA))
                                return null;
                            this.tokenStream.advance();
                        }
                    }
                }
                if (this.tokenStream.matches(TokenType.DEFAULT)) {
                    if (caseElements.stream().anyMatch(MatchExpression.CaseElement::isDefaultState)) {
                        this.languageParser.createSyntaxError("default statement already defined!");
                    }

                    this.tokenStream.advance();
                    defaultState = true;
                    // we don't have any conditions!
                }

                Expression body = null;
                if (this.tokenStream.current().getType().equals(TokenType.LAMBDA)) {
                    this.tokenStream.advance();

                    body = parseExpression(Operator.MAX_PRIORITY);
                }

                if (this.tokenStream.current().getType().equals(TokenType.SEMICOLON)) {
                    this.tokenStream.advance();
                }

                    caseElements.add(new MatchExpression.CaseElement(defaultState, conditions.toArray(new Expression[0]), body));

                continue;
            }

            languageParser.createSyntaxError(start, "expected 'case' or 'default' but got '%s'",
                    this.tokenStream.current().getType());
        }

        if (!languageParser.expected(TokenType.R_CURLY))
            return null;
        this.tokenStream.advance();

        if (caseElements.isEmpty()) {
            languageParser.createSyntaxError(start, "can't compile match expression with no cases!");
            return null;
        }
        if (caseElements.size() == 1 && caseElements.stream().anyMatch(MatchExpression.CaseElement::isDefaultState)) {
            languageParser.createSyntaxError(start, "can't compile match expression with only default case!");
            return null;
        }


        return new MatchExpression(expression, caseElements.toArray(new MatchExpression.CaseElement[0]));
    }


}
