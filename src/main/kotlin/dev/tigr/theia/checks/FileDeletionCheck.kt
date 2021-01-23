package dev.tigr.theia.checks

import dev.tigr.theia.Possible
import dev.tigr.theia.Program
import dev.tigr.theia.Theia
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/15/20
 *
 * Updated by GiantNuker 6/10/2020
 */
object FileDeletionCheck: AbstractCheck("FileDeletionCheck", "File deleted") {
    private val methods = arrayOf(
        "java/io/File:delete:()Z",
        "java/nio/file/Files:deleteIfExists:(Ljava/nio/file/Path;)Z"
    )

    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is MethodInsnNode && methods.contains(format(insn))) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN,
                                "Found file deletion",
                                cn.name
                            )
                        )
                    }
                }
            }
        }
    }
}