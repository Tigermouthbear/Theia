package me.tigermouthbear.theia

import me.tigermouthbear.theia.checks.*
import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 */

object Theia {
	fun run(file: File, path: String): String {
		val program = Program(file)
		val checks: Array<AbstractCheck> = arrayOf(ConnectionCheck(), URLCheck(), CommandCheck(), FileDeletionCheck(), CoordCheck())
		val out = StringBuilder()

		// run checks
		checks.forEach { check -> check.run(program, path) }

		// print checks
		checks.forEach { check ->
			if(check.possibles.size > 0) {
				out.append(check.name + ": {\n")
				check.possibles.forEach { possible ->
					out.append("\t" + possible.severity.name + ": " + possible.description + " in " + possible.clazz + "\n")
				}
				out.append("}\n")
			} else {
				out.append(check.name + ": CLEAR\n")
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
		if(overviewMap.keys.isNotEmpty()) {
			out.append("\nOverview:\n")
			overviewMap.keys.forEach { clazz ->
				val o: StringBuilder = StringBuilder("\t$clazz: ")
				overviewMap[clazz]!!.forEach { check -> o.append(check.name + " ") }
				out.append("$o\n")
			}
		} else {
			out.append("Program all clear!\n")
		}

		return out.toString()
	}
}
