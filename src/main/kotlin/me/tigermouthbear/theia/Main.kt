package me.tigermouthbear.theia

import java.io.File

/**
 * @author Tigermouthbear
 * 4/10/20
 */

fun main(args: Array<String>) {
	if(args.isEmpty()) {
		println("Please specify input file!")
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

	if(args.size == 1) Theia.run(File(args[0]), "")
	else if(args.size == 2) Theia.run(File(args[0]), args[1])
}