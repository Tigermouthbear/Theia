package dev.tigr.theia

/**
 * @author Tigermouthbear
 * 4/13/20
 */
class Possible(val severity: Severity, val description: String, val clazz: String) {
	enum class Severity { WARN, ALERT, CHECK }
}