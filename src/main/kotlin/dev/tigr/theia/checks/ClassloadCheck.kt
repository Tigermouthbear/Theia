package dev.tigr.theia.checks

import dev.tigr.theia.Possible
import dev.tigr.theia.Program
import dev.tigr.theia.Theia
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import java.util.function.Consumer

/**
 * @author GiantNuker
 * 6/9/2020
 * Updated by Crystallinqq 2 fix nukers easily bypassable resource cache check
 * 8/15/2020
 */
object ClassloadCheck : AbstractCheck("ClassloadCheck", "Dynamically loads a class (can bypass other checks)") {
    override fun run(program: Program) {
        for (cn in program.getClassNodes().values) {
            if (Theia.isExcluded(cn.name)) continue
            cn.methods.forEach(Consumer { methodNode: MethodNode ->
                methodNode.instructions.forEach(Consumer { ain: AbstractInsnNode ->
                    if (ain is MethodInsnNode)  {
                        when(ain.name) {
                            "defineClass" ->
                                possibles.add(
                                    Possible(
                                        Possible.Severity.ALERT, "Dynamically defined class with defineClass.", cn.name
                                    )
                                )
                            "getDeclaredField" ->
                                possibles.add(
                                    Possible(Possible.Severity.WARN, "Accesses field with Reflect API (May be dynamically loading classes/resources)", cn.name
                                    )
                                )
                        }
                    }
                })
                if (cn.superName == "java/lang/ClassLoader") {
                    possibles.add(
                        Possible(
                            Possible.Severity.ALERT, "Dynamically defined class", cn.name
                        )
                    )
                } else {
                    possibles.add(
                        Possible(
                            Possible.Severity.CHECK, "Might have dynamically defined class", cn.name
                        )
                    )
                }
            })
        }
    }
}