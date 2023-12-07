## _*axiol-language*_

- axiol-language is a small hobby language without any dependencies compiling
  to assembly and other different targets. the following readme shows further descriptions
  and documentations to features and general elements of the language.

#### The following symbols show the current state of a feature, syntax or general element:

(✅) This feature is fully implemented and working to current date   
(🚧) This feature is halfway implemented or still in progress   
(❌) This feature is NOT implemented and is still in planing

### Diagram

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

### Compiler-Targets

| Architecture                              | Supported Sizes | Status |
|-------------------------------------------|-----------------|--------|
| ARM                                       |                 | ❌      |
| THUMB<br> THUMB_EB                        |                 | ❌      |
| AARCH64<br> AARCH64_32<br> AARCH64_BE     |                 | ❌      |
| X86<br> X86_64                            |                 | ❌      |
| RISCV64<br> RISCV32                       |                 | ❌      |
| MIPS<br> MIPS_EL<br> MIPS64<br> MIPS64_EL |                 | ❌      |
| PPC32<br> PPC32_LE<br> PPC64<br> PPC64_LE |                 | ❌      |
| SPARC64                                   |                 | ❌      |
| AVR                                       |                 | ❌      |
| IR                                        |                 | ❌      |

#### Documentation 
