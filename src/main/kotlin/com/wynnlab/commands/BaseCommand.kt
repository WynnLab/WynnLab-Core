package com.wynnlab.commands

import com.wynnlab.wynnlab
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
    registerCommand(PVPCommands)
    registerCommand(RankCommand)
}

private fun registerCommand(command: BaseCommand) {
    for (name in command.names)
        wynnlab.getCommand(name)?.setExecutor(command)
}