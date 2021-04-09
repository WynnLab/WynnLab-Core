package com.wynnlab.items

import com.wynnlab.util.getWynncraftAPIResult
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.jvm.Throws

@Suppress("unchecked_cast")
@Throws(APIException::class)
fun getAPIResults(search: String): List<JSONObject> {
    val root = getWynncraftAPIResult("https://api.wynncraft.com/public_api.php?action=itemDB&search=${search.replace(" ", "%20")}")
    val items = root["items"] as JSONArray

    return items as List<JSONObject>
}

class APIException(message: String) : Exception("Could not access the API ($message)")