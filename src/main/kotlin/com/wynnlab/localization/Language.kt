package com.wynnlab.localization

import com.wynnlab.wynnlab
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*
import java.util.logging.Level

class Language(private val locale: Locale) {
    private val config = YamlConfiguration()

    init {
        val file = File(languageFolder, "${locale.toLanguageTag().toLowerCase().replace('-', '_')}.yml")
        config.load(file)
    }

    init {
        languages[locale] = this
        if (language_fallbacks[locale.language] == null) language_fallbacks[locale.language] = locale
    }

    fun getMessage(key: String, vararg format_args: Any?): String = getMessageOrNull(key, *format_args) ?:
    Language[language_fallbacks[locale.language]!!].getMessageOrNull(key, *format_args) ?:
    en_us.getMessageOrNull(key, *format_args) ?: "§4Nls: §r$key"

    fun getRandomMessage(key: String, vararg format_args: Any?): String = getRandomMessageOrNull(key, *format_args) ?:
    Language[language_fallbacks[locale.language]!!].getMessageOrNull(key, *format_args) ?:
    en_us.getRandomMessageOrNull(key, *format_args) ?: "§4Nls: §r$key"

    private fun getRandomMessageOrNull(key: String, vararg format_args: Any?) = config.getList(key)?.let {
        it.randomOrNull()?.let { r -> String.format(ChatColor.translateAlternateColorCodes('&', r as String), *format_args) }
    }

    private fun getMessageOrNull(key: String, vararg format_args: Any?): String? = config.getString(key)?.let {
        String.format(ChatColor.translateAlternateColorCodes('&', it), *format_args)
    }

    companion object {
        private val languages = hashMapOf<Locale, Language>()
        private val language_fallbacks = hashMapOf<String, Locale>()

        val en_us = Language(Locale.US)
        operator fun get(locale: Locale) = languages[locale] ?:
        languages[language_fallbacks[locale.language]] ?:
        en_us
    }
}

private val languageFolder by lazy { File(wynnlab.dataFolder, "lang") }

fun loadLanguages() {
    for (f in languageFolder.list() ?: return) {
        val name = f.substring(0, f.length - 4)
        wynnlab.logger.log(Level.INFO, "Loading language $name ...")
        try {
            if (name == "en_us") Language[Locale.US]
            else Language(Locale.US)
        } catch (e: Exception) {
            e.printStackTrace()
            continue
        }
    }
}