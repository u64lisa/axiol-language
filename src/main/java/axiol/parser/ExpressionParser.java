package axiol.parser;

import axiol.lexer.Token;
import axiol.lexer.TokenType;
import axiol.parser.expression.Operator;
import axiol.parser.tree.Expression;
import axiol.parser.tree.expressions.*;
import axiol.parser.tree.expressions.control.MatchExpression;
import axiol.parser.tree.expressions.extra.CastExpression;
import axiol.parser.tree.expressions.extra.ElementReferenceExpression;
import axiol.parser.tree.expressions.extra.StackAllocExpression;
import axiol.parser.tree.expressions.sub.BooleanExpression;
import axiol.parser.tree.expressions.sub.NumberExpression;
import axiol.parser.tree.expressions.sub.StringExpression;
import axiol.parser.util.error.TokenPosition;
import axiol.parser.util.scope.Scope;
import axiol.parser.util.scope.ScopeAble;
import axiol.parser.util.scope.ScopeElement;
import axiol.parser.util.stream.TokenStream;
import axiol.types.PrimitiveTypes;
import axiol.types.Reference;
import axiol.types.SimpleType;
import axiol.types.custom.I128;
import axiol.types.custom.U128;

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
            // big numbers
            TokenType.BIG_NUMBER, TokenType.BIG_HEX_NUM,
            // other
            TokenType.LITERAL
    };
    private final TokenType[] numberContainingTypes = {
            TokenType.INT, TokenType.LONG, TokenType.DOUBLE,
            TokenType.FLOAT, TokenType.HEX_NUM,

            TokenType.BIG_NUMBER, TokenType.BIG_HEX_NUM
    };

    private final LanguageParser languageParser;
    private final TokenStream tokenStream;

    private final Scope rootScope;

    public ExpressionParser(LanguageParser languageParser, Scope rootScope) {
        this.languageParser = languageParser;

        this.tokenStream = languageParser.getTokenStream();
        this.rootScope = rootScope;
    }

    /*
     * x array = {expr, expr}
     * x array = [10];
     * x array = [_];
     *
     * x var = expr ? expr : expr;
     *
     * x ref = &expression;
     * x ref = cast[type] expression;
     * x ref = stackAlloc[type];
     *
     * x var = test.t;
     * - var = test.*t; TODO
     *
     * - var = (test) -> null;
     * - var = (test) -> {};
     * - var = () -> null;
     * - var = () -> {};
     **/
    public Expression parseExpression(Scope scope, SimpleType simpleType, int priority) {
        if (priority < 0)
            priority = 0;

        Operator[] operators = Operator.getOperatorsByPriority(priority).toArray(new Operator[0]);

        if (priority == 0) {
            // &expr
            if (tokenStream.matches(TokenType.AND)) {
                this.tokenStream.advance();

                return new ElementReferenceExpression(this.parseExpression(scope, simpleType, Operator.MAX_PRIORITY), this.tokenStream.currentPosition());
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

                    return new ArrayInitExpression(new ArrayList<>(), simpleType, new NumberExpression(
                            current.getTokenPosition(), 0, PrimitiveTypes.I32.toType(), true), this.tokenStream.currentPosition());
                }
                Expression expression = this.parseExpression(scope, simpleType, 0);

                this.languageParser.expected(TokenType.R_SQUARE);
                this.tokenStream.advance();

                return new ArrayInitExpression(new ArrayList<>(), simpleType, expression, this.tokenStream.currentPosition());
            }
            if (tokenStream.matches(TokenType.CAST)) {
                TokenPosition tokenPosition = tokenStream.currentPosition();
                this.tokenStream.advance();

                this.languageParser.expected(TokenType.L_SQUARE);
                this.tokenStream.advance();

                SimpleType type = languageParser.parseType();

                this.languageParser.expected(TokenType.R_SQUARE);
                this.tokenStream.advance();

                Expression expression = this.languageParser.parseExpression(scope, type);
                return new CastExpression(tokenPosition, type, expression);
            }
            if (tokenStream.matches(TokenType.STACK_ALLOC)) {
                TokenPosition tokenPosition = tokenStream.currentPosition();
                this.tokenStream.advance();

                this.languageParser.expected(TokenType.L_SQUARE);
                this.tokenStream.advance();

                SimpleType type = languageParser.parseType();

                Expression depth = null;
                if (this.tokenStream.matches(TokenType.COMMA)) {
                    this.tokenStream.advance();

                    depth = parseExpression(scope, type, 0);

                    if (depth instanceof NumberExpression expression) {
                        this.languageParser.expected(TokenType.R_SQUARE);
                        this.tokenStream.advance();

                        return new StackAllocExpression(tokenPosition, type, expression);
                    }
                    languageParser.createSyntaxError(depth.position(), "expected number but got %s", depth.type().name());
                }
                this.languageParser.expected(TokenType.R_SQUARE);
                this.tokenStream.advance();
                return new StackAllocExpression(tokenPosition, type,
                        new NumberExpression(tokenPosition, 1, PrimitiveTypes.I32.toType(), true));
            }
            // {expr, expr, expr, ...}
            if (tokenStream.matches(TokenType.L_CURLY)) {
                this.tokenStream.advance();

                List<Expression> expressions = new ArrayList<>();

                while (!tokenStream.matches(TokenType.R_CURLY)) {
                    Expression element = this.parseExpression(scope, simpleType, 0);
                    expressions.add(element);

                    if (this.tokenStream.matches(TokenType.R_CURLY))
                        continue;

                    if (!this.languageParser.expected(TokenType.COMMA)) {
                        return null;
                    }
                    tokenStream.advance();
                }
                TokenPosition position = this.tokenStream.currentPosition();
                this.languageParser.expected(TokenType.R_CURLY);
                this.tokenStream.advance();

                return new ArrayInitExpression(expressions, simpleType, new NumberExpression(
                        position, expressions.size(), PrimitiveTypes.I32.toType(), true), this.tokenStream.currentPosition());
            }

            if (Arrays.stream(valueContainingTypes)
                    .anyMatch(type -> type.equals(this.tokenStream.current().getType()))) {
                return parseTypeExpression(scope, simpleType);
            }
            if (tokenStream.matches(TokenType.MATCH)) {
                return this.parseMatchExpression(scope, simpleType);
            }
            if (tokenStream.matches(TokenType.L_PAREN)) {
                this.tokenStream.advance();
                Expression expression = parseExpression(scope, simpleType, Operator.MAX_PRIORITY);
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

            leftAssociated = new UnaryExpression(operator, this.parseExpression(scope, simpleType, priority),
                    this.tokenStream.currentPosition());
        }
        if (leftAssociated == null) {
            leftAssociated = this.parseExpression(scope, simpleType, priority - 1);
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

                leftAssociated = new UnaryExpression(operator, leftAssociated,
                        this.tokenStream.currentPosition());
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
                        parseExpression(scope, simpleType, priority - 1) : parseExpression(scope, simpleType, priority);

                leftAssociated = new BinaryExpression(operator, leftAssociated, right,
                        this.tokenStream.currentPosition());
            }
        }


        return leftAssociated;
    }

    private Expression parseTypeExpression(Scope scope, SimpleType simpleType) {
        if (Arrays.stream(numberContainingTypes)
                .anyMatch(type -> type.equals(tokenStream.current().getType()))) {

            String tokenValue = this.tokenStream.current().getValue();
            Number value = 0; // default init 0
            boolean signed = true;

            if (this.tokenStream.matches(TokenType.HEX_NUM)) {
                value = Long.parseUnsignedLong(tokenValue.substring(2), 16);
            } else if (this.tokenStream.matches(TokenType.BIG_HEX_NUM)) {
                // todo
            } else {
                if (tokenValue.charAt(tokenValue.length() - 1) == 'u' ||
                        tokenValue.charAt(tokenValue.length() - 1) == 'U') {
                    signed = false;

                    tokenValue = tokenValue.substring(0, tokenValue.length() - 1);
                }

                if (this.tokenStream.matches(TokenType.BIG_NUMBER)) {
                    value = signed ? new I128(tokenValue) : new U128(tokenValue);
                } else {
                    value = Double.parseDouble(tokenValue);
                }

            }

            PrimitiveTypes type = switch (this.tokenStream.current().getType()) {
                case INT, HEX_NUM -> signed ? PrimitiveTypes.I32 : PrimitiveTypes.U32;
                case DOUBLE, LONG -> signed ? PrimitiveTypes.I64 : PrimitiveTypes.U64;
                case FLOAT -> PrimitiveTypes.F32;
                case SHORT -> signed ? PrimitiveTypes.I16 : PrimitiveTypes.U16;
                case BYTE -> signed ? PrimitiveTypes.I8 : PrimitiveTypes.U8;
                case BIG_NUMBER, BIG_HEX_NUM -> signed ? PrimitiveTypes.I128 : PrimitiveTypes.U128;
                default -> throw new IllegalArgumentException("Expected Number-Type but got '%s'"
                        .formatted(this.tokenStream.current().getType()));
            };

            NumberExpression numberExpression = new NumberExpression(
                    this.tokenStream.currentPosition(), value, type.toType(), signed);

            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.CHAR)) {
            String singletonChar = this.tokenStream.current().getValue()
                    .substring(1, tokenStream.current().getValue().length() - 1);

            int value = singletonChar.charAt(0);

            NumberExpression numberExpression = new NumberExpression(
                    this.tokenStream.currentPosition(), value, PrimitiveTypes.U8.toType(), false);

            this.tokenStream.advance();
            return numberExpression;
        }
        if (tokenStream.matches(TokenType.STRING)) {
            String singletonString = this.tokenStream.current().getValue()
                    .substring(1, tokenStream.current().getValue().length() - 1);

            StringExpression stringExpression = new StringExpression(
                    this.tokenStream.currentPosition(), singletonString);

            this.tokenStream.advance();
            return stringExpression;
        }
        if (tokenStream.matches(TokenType.BOOLEAN)) {
            BooleanExpression expression = new BooleanExpression(this.tokenStream.currentPosition(),
                    tokenStream.current().getValue().equals("true"));

            this.tokenStream.advance();
            return expression;
        }
        if (tokenStream.matches(TokenType.LITERAL)) {
            String value = this.tokenStream.current().getValue();
            TokenPosition namePosition = this.tokenStream.currentPosition();
            StringBuilder path = new StringBuilder(value);
            this.tokenStream.advance();

            if (this.tokenStream.matches(TokenType.DOT)) {
                this.tokenStream.advance();
                path.append("/");

                while (tokenStream.matches(TokenType.LITERAL) || tokenStream.matches(TokenType.DOT)) {
                    if (!this.languageParser.expected(TokenType.LITERAL))
                        return null;

                    path.append(this.tokenStream.current().getValue());
                    this.tokenStream.advance();

                    if (tokenStream.matches(TokenType.DOT))
                        this.tokenStream.advance();

                    path.append("/");
                }
            }
            // function call!
            if (this.tokenStream.matches(TokenType.L_PAREN)) {
                this.tokenStream.advance();

                List<Expression> parameters = new ArrayList<>();
                while (!this.tokenStream.matches(TokenType.R_PAREN)) {
                    Expression expression = this.parseExpression(scope, simpleType, Operator.MAX_PRIORITY);

                    if (expression != null)
                        parameters.add(expression);

                    if (this.tokenStream.matches(TokenType.COMMA))
                        this.tokenStream.advance();
                }
                this.languageParser.expected(TokenType.R_PAREN);
                this.tokenStream.advance();

                if (this.tokenStream.matches(TokenType.SEMICOLON))
                    this.tokenStream.advance();

                return new CallExpression(path.toString(), parameters, namePosition);
            }

            Reference reference = scope.findReference(scope,path.toString());
            if (reference == null)
                languageParser.createSyntaxError(namePosition, "reference is null for literal");
            return new LiteralExpression(reference, path.toString(), namePosition);
        }

        this.languageParser.createSyntaxError(
                "invalid token for expression parsing: '%s'",
                this.tokenStream.current().getType());
        return null;
    }

    private MatchExpression parseMatchExpression(Scope scope, SimpleType simpleType) {
        this.tokenStream.advance();

        Token start = this.tokenStream.current();

        if (!languageParser.expected(TokenType.L_PAREN))
            return null;
        this.tokenStream.advance();

        Expression expression = parseExpression(scope, simpleType, Operator.MAX_PRIORITY);

        if (!languageParser.expected(TokenType.R_PAREN))
            return null;
        this.tokenStream.advance();

        if (!languageParser.expected(TokenType.L_CURLY))
            return null;
        this.tokenStream.advance();

        List<MatchExpression.CaseElement> caseElements = new ArrayList<>();

        while (!this.tokenStream.matches(TokenType.R_CURLY)) {
            if (this.tokenStream.matches(TokenType.DEFAULT) ||
                    this.tokenStream.matches(TokenType.CASE)) {
                List<Expression> conditions = new ArrayList<>();
                boolean defaultState = false;

                if (this.tokenStream.matches(TokenType.CASE)) {
                    this.tokenStream.advance();

                    while (!this.tokenStream.matches(TokenType.LAMBDA) &&
                            !this.tokenStream.matches(TokenType.COLON)) {
                        conditions.add(parseExpression(scope, simpleType, Operator.MAX_PRIORITY));

                        if (!this.tokenStream.matches(TokenType.LAMBDA) &&
                                !this.tokenStream.matches(TokenType.COLON)) {
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
                if (this.tokenStream.matches(TokenType.LAMBDA)) {
                    this.tokenStream.advance();

                    body = parseExpression(scope, simpleType, Operator.MAX_PRIORITY);
                }

                if (this.tokenStream.matches(TokenType.SEMICOLON)) {
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


        return new MatchExpression(expression, caseElements.toArray(new MatchExpression.CaseElement[0]),
                this.tokenStream.currentPosition());
    }


}
