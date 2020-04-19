package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/13/20
 */

class CommandCheck: AbstractCheck("CommandCheck") {
	val methods = arrayOf(
		"java/lang/Runtime:exec:(Ljava/lang/String;)Ljava/lang/Process;",
		"java/lang/ProcessBuilder:command:([Ljava/lang/String;)Ljava/lang/ProcessBuilder;"
	)

	override fun run(program: Program, path: String) {
		for(cn in program.getClassNodes().values) {
			if(!cn.name.startsWith(path)) continue
			for(mn in cn.methods) {
				for(insn in mn.instructions) {
					if(insn is MethodInsnNode && methods.contains(format(insn))) {
						possibles.add(
							Possible(
								Possible.Severity.ALERT,
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