package me.tigermouthbear.theia

import me.tigermouthbear.theia.gui.GUI
import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 *
 * Updated by GiantNuker on 6/9/2020
 */

fun main(args: Array<String>) {
	if(args.size > 2) {
		println("java -jar theia.jar [file] [exclusions]")
		return
	}

	when(args.size) {
		0 -> GUI.open()
		1 -> print(Theia.run(File(args[0]), args[1].split(",")))
		2 -> print(Theia.run(File(args[0]), listOf()))
	}

	log(
				"___________.__           .__        \n" +
				"\\__    ___/|  |__   ____ |__|____   \n" +
				"  |    |   |  |  \\_/ __ \\|  \\__  \\  \n" +
				"  |    |   |   Y  \\  ___/|  |/ __ \\_\n" +
				"  |____|   |___|  /\\___  >__(____  /\n" +
				"                \\/     \\/        \\/ \n"
	)
	log("Created by Tigermouthbear\n")
}