package com.wynnlab.items

import com.wynnlab.plugin
import com.wynnlab.util.Deferred
import com.wynnlab.util.getWynncraftAPIResult
import com.wynnlab.util.jsonParser
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileNotFoundException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.jvm.Throws

@Suppress("unchecked_cast")
@Throws(APIException::class)
fun getAPIResults(search: String): Deferred<List<JSONObject>> = Deferred {
    val root = getWynncraftAPIResult("https://api.wynncraft.com/public_api.php?action=itemDB&search=${search.replace(" ", "%20")}").task()
    val items = (root["items"] as List<JSONObject>).toMutableList()

    val customItems = customItemFolder?.listFiles { file -> search in file.name } ?: arrayOf()
    customItems.forEach { item ->
        items.add(jsonParser.parse(item.readText()) as JSONObject)
    }

    items
}

val customItemFolder by lazy { try { File(plugin.dataFolder, "custom_items") } catch (e: FileNotFoundException) { null } }

class APIException(message: String) : Exception("Could not access the API ($message)")