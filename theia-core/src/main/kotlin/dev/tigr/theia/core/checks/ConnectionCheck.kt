package dev.tigr.theia.core.checks

import dev.tigr.theia.core.Possible
import dev.tigr.theia.core.Program
import dev.tigr.theia.core.Theia
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

/**
 * @author Tigermouthbear
 * 4/11/20
 *
 * Updated by GiantNuker 6/10/2020
 */
object ConnectionCheck: AbstractCheck("WebConnectionCheck", "Any outgoing connection") {
    private val types = arrayOf(
        "java/net/HttpURLConnection",
        "java/net/HttpsURLConnection",
        "org/apache/http/impl/client/CloseableHttpClient",
        "okhttp3/Request",
        "java/net/Socket",
        "java/net/InetSocketAddress",
        "org/apache/http/impl/client/HttpClientBuilder",
        "org/apache/http/client/methods/HttpPost"

    )

    private val methods = arrayOf(
        "java/net/URL:openConnection:()Ljava/net/URLConnection;",
        "java/net/URL:openStream:()Ljava/io/InputStream;"
    )

    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is TypeInsnNode && types.contains(insn.desc)) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN,
                                "Found connection",
                                cn.name
                            )
                        )
                        URLCheck.methods[mn] = cn.name
                    } else if(insn is MethodInsnNode && methods.contains(format(insn))) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN,
                                "Found connection",
                                cn.name
                            )
                        )
                        URLCheck.methods[mn] = cn.name
                    }
                }
            }
        }
    }
}