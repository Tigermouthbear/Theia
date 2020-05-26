package me.tigermouthbear.theia.checks

import me.tigermouthbear.theia.Possible
import me.tigermouthbear.theia.Program
import me.tigermouthbear.theia.Theia
import org.objectweb.asm.tree.FieldInsnNode

/**
 * @author Crystallinqq
 * 4/14/20
 * 
 * Updated by dominikaaaa on 26/05/20
 */
class CoordCheck: AbstractCheck("CoordCheck") {
    private val coordnames = arrayOf(
        "field_148990_b", // 1.8.9 posX
        "field_70165_t", // 1.8.9 posX
        "field_75646_b", // 1.8.9 posX
        "field_75656_e", // 1.8.9 posX
        "field_148988_d", // 1.8.9 posZ
        "field_70161_v", // 1.8.9 posZ
        "field_75644_d", // 1.8.9 posZ
        "field_75654_g", // 1.8.9 posZ
        "field_70165_t", // 1.12.2 posX
        "field_70161_v", // 1.12.2 posZ
        "field_148990_b", // 1.13.2 posX
        "field_148988_d", // 1.13.2 posZ
        "field_148990_b", // 1.14.4 posX
        "field_148988_d", // 1.14.4 posZ
        "field_148990_b", // 1.15 posX
        "field_148988_d" // 1.15 posZ
    )

    override fun run(program: Program) {
        for(cn in program.getClassNodes().values) {
            if(Theia.isExcluded(cn.name)) continue
            for(mn in cn.methods) {
                for(insn in mn.instructions) {
                    if(insn is FieldInsnNode && coordnames.contains(insn.name)) {
                        possibles.add(
                            Possible(
                                Possible.Severity.WARN,
                                "X or Z coordinates grabbed(1.12.2)",
                                cn.name
                            )
                        )
                    }
                }
            }
        }
    }
}
