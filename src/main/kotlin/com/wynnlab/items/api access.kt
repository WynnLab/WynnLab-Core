package com.wynnlab.items

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
    val url = try {
        URL("https://api.wynncraft.com/public_api.php?action=itemDB&search=${search.replace(" ", "%20")}")
    } catch (e: MalformedURLException) {
        throw APIException("Malformed URL")
    }

    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()
    val responseCode = connection.responseCode

    if (responseCode != 200)
        throw APIException("Response code: $responseCode")

    val scanner = Scanner(url.openStream())
    val inline = buildString {
        while (scanner.hasNext())
            append(scanner.nextLine())
    }
    scanner.close()

    val parser = JSONParser()
    val root = parser.parse(inline) as JSONObject
    val items = root["items"] as JSONArray

    return items as List<JSONObject>
}

class APIException(message: String) : Exception("Could not access the API ($message)")