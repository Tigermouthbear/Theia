package dev.tigr.theia

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.jar.JarFile

/**
 * @author Tigermouthbear
 * 4/10/20
 */

class Program(file: File) {
	private val files: MutableMap<String, ByteArray> = HashMap()
	private val classNodes: MutableMap<String, ClassNode> = HashMap()

	init {
		val jar = JarFile(file)
		val entries = jar.entries()
		while(entries.hasMoreElements()) {
			val entry = entries.nextElement()
			try {
				jar.getInputStream(entry).use { `in` ->
					val bytes: ByteArray
					val baos = ByteArrayOutputStream()
					val buf = ByteArray(256)
					var n: Int
					while(`in`.read(buf).also { n = it } != -1) {
						baos.write(buf, 0, n)
					}
					bytes = baos.toByteArray()

					if(!entry.name.endsWith(".class")) {
						files[entry.name] = bytes
					} else {
						val c = ClassNode()
						try {
							ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES)
							classNodes.put(c.name, c)
						} catch(ignored: Exception) {}

					}

				}
			} catch(e: IOException) {
				e.printStackTrace()
			}
		}
	}

	fun getFiles(): MutableMap<String, ByteArray> {
		return files
	}

	fun getClassNodes(): MutableMap<String, ClassNode> {
		return classNodes
	}
}
