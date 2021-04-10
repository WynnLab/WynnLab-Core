package com.wynnlab.localization

import com.wynnlab.plugin
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.logging.Level

class Language(name: String) {
    private val config = YamlConfiguration()

    init {
        val file = File(languageFolder, "$name.yml")
        config.load(file)
    }

    init {
        languages[name] = this
    }

    fun getMessage(key: String, vararg format_args: Any?): String = config.getString(key)?.let {
        String.format(ChatColor.translateAlternateColorCodes('&', it), *format_args)
    } ?: en_us.getMessageOrNull(key, *format_args) ?: "§4Nls: §r$key"

    private fun getMessageOrNull(key: String, vararg format_args: Any?): String? = config.getString(key)?.let {
        String.format(ChatColor.translateAlternateColorCodes('&', it), *format_args)
    }

    companion object {
        private val languages = hashMapOf<String, Language>()

        val en_us = Language("en_us")
        operator fun get(locale: String) = languages[locale] ?: en_us
    }
}

private val languageFolder by lazy { File(plugin.dataFolder, "lang") }

fun loadLanguages() {
    for (f in languageFolder.list() ?: return) {
        val name = f.substring(0, f.length - 4)
        plugin.logger.log(Level.INFO, "Loading language $name ...")
        try {
            if (name == "en_us") Language["en_us"]
            else Language(name)
        } catch (e: Exception) {
            e.printStackTrace()
            continue
        }
    }
}