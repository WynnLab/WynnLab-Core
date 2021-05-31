package com.wynnlab.commands.tab_completers

import com.wynnlab.wynnlab
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.io.File

object MobTabCompleter : BaseTabCompleter("mob") {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        if (args.isEmpty())
            return null

        return completeWord(
            (File(wynnlab.dataFolder, "mobs").list() ?: return emptyList()).asList()
                .filter { it.endsWith(".yml") }.map { it.substring(0, it.length - 4) },
            args[0])
    }
}