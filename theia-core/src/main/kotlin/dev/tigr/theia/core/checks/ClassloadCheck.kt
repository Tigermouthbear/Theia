package dev.tigr.theia.core.checks

import dev.tigr.theia.core.Possible
import dev.tigr.theia.core.Program
import dev.tigr.theia.core.Theia
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * @author GiantNuker
 * 6/9/2020
 * Updated by Crystallinqq 2 fix nukers easily bypass-able resource cache check
 * 8/15/2020
 */
object ClassloadCheck: AbstractCheck("ClassloadCheck", "Dynamically loads a class (can bypass other checks)") {
    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            cn.methods.forEach { methodNode: MethodNode ->
                methodNode.instructions.forEach { ain: AbstractInsnNode ->
                    if(ain is MethodInsnNode) {
                        when(ain.name) {
                            "defineClass" ->
                                possibles.add(
                                    Possible(
                                        Possible.Severity.SEVERE, "Dynamically defined class with defineClass.", cn.name
                                    )
                                )
                            "getDeclaredField" ->
                                possibles.add(
                                    Possible(
                                        Possible.Severity.WARN,
                                        "Accesses field with Reflect API (May be dynamically loading classes/resources)",
                                        cn.name
                                    )
                                )
                        }
                    }
                }
                if(cn.superName == "java/lang/ClassLoader") {
                    possibles.add(
                        Possible(
                            Possible.Severity.SEVERE, "Dynamically defined class", cn.name
                        )
                    )
                }
            }
        }
    }
}