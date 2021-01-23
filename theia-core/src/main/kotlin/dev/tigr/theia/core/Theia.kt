package dev.tigr.theia.core

import dev.tigr.theia.core.checks.*
import java.io.File
import java.lang.Thread.sleep
import kotlin.concurrent.thread

/**
 * @author Tigermouthbear
 * 4/10/20
 *
 * Updated by GiantNuker 6/10/2020
 */
object Theia {
    private val overviewMap: MutableMap<String, ArrayList<AbstractCheck>> = hashMapOf()
    val checks: Array<AbstractCheck> = arrayOf(
        ConnectionCheck,
        URLCheck,
        CommandCheck,
        FileDeletionCheck,
        CoordCheck,
        ClassloadCheck
    )

    private lateinit var exclusions: List<String>
    var log: String = ""
    var logCallback: (String) -> Unit = {}

    fun run(file: File, exclusions: List<String>) {
        val startTime = System.currentTimeMillis()
        Theia.exclusions = exclusions

        // clear previous possibles
        checks.forEach { it.possibles.clear() }
        overviewMap.clear()

        val program = Program(file)
        val out = StringBuilder()
        var completionIndex = -1
        var checkName = ""
        var mStartTime = 0L
        var active = false
        thread(start = true) {
            while(true) {
                sleep(10)
                if(active) {
                    if(completionIndex == -1) {
                        log("\rProcessing: ${file.name}")
                    } else if(completionIndex < checks.size) {
                        log("\r${completionIndex + 1}/${checks.size} - $checkName [${System.currentTimeMillis() - mStartTime}ms]")
                    }
                }
                if(completionIndex == checks.size) {
                    break
                }
            }
        }
        log("Processing: ${file.name}")
        log("")
        // run checks
        checks.forEach { check ->
            completionIndex = checks.indexOf(check)
            checkName = check.name
            log("\r${completionIndex + 1}/${checks.size} - $checkName [0ms]")
            mStartTime = System.currentTimeMillis()
            active = true
            check.run(program)
            active = false
            log("\r${completionIndex + 1}/${checks.size} - $checkName [${System.currentTimeMillis() - mStartTime}ms]")
            log("")
        }
        completionIndex = checks.size
        log("Done in ${System.currentTimeMillis() - startTime}ms")

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
        checks.forEach { check ->
            check.possibles.forEach { possible ->
                if(overviewMap.containsKey(possible.clazz) && !overviewMap[possible.clazz]!!.contains(check)) overviewMap[possible.clazz]!!.add(
                    check
                )
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
        log = out.toString()
    }

    fun isExcluded(className: String): Boolean {
        for(depencency in exclusions) {
            if(className.startsWith(depencency)) return true
        }
        return false
    }

    fun log(text: String) {
        if(text.startsWith('\r')) print(text) else print("\n$text")
        logCallback.invoke(text)
    }
}