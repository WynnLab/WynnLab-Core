package com.wynnlab.commands

import com.wynnlab.plugin
import org.bukkit.command.CommandExecutor

abstract class BaseCommand(vararg val names: String) : CommandExecutor

fun registerCommands() {
    registerCommand(CastCommand)
    registerCommand(ClassCommand)
    registerCommand(DevCommands)
    registerCommand(DummyCommand)
    registerCommand(EssentialsCommands)
    registerCommand(GMCommands)
    registerCommand(ItemCommand)
    registerCommand(MobCommand)
    registerCommand(RankCommand)
    registerCommand(SidebarCommand)
}

private fun registerCommand(command: BaseCommand) {
    for (name in command.names)
        plugin.getCommand(name)?.setExecutor(command)
}