package com.wynnlab.commands

import com.wynnlab.plugin

fun registerCommands() = with(plugin) {
    getCommand("class")?.setExecutor(ClassCommand)

    getCommand("item")?.setExecutor(ItemCommand)

    getCommand("cast")?.setExecutor(CastCommand)

    getCommand("rank")?.setExecutor(RankCommand)

    getCommand("itemdata")?.setExecutor(DevCommands)
    getCommand("getid")?.setExecutor(DevCommands)
    getCommand("script")?.setExecutor(DevCommands)

    getCommand("msg")?.setExecutor(EssentialsCommands)
    getCommand("r")?.setExecutor(EssentialsCommands)
    getCommand("party")?.setExecutor(EssentialsCommands)
    getCommand("p")?.setExecutor(EssentialsCommands)

    getCommand("dummy")?.setExecutor(DummyCommand)

    getCommand("upload")?.setExecutor(GMCommands)
    getCommand("wlrl")?.setExecutor(GMCommands)

    getCommand("mob")?.setExecutor(MobCommand)
}