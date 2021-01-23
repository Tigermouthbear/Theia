package dev.tigr.theia.core

/**
 * @author Tigermouthbear
 * 4/13/20
 */
class Possible(val severity: Severity, val description: String, val clazz: String) {
    enum class Severity { SEVERE, WARN, CHECK }
}