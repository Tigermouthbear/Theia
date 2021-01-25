package dev.tigr.theia

import dev.tigr.theia.core.Theia
import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 *
 * Updated by GiantNuker on 6/10/2020
 */
fun main(args: Array<String>) {
    if(args.size > 2) {
        println("java -jar theia.jar [file] [exclusions]")
        return
    }

    // contributors: feel free to add your name here
    val credits = "Theia v0.2\n" +
            "Created by Tigermouthbear\n" +
            "With contributions from GiantNuker, Crystalinqq, and Liv\n"

    print(
        "___________.__           .__        \n" +
        "\\__    ___/|  |__   ____ |__|____   \n" +
        "  |    |   |  |  \\_/ __ \\|  \\__  \\  \n" +
        "  |    |   |   Y  \\  ___/|  |/ __ \\_\n" +
        "  |____|   |___|  /\\___  >__(____  /\n" +
        "                \\/     \\/        \\/ \n" +
        credits
    )

    when(args.size) {
        0 -> {
            // enable anti-aliasing
            System.setProperty("awt.useSystemAAFontSettings", "on")
            System.setProperty("swing.aatext", "true")

            GUI.open()
            GUI.log(credits)
            Theia.logCallback = { GUI.log(it) }
        }
        1 -> print(Theia.run(File(args[0]), listOf()).getJSONString())
        2 -> print(Theia.run(File(args[0]), args[1].split(",")).getJSONString())
    }
}
