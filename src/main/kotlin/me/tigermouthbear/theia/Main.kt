package me.tigermouthbear.theia

import me.tigermouthbear.theia.gui.GUI
import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 */

fun main(args: Array<String>) {
	if(args.size > 2) {
		//TODO make help text
		println("(HELP TEXT)")
		return
	}

	println(
		"\n" +
				"___________.__           .__        \n" +
				"\\__    ___/|  |__   ____ |__|____   \n" +
				"  |    |   |  |  \\_/ __ \\|  \\__  \\  \n" +
				"  |    |   |   Y  \\  ___/|  |/ __ \\_\n" +
				"  |____|   |___|  /\\___  >__(____  /\n" +
				"                \\/     \\/        \\/ \n"
	)
	println("Created by Tigermouthbear\n")

	when(args.size) {
		0 -> GUI.open()
		1 -> print(Theia.run(File(args[0]), ""))
		2 -> print(Theia.run(File(args[0]), args[1]))
	}
}