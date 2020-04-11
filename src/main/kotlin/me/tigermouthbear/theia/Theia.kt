package me.tigermouthbear.theia

import me.tigermouthbear.theia.checks.AbstractCheck
import me.tigermouthbear.theia.checks.ConnectionCheck
import me.tigermouthbear.theia.checks.URLCheck
import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 */

object Theia {
	fun run(file: File) {
		val program = Program(file)

		val checks: Array<AbstractCheck> = arrayOf(ConnectionCheck(), URLCheck())

		checks.forEach { check ->
			println(check.name + "-> {")
			check.run(program).forEach { possible ->
				println("  " + possible.name + ": " + possible.body)
			}
			println("}")
		}
	}
}