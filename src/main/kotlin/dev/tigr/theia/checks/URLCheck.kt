package dev.tigr.theia.checks

import dev.tigr.theia.Possible
import dev.tigr.theia.Program
import dev.tigr.theia.Theia
import org.objectweb.asm.tree.*

/**
 * @author Tigermouthbear
 * 4/11/20
 *
 * Updated by GiantNuker 6/10/2020
 * Improved speed -Tigermouthbear 6/10/2020
 */

object URLCheck: AbstractCheck("URLCheck", "URL created") {
	var classes: MutableList<ClassNode> = mutableListOf()
	var methods: MutableList<MethodNode> = mutableListOf()

	override fun run(program: Program) {
		for(cn in program.getClassNodes().values) {
			if(Theia.isExcluded(cn.name)) continue
			for(mn in cn.methods) {
				if(!methods.contains(mn)) continue
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
									cn.name
								)
							)
						}
					}
				}
			}
		}
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