package com.wynnlab.commands.tab_completers

import com.wynnlab.plugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

fun registerTabCompleters() = with(plugin) {
    getCommand("class")?.tabCompleter = ClassTabCompleter

    getCommand("item")?.tabCompleter = ItemTabCompleter

    getCommand("cast")?.tabCompleter = CastTabCompleter

    getCommand("rank")?.tabCompleter = RankTabCompleter

    /*getCommand("itemdata")?.setExecutor(DevCommands)
    getCommand("getid")?.setExecutor(DevCommands)
    getCommand("script")?.setExecutor(DevCommands)*/

    getCommand("msg")?.tabCompleter = EssentialsTabCompleters
    getCommand("r")?.tabCompleter = EssentialsTabCompleters
    getCommand("party")?.tabCompleter = EssentialsTabCompleters
    getCommand("p")?.tabCompleter = EssentialsTabCompleters

    getCommand("dummy")?.tabCompleter = NoTabCompleter

    getCommand("upload")?.tabCompleter = GMTabCompleters
    getCommand("wlrl")?.tabCompleter = GMTabCompleters

    getCommand("mob")?.tabCompleter = MobTabCompleter
}

fun completeWord(words: List<String>, start: String, ignoreCase: Boolean = true) =
    words.filter { it.startsWith(start, ignoreCase) }

object NoTabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ) = emptyList<String>()
}