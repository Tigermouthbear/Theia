package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/10/20
 */

abstract class AbstractCheck(val name: String) {
	abstract fun run(program: Program, path: String): List<Possible>

	protected fun format(insn: MethodInsnNode): String {
		return insn.owner + ":" + insn.name + ":" + insn.desc;
	}
}