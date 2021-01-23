package dev.tigr.theia

import dev.tigr.theia.core.Result
import dev.tigr.theia.core.Theia
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
 *
 * updated by Tigermouthbear
 * 1/23/21
 */
object GUI: JFrame("Theia") {

    private val cachedExec: Executor = Executors.newCachedThreadPool()
    private val exclusions = mutableListOf(
        "org/reflections/",
        "javassist/",
        "com/sun/jna/",
        "org/spongepowered/",
        "net/jodah/typetools"
    )

    private lateinit var fileIndicator: JLabel
    private lateinit var fileSelectButton: JButton
    private lateinit var runButton: JButton
    private lateinit var tabs: JTabbedPane
    private lateinit var logPanel: JTextArea
    private lateinit var jsonOutputPanel: JTextArea
    private lateinit var textOutputPanel: JTextArea
    private lateinit var tableOutputPanel: JPanel
    private lateinit var excludeLibraries: JCheckBox
    private lateinit var exclusionsBox: JTextArea

    private lateinit var file: File
    private var firstRun = true

    fun open() {
        addElements()

        iconImage = ImageIcon(javaClass.classLoader.getResource("icon.png")).image
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(850, 700)
        setLocationRelativeTo(null)
        isVisible = true // set visible
    }

    private fun addElements() {
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
                    runButton.text = "Running..."
                    runButton.isEnabled = false
                    cachedExec.execute {
                        // display running info
                        jsonOutputPanel.text = "Running..."
                        textOutputPanel.text = "Running..."
                        tableOutputPanel.removeAll()
                        val panel = JPanel()
                        panel.layout = BorderLayout()
                        panel.add(JLabel("Running..."), BorderLayout.WEST)
                        tableOutputPanel.add(panel)
                        tabs.selectedIndex = 0

                        // add exclusions
                        exclusions.clear()
                        exclusionsBox.text.split("\n").filter { !it.isBlank() }.forEach { exclusions.add(it) }

                        // run
                        finish(Theia.run(file, if(excludeLibraries.isSelected) exclusions else listOf()))
                        runButton.isEnabled = true
                        runButton.text = "Run Theia"
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
        jsonOutputPanel = JTextArea()
        jsonOutputPanel.isEditable = false
        textOutputPanel = JTextArea()
        textOutputPanel.isEditable = false
        tableOutputPanel = JPanel()
        tableOutputPanel.layout = BoxLayout(tableOutputPanel, BoxLayout.Y_AXIS)

        tabs = JTabbedPane()
        tabs.add(
            "Log",
            scrollPanel(logPanel)
        )
        add(tabs, BorderLayout.CENTER)
    }

    private fun scrollPanel(component: Component): JScrollPane {
        val scrollPane = JScrollPane(component)
        scrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        scrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        scrollPane.autoscrolls = true
        return scrollPane
    }

    private fun finish(result: Result) {
        if(firstRun) {
            firstRun = false
            tabs.add(
                "Table",
                scrollPanel(tableOutputPanel)
            )
            tabs.add(
                "Text",
                scrollPanel(textOutputPanel)
            )
            tabs.add(
                "JSON",
                scrollPanel(jsonOutputPanel)
            )
        }
        finishTypeTable(result)
        textOutputPanel.scrollRectToVisible(Rectangle(0, 0))
        textOutputPanel.text = result.getFormattedText()
        jsonOutputPanel.scrollRectToVisible(Rectangle(0, 0))
        jsonOutputPanel.text = result.getJSONString()
        tabs.selectedIndex = 1
    }

    private fun finishTypeTable(result: Result) {
        tableOutputPanel.removeAll()

        val widthArray = arrayOf(0, 0, 0)
        val tableModels = arrayOfNulls<DefaultTableModel>(result.getMap().size)

        for((index, entry) in result.getMap().entries.withIndex()) {
            val model = DefaultTableModel(0, 3)
            for(possible in entry.value) {
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

        for(i in 0..result.checks.lastIndex) {
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
            panel.add(JLabel("${result.checks[i].name} - ${result.checks[i].desc}"), BorderLayout.WEST)
            tableOutputPanel.add(panel)

            if(result.checks[i].possibles.isEmpty()) panel.add(JLabel(" - Passed check"), BorderLayout.SOUTH)
            else tableOutputPanel.add(table)
        }
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