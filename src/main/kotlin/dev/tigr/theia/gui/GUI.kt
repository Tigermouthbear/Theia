package dev.tigr.theia.gui

import dev.tigr.theia.Theia
import li.flor.nativejfilechooser.NativeJFileChooser
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Image
import java.awt.Rectangle
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.swing.*
import javax.swing.table.DefaultTableModel

/**
 * @author GiantNuker
 * 6/10/2020
 */
object GUI: JFrame("Theia") {
    private val exclusions = mutableListOf(
        "org/reflections/",
        "javassist/",
        "com/sun/jna/",
        "org/spongepowered/",
        "net/jodah/typetools"
    )
    lateinit var fileIndicator: JLabel
    lateinit var fileSelectButton: JButton
    lateinit var runButton: JButton
    lateinit var tabs: JTabbedPane
    lateinit var logPanel: JTextArea
    lateinit var oldOutputPanel: JTextArea
    lateinit var tableOutputPanel: JPanel
    lateinit var excludeLibraries: JCheckBox
    lateinit var exclusionsBox: JTextArea
    lateinit var file: File
    var runOnce = false
    private val cachedExec: Executor = Executors.newCachedThreadPool()
    fun addElements() {
        layout = BorderLayout()

        val header = JPanel()
        header.layout = BoxLayout(header, BoxLayout.X_AXIS)

        val label = JLabel(
            ImageIcon(
                ImageIcon(javaClass.classLoader.getResource("theia.png")).image.getScaledInstance(
                    500,
                    250,
                    Image.SCALE_DEFAULT
                )
            )
        )
        header.add(label)

        val fileBox = JPanel()
        fileBox.layout = BoxLayout(fileBox, BoxLayout.Y_AXIS)

        fileIndicator = JLabel("File: NONE")
        fileBox.add(fileIndicator)

        fileSelectButton = JButton("Select File")
        fileSelectButton.addActionListener { e ->
            val fileChooser = NativeJFileChooser()
            if(fileChooser.showOpenDialog(e.source as Component?) == NativeJFileChooser.APPROVE_OPTION) {
                file = fileChooser.selectedFile
                fileIndicator.text = "File: ${file.name}"
                runButton.isEnabled = true
            }
        }
        fileBox.add(fileSelectButton)

        runButton = JButton("Run Theia")
        runButton.isEnabled = false
        runButton.addActionListener {
            if(runButton.text != "Running...") {
                if(GUI::file.isInitialized) {
                    // clear previous possibles
                    Theia.checks.forEach { it.possibles.clear() }

                    runButton.text = "Running..."
                    runButton.isEnabled = false
                    cachedExec.execute {
                        oldOutputPanel.text = "Running..."
                        tableOutputPanel.removeAll()
                        val panel = JPanel()
                        panel.layout = BorderLayout()
                        panel.add(JLabel("Running..."), BorderLayout.WEST)
                        tableOutputPanel.add(panel)
                        tabs.selectedIndex = 0
                        exclusions.clear()
                        exclusionsBox.text.split("\n").filter { !it.isBlank() }.forEach { exclusions.add(it) }
                        Theia.run(file, if(excludeLibraries.isSelected) exclusions else listOf())
                        runButton.isEnabled = true
                        runButton.text = "Run Theia"
                        finish()
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        null,
                        "Please select a file to run with!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
        fileBox.add(runButton)
        excludeLibraries = JCheckBox("Exclude Libraries")
        excludeLibraries.isSelected = true
        fileBox.add(excludeLibraries)

        exclusionsBox = JTextArea()
        for(defaultExclusion in exclusions) {
            exclusionsBox.text += "$defaultExclusion\n"
        }
        fileBox.add(scrollPanel(exclusionsBox))

        header.add(fileBox)
        add(header, BorderLayout.NORTH)

        logPanel = JTextArea()
        logPanel.isEditable = false
        oldOutputPanel = JTextArea()
        oldOutputPanel.isEditable = false
        tableOutputPanel = JPanel()
        tableOutputPanel.layout = BoxLayout(tableOutputPanel, BoxLayout.Y_AXIS)

        tabs = JTabbedPane()
        tabs.add(
            "Log",
            scrollPanel(logPanel)
        )
        add(tabs, BorderLayout.CENTER)
    }

    fun scrollPanel(component: Component): JScrollPane {
        val scrollPane = JScrollPane(component)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPane.autoscrolls = true
        return scrollPane
    }

    fun finish() {
        if(!runOnce) {
            runOnce = true
            tabs.add(
                "Table",
                scrollPanel(tableOutputPanel)
            )
            tabs.add(
                "Text",
                scrollPanel(oldOutputPanel)
            )
        }
        finishTypeTable()
        oldOutputPanel.scrollRectToVisible(Rectangle(0, 0))
        oldOutputPanel.text = Theia.log
        tabs.selectedIndex = 1
    }

    fun finishTypeTable() {
        tableOutputPanel.removeAll()
        val widthArray = arrayOf(0, 0, 0)
        val tableModels = arrayOfNulls<DefaultTableModel>(Theia.checks.size)
        for((index, check) in Theia.checks.withIndex()) {
            val model = DefaultTableModel(0, 3)
            for(possible in check.possibles) {
                model.addRow(arrayOf(possible.severity.name, possible.clazz, possible.description))
                if(JLabel(possible.severity.name).preferredSize.width > widthArray[0]) widthArray[0] =
                    JLabel(possible.severity.name).preferredSize.width
                if(JLabel(possible.clazz).preferredSize.width > widthArray[1]) widthArray[1] =
                    JLabel(possible.clazz).preferredSize.width
                if(JLabel(possible.description).preferredSize.width > widthArray[2]) widthArray[2] =
                    JLabel(possible.description).preferredSize.width
            }
            tableModels[index] = model
        }
        for(i in 0..Theia.checks.lastIndex) {
            val model = tableModels[i]!!
            val table = object: JTable(model) {
                override fun isCellEditable(row: Int, column: Int) = false
            }
            var j = 0
            for(column in table.columnModel.columns) {
                column.minWidth = widthArray[j] + 20
                j++
            }
            val panel = JPanel()
            panel.layout = BorderLayout()
            panel.add(JLabel("${Theia.checks[i].name} - ${Theia.checks[i].desc}"), BorderLayout.WEST)
            tableOutputPanel.add(panel)
            if(Theia.checks[i].possibles.isEmpty()) {
                panel.add(JLabel(" - Passed check"), BorderLayout.SOUTH)
            } else {
                tableOutputPanel.add(table)
            }
        }
    }

    fun open() {
        addElements()

        iconImage = ImageIcon(javaClass.classLoader.getResource("icon.png")).image
        defaultCloseOperation = EXIT_ON_CLOSE
        //GUI.pack() // pack elements
        setSize(850, 700)
        setLocationRelativeTo(null)
        isVisible = true // set visible
    }

    var lastLogHeight = 0
    fun log(text: String) {
        logPanel.text =
            if(text.startsWith("\r")) logPanel.text.substringBeforeLast('\n') + '\n' + text else logPanel.text + '\n' + text
        if(logPanel.height > lastLogHeight && logPanel.visibleRect.y + logPanel.visibleRect.height > lastLogHeight - 80) {
            logPanel.scrollRectToVisible(Rectangle(0, logPanel.height, 0, 0))
        }
        lastLogHeight = logPanel.height
    }
}