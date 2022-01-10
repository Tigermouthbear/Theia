package dev.tigr.theia.core.checks

import dev.tigr.theia.core.Possible
import dev.tigr.theia.core.Program
import dev.tigr.theia.core.Theia
import org.objectweb.asm.tree.MethodInsnNode

/**
 * @author Tigermouthbear
 * 4/15/20
 *
 * Updated by GiantNuker 6/10/2020
 * Updated by perry 1/4/2022
 */
object FileDeletionCheck: AbstractCheck("FileDeletionCheck", "File deleted") {
    private val methods = arrayOf(
        "java/io/File:delete:()Z",
        "java/io/File:deleteOnExit:()V",
        "java/nio/file/Files:deleteIfExists:(Ljava/nio/file/Path;)Z",
        "java/nio/file/Files:delete:(Ljava/nio/file/Path;)V",
        "org/apache/commons/io/FileUtils:forceDelete:(Ljava/io/File;)V",
        "org/apache/commons/io/FileUtils:forceDeleteOnExit:(Ljava/io/File;)V",
        "org/apache/commons/io/FileUtils:deleteDirectory:(Ljava/io/File;)V",
        "org/apache/commons/io/FileUtils:cleanDirectory:(Ljava/io/File;)V",
        "org/apache/commons/io/FileUtils:deleteQuietly:(Ljava/io/File;)V",
        "org/springframework/util/FileSystemUtils:deleteRecursively:(Ljava/io/File;)Z",
        "org/springframework/util/FileSystemUtils:deleteRecursively:(Ljava/nio/file/Path;)Z"
    )

    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is MethodInsnNode && methods.contains(format(insn))) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN,
                                "Found file deletion",
                                cn.name
                            )
                        )
                    }
                }
            }
        }
    }
}