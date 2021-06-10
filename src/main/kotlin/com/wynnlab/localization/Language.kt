package com.wynnlab.localization

import com.wynnlab.NL_REGEX
import com.wynnlab.wynnlab
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*
import java.util.logging.Level

class Language(private val locale: Locale) {
    private val config = YamlConfiguration()

    init {
        val file = File(languageFolder, "${locale.toLanguageTag().replace('-', '_')}.yml")
        config.load(file)
    }

    init {
        languages[locale] = this
        if (language_fallbacks[locale.language] == null) language_fallbacks[locale.language] = locale
    }

    fun getMessage(key: String, vararg format_args: Any?) = LegacyComponentSerializer.legacy('&').deserialize(getMessageAsString(key, *format_args))

    fun getMessageMultiline(key: String, vararg format_args: Any?) = getMessageAsString(key, *format_args).split(NL_REGEX).map {
        LegacyComponentSerializer.legacy('&').deserialize(it)
    }

    fun getMessageAsString(key: String, vararg format_args: Any?) = getMessageOrNull(key, format_args) ?:
    Language[language_fallbacks[locale.language]!!].getMessageOrNull(key, format_args) ?:
    en_us.getMessageOrNull(key, format_args) ?: "&4Nls: &r$key"


    fun getRandomMessage(key: String, vararg format_args: Any?) = LegacyComponentSerializer.legacy('&').deserialize(getRandomMessageAsString(key, *format_args))

    fun getRandomMessageAsString(key: String, vararg format_args: Any?): String = getRandomMessageOrNull(key, format_args) ?:
    Language[language_fallbacks[locale.language]!!].getMessageOrNull(key, format_args) ?:
    en_us.getRandomMessageOrNull(key, format_args) ?: "&4Nls: &r$key"


    private fun getRandomMessageOrNull(key: String, format_args: Array<out Any?>) = config.getList(key)?.let {
        it.randomOrNull()?.let { r -> String.format(r as String, *format_args) }
    }

    private fun getMessageOrNull(key: String, format_args: Array<out Any?>): String? = config.getString(key)?.let {
        String.format(it, *format_args)
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
            if (name == "en_US") Language[Locale.US]
            else Language(Locale.US)
        } catch (e: Exception) {
            e.printStackTrace()
            continue
        }
    }
}