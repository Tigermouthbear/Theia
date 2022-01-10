package dev.tigr.theia.core.checks

import dev.tigr.theia.core.Possible
import dev.tigr.theia.core.Program
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode

/**
 * @author Tigermouthbear
 * 4/11/20
 *
 * Updated by GiantNuker 6/10/2020
 * Improved speed -Tigermouthbear 6/10/2020
 */
object URLCheck: AbstractCheck("URLCheck", "URL created") {
    var methods: MutableMap<MethodNode, String> = mutableMapOf()

    override fun run(program: Program) {
        val thread = Thread {
            for(mn in methods.keys) {
                for(insn in mn.instructions) {
                    if(insn is TypeInsnNode) {
                        if(insn.desc == "java/net/URL") {
                            possibles.add(
                                Possible(
                                    Possible.Severity.WARN,
                                    "Found URL [" + getURL(
                                        insn,
                                        program
                                    ) + "]",
                                    methods[mn]!!
                                )
                            )
                        }
                    }
                }
            }
        }
        thread.start()
        val startTime = System.currentTimeMillis()
        while(thread.isAlive) {
            if(System.currentTimeMillis() - startTime > 25000) {
                thread.interrupt()
                possibles.add(Possible(Possible.Severity.WARN, "URL CHECK TIMED OUT", "URL CHECK TIMED OUT"))
                return
            }
            Thread.sleep(10)
        }
        methods.clear()
    }

    private fun getURL(target: TypeInsnNode, program: Program): String {
        var out = ""
        var looking = false

        for(cn in program.getClassNodes().values) {
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(target == insn) looking = true
                    if(looking) {
                        if(insn is LdcInsnNode) out += insn.cst.toString() + " : "
                        else if(insn is MethodInsnNode) {
                            if(insn.owner == "java/net/URL" && insn.name == "<init>" && insn.desc == "(Ljava/lang/String;)V") break
                        }
                    }
                }
            }
        }

        if(out.length > 80) return out.substring(0, 80)
        else if(out.isNotEmpty()) return out

        return "could not parse URL"
    }
}