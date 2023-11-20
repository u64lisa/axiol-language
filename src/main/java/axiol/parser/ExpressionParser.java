package axiol.parser;

import axiol.lexer.TokenType;

public class ExpressionParser {

    private final LanguageParser languageParser;

    public ExpressionParser(LanguageParser languageParser) {
        this.languageParser = languageParser;
    }

    public void parserExpression() {
        languageParser.createSyntaxError("error while parsing expression");
    }


    // binary
    // unary
    // num
    // string
    // call

}
