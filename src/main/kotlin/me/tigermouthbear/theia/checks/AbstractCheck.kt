package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program

/**
 * @author Tigermouthbear
 * 4/10/20
 */

abstract class AbstractCheck(val name: String) {
	abstract fun run(program: Program): List<Possible>
}