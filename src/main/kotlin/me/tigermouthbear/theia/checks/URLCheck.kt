package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.TypeInsnNode

/**
 * @author Tigermouthbear
 * 4/11/20
 */

class URLCheck: AbstractCheck("URLCheck") {
	override fun run(program: Program): List<Possible> {
		val possibles = arrayListOf<Possible>()

		for(cn in program.getClassNodes().values) {
			for(mn in cn.methods) {
				for(insn in mn.instructions) {
					if(insn is TypeInsnNode) {
						if(insn.desc == "java/net/URL") possibles.add(
							Possible(
								"WARN",
								"Found URL: " + getURL(insn, program) + " in " + cn.name
							)
						)
					}
				}
			}
		}

		return possibles
	}

	//TODO: Make URL parsing better
	private fun getURL(target: TypeInsnNode, program: Program): String {
		var targetNum = -1
		var num = 0
		for(cn in program.getClassNodes().values) {
			for(mn in cn.methods) {
				for(insn in mn.instructions) {
					num++
					if(target == insn) targetNum = num + 2
					if(num == targetNum && insn is LdcInsnNode) return insn.cst as String
				}
			}
		}

		return "could not parse URL"
	}
}