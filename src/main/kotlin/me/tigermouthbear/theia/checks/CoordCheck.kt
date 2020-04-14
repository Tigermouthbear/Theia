package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.FieldInsnNode

class CoordCheck: AbstractCheck("CoordCheck") {
    //1.12.2 x and z coordinate names. idk how to check for all versions since obfuscation changes each version :c
    val coordnames = arrayOf(
        "field_70165_t",
        "field_70161_v"
    )
    override fun run(program: Program, path: String): List<Possible> {
        val possibles = arrayListOf<Possible>()

        for(cn in program.getClassNodes().values) {
            if(!cn.name.startsWith(path)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is FieldInsnNode && coordnames.contains(insn.name)) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN, "Class " + cn.name + " gets your X or Z coordinates! (1.12.2)"
                            )
                        )
                    }
                }
            }
        }
        return possibles;
    }
}