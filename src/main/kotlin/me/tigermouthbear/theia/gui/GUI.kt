package me.tigermouthbear.theia.gui

import me.tigermouthbear.theia.Theia
import java.awt.*
import java.awt.BorderLayout.*
import java.io.File
import javax.swing.*

object GUI: JFrame("Theia") {
	private val defaultExclusions: List<String> = listOf(
		"org/reflections/",
		"javassist/",
		"com/sun/jna/",
		"org/spongepowered/"
		)

	lateinit var file: File
	lateinit var fileLabel: JLabel
	lateinit var checkbox: JCheckBox

	fun open() {
		// set look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

		addElements()

		iconImage = ImageIcon(javaClass.classLoader.getResource("icon.png")).image
		defaultCloseOperation = EXIT_ON_CLOSE
		pack() // pack elements
		setSize(650, 400)
		isVisible = true // set visible
	}

	fun addElements() {
		// create contentPanel
		val contentPanel = JPanel(GridBagLayout())

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
		val c = GridBagConstraints()
		c.fill = GridBagConstraints.HORIZONTAL
		c.weightx = 0.5
		c.gridx = 0
		c.gridy = 0
		contentPanel.add(topPanel, c)

		val midPanel = JPanel()
		checkbox = JCheckBox("Exclude libraries")
		checkbox.isSelected = true
		midPanel.add(checkbox)

		// add midPanel to contentPanel
		c.fill = GridBagConstraints.HORIZONTAL
		c.weightx = 0.5
		c.gridx = 0
		c.gridy = 1
		contentPanel.add(midPanel, c)

		// add run button to botPanel
		val botPanel = JPanel()
		val runButton = JButton("Run")
		runButton.addActionListener {
			if(::file.isInitialized) {
				ResultsFrame.open(Theia.run(file, if(checkbox.isSelected) defaultExclusions else listOf()))
			} else {
				JOptionPane.showMessageDialog(null, "Please select a file to run with!", "Error", JOptionPane.ERROR_MESSAGE)
			}
		}
		botPanel.add(runButton)

		// add botPanel to contentPanel
		c.fill = GridBagConstraints.HORIZONTAL
		c.weightx = 0.5
		c.gridx = 0
		c.gridy = 2
		contentPanel.add(botPanel, c)

		// add contentPanel to frame
		add(contentPanel, CENTER)

		// add logo to frame
		add(JLabel(ImageIcon(ImageIcon(javaClass.classLoader.getResource("theia.png")).image.getScaledInstance(500, 250, Image.SCALE_DEFAULT))), NORTH)
	}
}