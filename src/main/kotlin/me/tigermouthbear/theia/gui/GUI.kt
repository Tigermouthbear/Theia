package me.tigermouthbear.theia.gui

import me.tigermouthbear.theia.Theia
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.Component
import java.awt.Image
import java.io.File
import java.io.InputStream
import javax.swing.*

object GUI: JFrame("Theia") {
	lateinit var file: File
	lateinit var fileLabel: JLabel

	fun open() {
		// set look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

		addElements()

		val index: InputStream = javaClass.classLoader.getResourceAsStream("icon.png")
		val bytes = ByteArray(index.available())
		index.read(bytes)
		iconImage = ImageIcon(bytes).image
		defaultCloseOperation = EXIT_ON_CLOSE
		pack() // pack elements
		setSize(650, 400)
		isVisible = true // set visible
	}

	fun addElements() {
		// create contentPanel
		val contentPanel = JPanel(BorderLayout())

		// add file label to topPanel
		val topPanel = JPanel()
		fileLabel = JLabel("File: NONE")
		topPanel.add(fileLabel)

		// add open button to topPanel
		val fileButton = JButton("Open File")
		fileButton.addActionListener {e ->
			val fileChooser = JFileChooser()
			if (fileChooser.showOpenDialog(e.source as Component?) == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.selectedFile
				fileLabel.text = file.name

			}
		}
		topPanel.add(fileButton)

		//add topPanel to contentPanel
		contentPanel.add(topPanel, CENTER)

		// add run button to botPanel
		val botPanel = JPanel()
		val runButton = JButton("Run")
		runButton.addActionListener {
			if(::file.isInitialized) {
				ResultsFrame.open(Theia.run(file, ""))
			} else {
				JOptionPane.showMessageDialog(null, "Please select a file to run with!", "Error", JOptionPane.ERROR_MESSAGE)
			}
		}
		botPanel.add(runButton)

		// add botPanel to contentPanel
		contentPanel.add(botPanel, SOUTH)

		// add contentPanel to frame
		add(contentPanel, CENTER)

		// add logo to frame
		val index: InputStream = javaClass.classLoader.getResourceAsStream("theia.png")
		val bytes = ByteArray(index.available())
		index.read(bytes)
		add(JLabel(ImageIcon(ImageIcon(bytes).image.getScaledInstance(500, 250, Image.SCALE_DEFAULT))), NORTH)
	}
}