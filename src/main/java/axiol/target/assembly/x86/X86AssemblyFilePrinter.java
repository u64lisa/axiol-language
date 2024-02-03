package axiol.target.assembly.x86;

import axiol.target.assembly.AssemblyFilePrinter;

public class X86AssemblyFilePrinter extends AssemblyFilePrinter {
    // BITS 64
    //
    //global _start
    //section .text
    //
    //_start:
    //    call %s
    //    mov rdi, rax
    //    mov rax, 60
    //    syscall
    //
    //%s
    //
    //section .data
    //%s

    public StringBuilder source = new StringBuilder();

    @Override
    public void init(int bitSize) {
        switch (bitSize) {
            case 32, 64 -> source.append("BITS %s".formatted(bitSize))
                    .append(NEXT_LINE.repeat(2));

            default ->
                    throw new IllegalArgumentException("unsupported bit size for assembly file: '%d'".formatted(bitSize));
        }

        source.append("section .text").append(NEXT_LINE);
        source.append("global _start").append(NEXT_LINE);
        source.append(NEXT_LINE);
    }

    @Override
    public void createEntryPoint(String main) {
        source.append("_start:").append(NEXT_LINE);
        source.append(TAB).append("call %s".formatted(main)).append(NEXT_LINE);
        source.append(TAB).append("mov rdi, rax").append(NEXT_LINE);
        source.append(TAB).append("mov rax, 60").append(NEXT_LINE);
        source.append(TAB).append("syscall").append(NEXT_LINE);
        source.append(NEXT_LINE);
    }

    @Override
    public void createCodeSection(String code) {
        source.append(code);
        source.append(NEXT_LINE);
    }

    @Override
    public void createDataSection(String data) {
        source.append("section .data").append(NEXT_LINE);
        source.append(data);
        source.append(NEXT_LINE);
    }

    @Override
    public String print() {
        String sourceRaw = source.toString();
        source = new StringBuilder();

        return sourceRaw;
    }
}
