## _*axiol-language*_

- axiol-language is a small hobby language without any dependencies compiling 
to assembly and other different targets. the following readme  shows further descriptions
and documentations to features and general elements of the language.

#### The following symbols show the current state of a feature, syntax or general element:

(✅) This feature is fully implemented and working to current date   
(🚧) This feature is halfway implemented or still in progress   
(❌) This feature is NOT implemented and is still in planing

#### Diagram

```mermaid
graph LR;
    source["Source *.ax"]
    lexer["Lexer (TokenStream)"]
    source --> lexer
    parser["Parser (AST)"]
    lexer --> parser
    analysis["StaticAnalysis"]
    optimising["ASTOptimizer"]
    parser -> analysis
    parser -> optimising    
```

#### Compiler-Targets


#### Documentation 
