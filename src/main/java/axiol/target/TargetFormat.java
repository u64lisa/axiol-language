package axiol.target;

import axiol.target.assembly.aarch.AARCHAssemblyGenerator;
import axiol.target.assembly.arm.ARMAssemblyGenerator;
import axiol.target.assembly.avr.AVRAssemblyGenerator;
import axiol.target.assembly.mips.MIPSAssemblyGenerator;
import axiol.target.assembly.ppc.PPCAssemblyGenerator;
import axiol.target.assembly.riscv.RiscVAssemblyGenerator;
import axiol.target.assembly.sparc.SparcAssemblyGenerator;
import axiol.target.assembly.thumb.ThumbAssemblyGenerator;
import axiol.target.assembly.x86.X86AssemblyGenerator;
import axiol.target.other.ir.IRGenerator;

@SuppressWarnings("all")
public enum TargetFormat {
    // @formatter:off
    ARM         (new ARMAssemblyGenerator()),   // 8, 16, 32, 64

    THUMB       (new ThumbAssemblyGenerator()), // 16, 32
    THUMB_EB    (new ThumbAssemblyGenerator()),

    AARCH64     (new AARCHAssemblyGenerator()), // 8, 16, 32, 64
    AARCH64_32  (new AARCHAssemblyGenerator()),
    AARCH64_BE  (new AARCHAssemblyGenerator()),

    X86         (new X86AssemblyGenerator()),   // 8, 16, 32, 64
    X86_64      (new X86AssemblyGenerator()),

    RISCV64     (new RiscVAssemblyGenerator()), // 32, 64
    RISCV32     (new RiscVAssemblyGenerator()),

    MIPS        (new MIPSAssemblyGenerator()),  // 32, 64
    MIPS_EL     (new MIPSAssemblyGenerator()),
    MIPS64      (new MIPSAssemblyGenerator()),
    MIPS64_EL   (new MIPSAssemblyGenerator()),

    PPC32       (new PPCAssemblyGenerator()),   // 32, 64
    PPC32_LE    (new PPCAssemblyGenerator()),
    PPC64       (new PPCAssemblyGenerator()),
    PPC64_LE    (new PPCAssemblyGenerator()),

    SPARC64     (new SparcAssemblyGenerator()), // 32, 64

    AVR         (new AVRAssemblyGenerator()),   // 8, 16, 32

    // not assembly :c
    IR("%s.ir", new IRGenerator()),

    // spooky
    //
    ;
    // @formatter:on

    public final String outPutFormat;
    public final axiol.target.AssemblyGenerator generatorClass;

    TargetFormat(axiol.target.AssemblyGenerator generatorClass) {
        this.generatorClass = generatorClass;
        outPutFormat = "/%s.asm";
    }

    TargetFormat(String outPutFormat, axiol.target.AssemblyGenerator generatorClass) {
        this.outPutFormat = outPutFormat;
        this.generatorClass = generatorClass;
    }

    public AssemblyGenerator getGeneratorClass() {
        return generatorClass;
    }

    public String getOutPutFormat() {
        return outPutFormat;
    }
}
