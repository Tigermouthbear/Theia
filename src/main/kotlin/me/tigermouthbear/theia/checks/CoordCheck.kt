package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import me.tigermouthbear.theia.Theia
import org.objectweb.asm.tree.FieldInsnNode

/**
 * @author Crystallinqq
 * 4/14/20
 */

class CoordCheck: AbstractCheck("CoordCheck") {
    //1.12.2 x and z coordinate names. idk how to check for all versions since obfuscation changes each version :c
    private val coordnames = arrayOf(
        "field_70165_t",
        "field_70161_v"
    )

    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is FieldInsnNode && coordnames.contains(insn.name)) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN,
                                "X or Z coordinates grabbed(1.12.2)",
                                cn.name
                            )
                        )
                    }
                }
            }
        }
    }
}