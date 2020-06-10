package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/10/20
 *
 * Updated by GiantNuker 6/10/2020
 */

abstract class AbstractCheck(val name: String, val desc: String) {
	val possibles: ArrayList<Possible> = arrayListOf()

	abstract fun run(program: Program)

	protected fun format(insn: MethodInsnNode): String {
		return insn.owner + ":" + insn.name + ":" + insn.desc;
	}
}