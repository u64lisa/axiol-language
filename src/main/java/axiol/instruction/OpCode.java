package axiol.instruction;

public enum OpCode {
    // operation
    MOVE,                       // v1 = v2
    ADD,                        // v1 = v2 +  v1
    SUB,                        // v1 = v2 -  v1
    AND,                        // v1 = v2 &  v1
    XOR,                        // v1 = v2 ^  v1
    OR,                         // v1 = v2 |  v1
    SHIFT_RIGHT,                // v1 = v2 >> v1
    SHIFT_LEFT,                 // v1 = v2 << v1
    EQUALS,                     // v1 = v2 == v1
    NEGATED_EQUALS,             // v1 = v2 != v1
    MULTIPLY,                   // v1 = v2 *  v1
    MODULO,                     // v1 = v2 %  v1
    DIVIDE,                     // v1 = v2 /  v1

    GREATER_THAN,               // v1 = v2 >  v1
    GREATER_THAN_EQUAL,         // v1 = v2 >= v1

    LESS_THAN,                  // v1 = v2 <  v1
    LESS_THAN_EQUAL,            // v1 = v2 <= v1

    // unary
    NEGATE,                     // v1 = !v2
    SUBSTR,                     // v1 = -v2
    NEGATE_OR,                  // v1 = ~v2

    // memory
    LOAD,                       // v1 = [REG1] v2
    STORE,                      // [REG1] v1 = v2

    // branch
    RETURN,                     // return
    GOTO,                       // goto LABEL
    LABEL,                      // label
    JUMP_IF,                    // v1 == r2 ? goto LABEL
    JUMP_IF_NOT_EQ              // v1 != r2 ? goto LABEL


}