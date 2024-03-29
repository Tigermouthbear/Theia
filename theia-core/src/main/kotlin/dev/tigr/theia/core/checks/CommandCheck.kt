package dev.tigr.theia.core.checks

import dev.tigr.theia.core.Possible
import dev.tigr.theia.core.Program
import dev.tigr.theia.core.Theia
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/13/20
 *
 * Updated by GiantNuker 6/10/2020
 */
object CommandCheck: AbstractCheck("CommandCheck", "Executes a system command") {
    private val methods = arrayOf(
        "java/lang/Runtime:exec:(Ljava/lang/String;)Ljava/lang/Process;",
        "java/lang/ProcessBuilder:command:([Ljava/lang/String;)Ljava/lang/ProcessBuilder;"
    )

    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is MethodInsnNode && methods.contains(format(insn))) {
                        possibles.add(
                            Possible(
                                Possible.Severity.SEVERE,
                                "Shell command executed",
                                cn.name
                            )
                        )
                    }
                }
            }
        }
    }
}