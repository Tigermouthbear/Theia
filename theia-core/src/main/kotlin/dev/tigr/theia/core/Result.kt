package dev.tigr.theia.core

import dev.tigr.theia.core.checks.AbstractCheck
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import org.json.JSONObject

/**
 * @author Tigermouthbear 1/23/21
 */
open class Result(val checks: Array<AbstractCheck>) {
    private val map: MutableMap<AbstractCheck, ArrayList<Possible>> = mutableMapOf()
    private val severityMap: MutableMap<AbstractCheck, MutableMap<Possible.Severity, ArrayList<Possible>>> = hashMapOf()
    private val overviewMap: MutableMap<String, ArrayList<AbstractCheck>> = hashMapOf()
    private var json: JSONObject
    private var text: String

    companion object empty: Result(arrayOf())

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
        severityMap.forEach {
            val checkObject = JSONObject()
            it.value.forEach {
                val severityArray = JSONArray()
                it.value.forEach {
                    val possibleObject = JSONObject()
                    possibleObject.put("class", it.clazz)
                    possibleObject.put("description", it.description)
                    severityArray.put(possibleObject)
                }
                checkObject.put(it.key.name, severityArray)
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

    public fun getMap(): MutableMap<AbstractCheck, ArrayList<Possible>> = map
    public fun getSeverityMap(): MutableMap<AbstractCheck, MutableMap<Possible.Severity, ArrayList<Possible>>> = severityMap
    public fun getJSON(): JSONObject = json
    public fun getJSONString(): String = json.toString(4)
    public fun getFormattedText(): String = text
}