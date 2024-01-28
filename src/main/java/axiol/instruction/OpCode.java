package axiol.instruction;

public enum OpCode {

    // signed
    SIGNED_GREATER_THAN,              // v1 = v2 >  v1
    SIGNED_GREATER_THAN_EQUAL,        // v1 = v2 >= v1
    SIGNED_LESS_THAN,                 // v1 = v2 <  v1
    SIGNED_LESS_THAN_EQUAL,           // v1 = v2 <= v1

    // signed operators
    SIGNED_DIVIDE,                    // v1 = v2 *  v1
    SIGNED_MULTIPLY,                  // v1 = v2 %  v1
    SIGNED_MODULO,                    // v1 = v2 /  v1

    // unsigned
    UNSIGNED_GREATER_THAN,            // v1 = v2 >  v1
    UNSIGNED_GREATER_THAN_EQUAL,      // v1 = v2 >= v1
    UNSIGNED_LESS_THAN,               // v1 = v2 <  v1
    UNSIGNED_LESS_THAN_EQUAL,         // v1 = v2 <= v1

    // unsigned operators
    UNSIGNED_MULTIPLY,                // v1 = v2 *  v1
    UNSIGNED_MODULO,                  // v1 = v2 %  v1
    UNSIGNED_DIVIDE,                  // v1 = v2 /  v1

    // operation
    MOVE,                             // v1 = v2
    ADD,                              // v1 = v2 +  v1
    SUB,                              // v1 = v2 -  v1
    AND,                              // v3 = v2 &  v1
    XOR,                              // v3 = v2 ^  v1
    BIT_OR,                           // v3 = v2 |  v1
    OR,                               // v3 = v2 || v1
    XOR_EQUAL,                        // v2 = v2 ^  v1
    SHIFT_RIGHT,                      // v1 = v2 >> v1
    SHIFT_LEFT,                       // v1 = v2 << v1
    EQUALS,                           // v1 = v2 == v1
    NEGATED_EQUALS,                   // v1 = v2 != v1

    // floating operators
    FLOATING_ADD,
    FLOATING_SUB,
    FLOATING_MULTIPLY,
    FLOATING_DIVIDE,
    FLOATING_MODULO,

    // floating equal
    FLOATING_EQUALS,
    FLOATING_NEGATED_EQUALS,

    // floating comp.
    FLOATING_GREATER_THAN_EQUAL,
    FLOATING_GREATER_THAN,
    FLOATING_LESS_THAN_EQUAL,
    FLOATING_LESS_THAN,

    // size
    SIGN_EXTEND,
    ZERO_EXTEND,
    TRUNCATE,
    BIG_ZERO_EXTEND,
    BIG_TRUNCATE,
    FLOATING_EXTEND,

    // unary
    NEGATE,                     // v1 = !v2
    SUBSTR,                     // v1 = -v2
    NEGATE_OR,                  // v1 = ~v2

    // memory
    LOAD,                       // v1 = [REG1] v2
    STORE,                      // [REG1] v1 = v2
    ALLOC,

    // modifying
    INSTRUCTION_MODIFY,
    INLINE_ASSEMBLY,

    // branch
    RETURN,                     // return
    GOTO,                       // goto LABEL
    LABEL,                      // label
    GOTO_IF,                    // v1 == r2 ? goto LABEL
    GOTO_IF_NOT_EQ,             // v1 != r2 ? goto LABEL
    CALL,
    TERNARY,                    // v1 == (boolExpr) true : false
    ;


}
