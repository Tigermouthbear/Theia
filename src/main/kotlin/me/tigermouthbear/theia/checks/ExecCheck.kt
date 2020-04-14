package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode


/**
 * @author Crystallinqq
 * 4/14/20
 */

class ExecCheck: AbstractCheck("ExecCheck") {
    override fun run(program: Program): List<Possible> {
            val possibles = arrayListOf<Possible>()
            for(cn in program.getClassNodes().values) {
                for(mn in cn.methods) {
                    for(insn in mn.instructions) {
                        if(insn is MethodInsnNode) {
                            if(insn.name == "exec" && insn.owner == "java/lang/Runtime") possibles.add(
                                Possible(
                                    "WARN",
                                    "Class " + cn.name + " possibly executes shell commands! Be careful!"
                                )
                            )
                        }
                    }
                }
            }

            return possibles
        }
    }