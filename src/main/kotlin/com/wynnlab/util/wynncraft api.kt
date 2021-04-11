package com.wynnlab.util

import com.wynnlab.items.APIException
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

fun getWynncraftAPIResult(address: String): Deferred<JSONObject> {
    val task = {
        val url = try {
            URL(address)
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

        jsonParser.parse(inline) as JSONObject
    }

    return Deferred(task)
}

val jsonParser = JSONParser()