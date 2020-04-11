package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.TypeInsnNode

/**
 * @author Tigermouthbear
 * 4/11/20
 */

class ConnectionCheck: AbstractCheck("UrlConnectionCheck") {
	private val connections = arrayOf("java/net/HttpURLConnection", "java/net/HttpsURLConnection")

	override fun run(program: Program): List<Possible> {
		val possibles = arrayListOf<Possible>()

		for(cn in program.getClassNodes().values) {
			for(mn in cn.methods) {
				for(insn in mn.instructions) {
					if(insn is TypeInsnNode) {
						if(connections.contains(insn.desc)) possibles.add(
							Possible(
								"WARN",
								"Found http connection in " + cn.name
							)
						)
					}
				}
			}
		}

		return possibles
	}
}