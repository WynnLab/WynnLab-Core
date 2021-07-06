package com.wynnlab.commands.tab_completers

import com.wynnlab.wynnlab
import org.bukkit.command.TabCompleter

abstract class BaseTabCompleter(vararg val names: String) : TabCompleter {
    fun completeWord(words: List<String>, input: String) = words.filter { it.startsWith(input) }
}

fun registerTabCompleters() {
    registerTabCompleter(BuildTabCompleter)
    registerTabCompleter(CastTabCompleter)
    registerTabCompleter(ClassTabCompleter)
    registerTabCompleter(EssentialsTabCompleters)
    registerTabCompleter(GMTabCompleters)
    registerTabCompleter(ItemTabCompleter)
    registerTabCompleter(MobTabCompleter)
    registerTabCompleter(RankTabCompleter)
    registerTabCompleter(NoTabCompleter)
}

private fun registerTabCompleter(tabCompleter: BaseTabCompleter) {
    for (name in tabCompleter.names)
        wynnlab.getCommand(name)?.tabCompleter = tabCompleter
}