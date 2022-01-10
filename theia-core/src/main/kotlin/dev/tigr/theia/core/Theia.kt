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
 * Updated by Tigermouthbear 1/23/2021
 */
object Theia {
    var logCallback: (String) -> Unit = {}

    private lateinit var exclusions: List<String>
    val checks: Array<AbstractCheck> = arrayOf(
        ConnectionCheck,
        URLCheck,
        CommandCheck,
        FileDeletionCheck,
        CoordCheck,
        ClassloadCheck
    )

    fun run(file: File, exclusions: List<String>): Result {
        val startTime = System.currentTimeMillis()
        this.exclusions = exclusions

        // reset checks possibles
        checks.forEach { it.possibles.clear() }

        // background timer
        val program = dev.tigr.theia.core.Program(file)
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
        log("Processing: ${file.name}\n")

        // run checks
        checks.forEach { check ->
            completionIndex = checks.indexOf(check)
            checkName = check.name
            log("\r${completionIndex + 1}/${checks.size} - $checkName [0ms]")
            mStartTime = System.currentTimeMillis()
            active = true
            check.run(program)
            active = false
            log("\r${completionIndex + 1}/${checks.size} - $checkName [${System.currentTimeMillis() - mStartTime}ms]\n")
        }
        completionIndex = checks.size

        log("Done in ${System.currentTimeMillis() - startTime}ms")

        return Result(checks)
    }

    fun isExcluded(className: String): Boolean {
        for(dependency in exclusions) {
            if(className.startsWith(dependency)) return true
        }
        return false
    }

    private fun log(text: String) {
        if(text.startsWith('\r')) print(text) else print("\n$text")
        logCallback.invoke(text)
    }
}