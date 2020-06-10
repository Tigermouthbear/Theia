package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import me.tigermouthbear.theia.Theia
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author GiantNuker
 * 6/9/2020
 */
class ClassloadCheck : AbstractCheck("ClassloadCheck", "Dynamically loads a class (can bypass other checks)") {
    override fun run(program: Program) {
        var lastLdc: LdcInsnNode? = null
        for (cn in program.getClassNodes().values) {
            if (Theia.isExcluded(cn.name)) continue
            for (mn in cn.methods) {
                for (insn in mn.instructions) {
                    if (insn is LdcInsnNode) {
                        lastLdc = insn
                    } else if (insn is MethodInsnNode) {
                        if (format(insn) == "java/lang/Class:getDeclaredField:(Ljava/lang/String;)Ljava/lang/reflect/Field;") {
                            if ((lastLdc as LdcInsnNode).cst is String) {
                                if (lastLdc.cst == "resourceCache") {
                                    possibles.add(
                                        Possible(
                                            Possible.Severity.WARN,
                                            "Accessed resource cache (dynamic resource/classloading)",
                                            cn.name
                                        )
                                    )
                                }
                            }
                        } else if (format(insn) == "sun/misc/Unsafe:defineClass:(Ljava/lang/String;[BIILjava/lang/ClassLoader;Ljava/security/ProtectionDomain;)Ljava/lang/Class;") {
                            possibles.add(
                                Possible(
                                    Possible.Severity.ALERT,
                                    "Dynamically defined class with Unsafe",
                                    cn.name
                                )
                            )
                        } else if (format(insn).endsWith("defineClass:([BII)Ljava/lang/Class;")) {
                            var lastCn = cn
                            while (lastCn.superName != "java/lang/ClassLoader" && lastCn.superName != "java/lang/Object") {
                                if (program.getClassNodes().get(lastCn.superName) != null) {
                                    lastCn = program.getClassNodes().get(lastCn.superName)!!
                                } else {
                                    break
                                }
                            }
                            if (lastCn.superName == "java/lang/ClassLoader") {
                                possibles.add(
                                    Possible(
                                        Possible.Severity.ALERT,
                                        "Dynamically defined class",
                                        cn.name
                                    )
                                )
                            } else {
                                possibles.add(
                                    Possible(
                                        Possible.Severity.CHECK,
                                        "Might have dynamically defined class",
                                        cn.name
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}