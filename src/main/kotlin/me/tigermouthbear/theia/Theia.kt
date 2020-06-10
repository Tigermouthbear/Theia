package me.tigermouthbear.theia

import me.tigermouthbear.theia.checks.*
import java.io.File
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/**
 * @author Tigermouthbear
 * 4/10/20
 *
 * Updated by GiantNuker 6/9/2020
 */

object Theia {
	private lateinit var exclusions: List<String>

	fun run(file: File, exclusions: List<String>): String {
		val startTime = System.currentTimeMillis()

		this.exclusions = exclusions

		val program = Program(file)
		val checks: Array<AbstractCheck> = arrayOf(ConnectionCheck(), URLCheck(), CommandCheck(), FileDeletionCheck(), CoordCheck(), ClassloadCheck())
		val out = StringBuilder()
		var completionIndex = -1
		var checkName = ""
		var mStartTime = 0L
		var active = false
		thread(start = true) {
			while (true) {
				sleep(10)
				if (active) {
					if (completionIndex == -1) {
						print("\rProcessing: ${file.name}")
					} else if (completionIndex < checks.size) {
						print("\r${completionIndex + 1}/${checks.size} - $checkName [${System.currentTimeMillis() - mStartTime}ms]")
					}
				}
				if (completionIndex == checks.size) {
					break
				}
			}
		}
		println("Processing: ${file.name}")
		// run checks
		checks.forEach { check ->
			completionIndex = checks.indexOf(check)
			checkName = check.name
			print("\r${completionIndex + 1}/${checks.size} - $checkName [0ms]")
			mStartTime = System.currentTimeMillis()
			active = true
			//print("\r${String.format("%.1f", (checks.indexOf(check).toDouble()/checks.size) * 100.0)}% - Running Check: ${check.name}")
			check.run(program)
			active = false
			println("\r${completionIndex + 1}/${checks.size} - $checkName [${System.currentTimeMillis() - mStartTime}ms]")
		}
		completionIndex = checks.size
		println("Done in ${System.currentTimeMillis() - startTime}ms")

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

		out.append("\nTheia completed in " + (System.currentTimeMillis() - startTime) + " milliseconds")

		return out.toString()
	}

	fun isExcluded(className: String): Boolean {
		for(depencency in exclusions) {
			if(className.startsWith(depencency)) return true
		}
		return false
	}
}
