package me.tigermouthbear.theia.gui

import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

/**
 * @author Tigermouthbear
 * 4/27/20
 *
 * Updated by GiantNuker on 6/9/2020
 */

class ResultsFrame: JFrame("Theia Results") {
	fun open(output: String) {
		// set look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

		val textArea = JTextArea(output)

		// set font size
		val font: Font = textArea.font
		val size = font.size + 5.0f
		textArea.font = font.deriveFont(size)

		//set scroll plane
		val scrollPane = JScrollPane(textArea)
		scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
		scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED

		add(scrollPane, BorderLayout.CENTER)

		pack() // pack elements
		setSize(1200, 800)
		isVisible = true // set visible

		// reset run button
		GUI.runButton.text = "Run"
	}
}