package dev.tigr.theia.core

import dev.tigr.theia.core.checks.AbstractCheck
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * @author Tigermouthbear 1/23/21
 */
open class Result(val checks: Array<AbstractCheck>) {
    private val map: MutableMap<AbstractCheck, ArrayList<Possible>> = mutableMapOf()
    private val severityMap: MutableMap<AbstractCheck, MutableMap<Possible.Severity, ArrayList<Possible>>> = hashMapOf()
    private val overviewMap: MutableMap<String, ArrayList<AbstractCheck>> = hashMapOf()
    private var json: JSONObject
    private var text: String

    companion object Empty: Result(arrayOf())

    init {
        // generate overview and map
        checks.forEach { check ->
            // create maps
            map[check] = arrayListOf()
            severityMap[check] = EnumMap(Possible.Severity::class.java)
            Possible.Severity.values().forEach { severityMap[check]!![it] = arrayListOf() }

            // add
            check.possibles.forEach { possible ->
                map[check]?.add(possible)
                severityMap[check]!![possible.severity]?.add(possible)
                if(overviewMap.containsKey(possible.clazz) && !overviewMap[possible.clazz]!!.contains(check)) overviewMap[possible.clazz]!!.add(check)
                else overviewMap[possible.clazz] = arrayListOf(check)
            }
        }

        json = generateMapJSON()
        text = generateFormattedText()
    }

    private fun generateMapJSON(): JSONObject {
        val jsonObject = JSONObject()
        severityMap.forEach { it ->
            val checkObject = JSONObject()
            it.value.forEach { it1 ->
                val severityArray = JSONArray()
                it1.value.forEach {
                    val possibleObject = JSONObject()
                    possibleObject.put("class", it.clazz)
                    possibleObject.put("description", it.description)
                    severityArray.put(possibleObject)
                }
                checkObject.put(it1.key.name, severityArray)
            }
            jsonObject.put(it.key.name, checkObject)
        }
        return jsonObject
    }

    private fun generateFormattedText(): String {
        val out = StringBuilder()

        // print checks
        checks.forEach { check ->
            if(check.possibles.size > 0) {
                out.append(check.name + ": {\n")
                check.possibles.forEach { possible ->
                    out.append("\t" + possible.severity.name + ": " + possible.description + " in " + possible.clazz + "\n")
                }
                out.append("}\n")
            } else {
                out.append(check.name + ": CLEAR\n")
            }
        }

        // print overview map formatted
        if(overviewMap.keys.isNotEmpty()) {
            out.append("\nOverview:\n")
            overviewMap.keys.forEach { clazz ->
                val o: StringBuilder = StringBuilder("\t$clazz: ")
                overviewMap[clazz]!!.forEach { check -> o.append(check.name + " ") }
                out.append("$o\n")
            }
        } else {
            out.append("Program looks clear!\n")
        }

        return out.toString()
    }

    fun getMap(): MutableMap<AbstractCheck, ArrayList<Possible>> = map
    fun getSeverityMap(): MutableMap<AbstractCheck, MutableMap<Possible.Severity, ArrayList<Possible>>> = severityMap
    fun getJSON(): JSONObject = json
    fun getJSONString(): String = json.toString(4)
    fun getFormattedText(): String = text
}