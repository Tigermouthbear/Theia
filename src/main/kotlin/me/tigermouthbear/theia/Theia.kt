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
		val checks: Array<AbstractCheck> = arrayOf(ConnectionCheck(), URLCheck(), CommandCheck(), FileDeletionCheck(), CoordCheck())

		// run checks
		checks.forEach { check -> check.run(program, path) }

		// print checks
		checks.forEach { check ->
			if(check.possibles.size > 0) {
				println(check.name + ": {")
				check.possibles.forEach { possible ->
					println("\t" + possible.severity.name + ": " + possible.description + " in " + possible.clazz)
				}
				println("}")
			} else {
				println(check.name + ": CLEAR")
			}
		}

		// generate overview map
		val overviewMap: MutableMap<String, ArrayList<AbstractCheck>> = hashMapOf()
		checks.forEach { check ->
			check.possibles.forEach { possible ->
				if(overviewMap.containsKey(possible.clazz) && !overviewMap[possible.clazz]!!.contains(check)) overviewMap[possible.clazz]!!.add(check)
				else overviewMap[possible.clazz] = arrayListOf(check)
			}
		}

		// print overview map formatted
		println("\nOverview:")
		overviewMap.keys.forEach { clazz ->
			val out: StringBuilder = StringBuilder("\t$clazz: ")
			overviewMap[clazz]!!.forEach { check -> out.append(check.name + " ") }
			println("$out")
		}
	}
}
