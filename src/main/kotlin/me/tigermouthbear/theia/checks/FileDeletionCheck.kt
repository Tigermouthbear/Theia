package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/15/20
 */

class FileDeletionCheck: AbstractCheck("FileDeletionCheck") {
	private val methods = arrayOf(
		"java/io/File:delete:()Z",
		"java/nio/file/Files:deleteIfExists:(Ljava/nio/file/Path;)Z"
	)

	override fun run(program: Program, path: String): List<Possible> {
		val possibles = arrayListOf<Possible>()

		for(cn in program.getClassNodes().values) {
			if(!cn.name.startsWith(path)) continue
			for(mn in cn.methods) {
				for(insn in mn.instructions) {
					if(insn is MethodInsnNode && methods.contains(format(insn))) {
						possibles.add(
							Possible(
								Possible.Severity.WARN,
								"Found file deletion in " + cn.name
							)
						)
					}
				}
			}
		}

		return possibles
	}
}