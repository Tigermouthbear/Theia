package me.tigermouthbear.theia

import me.tigermouthbear.theia.checks.*
import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 */

object Theia {
	fun run(file: File, path: String) {
		val program = Program(file)

		val checks: Array<AbstractCheck> = arrayOf(ConnectionCheck(), URLCheck(), CommandCheck(), CoordCheck())

		checks.forEach { check ->
			val possibles = arrayListOf<Possible>()
			check.run(program, path).forEach { possible: Possible ->
				if(possibles.stream().filter { checkingPossible ->
						checkingPossible.description == possible.description
					}.count().toInt() == 0)
					possibles.add(possible)
			}

			if(possibles.size > 0) {
				println(check.name + ": {")
				possibles.forEach { possible ->
					println("	" + possible.severity.name + ": " + possible.description)
				}
				println("}")
			}
			else {
				println(check.name + ": CLEAR")
			}
		}
	}
}
