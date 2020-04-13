package me.tigermouthbear.theia

/**
 * @author Tigermouthbear
 * 4/13/20
 */

class Possible(val severity: Severity, val description: String) {
	enum class Severity { WARN, ALERT, CHECK }
}