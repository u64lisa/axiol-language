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
---
title: Language Diagram
---
graph TD;
    classDef red stroke:#f00
    classDef green stroke:#0f0

    source(["Source *.ax"])
    lexer["Lexer (TokenStream)"]
    source:::red --> lexer
    parser["Parser (AST)"]
    lexer --> parser
    analysis["StaticAnalysis"]
    error_stage_1["Analysis Error report"]
    analysis --> error_stage_1
    
    subgraph one LR;
    Analysis --> DuplicationCheck
    DuplicationCheck --> AccessCheck
    AccessCheck --> TypeCheck
    TypeCheck --> DeclareCheck
    DeclareCheck --> ReturnCheck
    
    end
    
    optimising["ASTOptimizer"]
    parser --> analysis
    parser --> optimising    
    instructionGen["Instruction Generation"]
    optimising --> instructionGen
    instructionModification["Instruction Modification
    using the **isa** keyword"]
    instructionGen --> instructionModification
    optimising-- ISAStatement ---instructionModification
    linker["Linking multiple
    instruction sets"]
    instructionModification --> linker
    importingLogic["importing files"]
    importedSources["imported files"]
    linker --> importingLogic
    importingLogic --> importedSources
    importedSources --> lexer;
    compiler["Compiler Instructions
    to target"]
    inlineASM["Generating Inline 
    Assembly"]
    linker --> inlineASM
    linker --> compiler
       finishedCode(["Finished generated
        Code *.asm"])
    compiler --> finishedCode:::green
```

#### Compiler-Targets


#### Documentation 
