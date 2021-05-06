package com.wynnlab.commands.tab_completers

import com.wynnlab.plugin
import org.bukkit.command.TabCompleter

abstract class BaseTabCompleter(vararg val names: String) : TabCompleter {
    fun completeWord(words: List<String>, input: String) = words.filter { it.startsWith(input) }
}

fun registerTabCompleters() {
    registerTabCompleter(CastTabCompleter)
    registerTabCompleter(ClassTabCompleter)
    registerTabCompleter(EssentialsTabCompleters)
    registerTabCompleter(GMTabCompleters)
    registerTabCompleter(ItemTabCompleter)
    registerTabCompleter(MobTabCompleter)
    registerTabCompleter(RankTabCompleter)
}

private fun registerTabCompleter(tabCompleter: BaseTabCompleter) {
    for (name in tabCompleter.names)
        plugin.getCommand(name)?.tabCompleter = tabCompleter
}