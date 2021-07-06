package com.wynnlab.items

import com.wynnlab.api.meta
import com.wynnlab.api.metaAs
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import java.util.*

data class Build(
    val head: ItemStack? = null,
    val chest: ItemStack? = null,
    val legs: ItemStack? = null,
    val feet: ItemStack? = null,
    val hand: ItemStack? = null,
) {
    fun equip(player: Player) {
        player.equipment?.apply {
            helmet = head
            chestplate = chest
            leggings = legs
            boots = feet
            setItemInMainHand(hand)
        }
    }

    fun publish(name: String) {
        publicBuilds[name] = this
    }

    companion object {
        operator fun set(player: UUID, name: String, build: Build) {
            (builds[player] ?: hashMapOf<String, Build>().also { builds[player] = it })[name] = build
        }

        operator fun get(player: UUID, name: String): Build? = builds[player]?.get(name)

        fun remove(player: UUID, name: String): Build? {
            val b = builds[player]?.remove(name) ?: return null
            for ((k, v) in publicBuilds) {
                if (v == b) publicBuilds.remove(k)
            }
            return b
        }

        fun buildsOf(player: Player): HashMap<String, Build> = builds[player.uniqueId] ?: hashMapOf()

        fun buildNamesOf(player: Player): Set<String> = builds[player.uniqueId]?.keys ?: emptySet()

        private val builds = hashMapOf<UUID, HashMap<String, Build>>()
        private val publicBuilds = hashMapOf<String, Build>()
    }

    class GUI(player: Player) : com.wynnlab.gui.GUI(player, Component.text("Builds"), 6) {
        var privateBuilds = true

        init {
            registerListener {
                it.isCancelled = true

                val pb = privateBuilds
                var update = false

                if (it.slot == 0) privateBuilds = true
                else if (it.slot == 1) privateBuilds = false
                if (pb != privateBuilds) update = true

                if (it.slot >= 9) {
                    val build = if (pb)
                        builds[player.uniqueId]?.get((it.currentItem?.displayName() as? TextComponent)?.content())
                    else
                        publicBuilds[it.currentItem?.displayName()?.let { t -> LegacyComponentSerializer.legacy('&').serialize(t) }]

                    if (it.isLeftClick) {
                        build?.equip(player)
                        player.playSound(player.location, Sound.ENTITY_HORSE_SADDLE, .5f, 1f)
                        player.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1f, 1f)
                        player.closeInventory()
                    } else if (it.isRightClick && it.currentItem != null) {
                        if (it.currentItem?.type == Material.BUNDLE) {
                            it.currentItem = ItemStack(build?.hand?.type ?: Material.BARRIER).meta { displayName(it.currentItem?.displayName()) }
                        } else {
                            it.currentItem = ItemStack(Material.BUNDLE).metaAs<BundleMeta> {
                                displayName(it.currentItem?.displayName())
                                build?.run {
                                    hand?.let(this@metaAs::addItem)
                                    feet?.let(this@metaAs::addItem)
                                    legs?.let(this@metaAs::addItem)
                                    chest?.let(this@metaAs::addItem)
                                    head?.let(this@metaAs::addItem)
                                }
                            }
                        }
                    }
                }

                if (update) update()
            }
        }

        override fun update() {
            inventory.clear()

            inventory.setItem(0, ItemStack(Material.PLAYER_HEAD).meta { displayName(Component.text("private")) })
            inventory.setItem(1, ItemStack(Material.PLAYER_HEAD).meta { displayName(Component.text("public")) })

            if (privateBuilds) {
                var i = 0
                for ((k, v) in buildsOf(player)) {
                    inventory.setItem(i + 9, ItemStack(v.hand?.type ?: Material.BARRIER).meta { displayName(Component.text(k)) })
                    ++i
                }
            } else {
                var i = 0
                for ((k, v) in publicBuilds) {
                    inventory.setItem(i + 9, ItemStack(v.hand?.type ?: Material.BARRIER).meta { displayName(LegacyComponentSerializer.legacy('&').deserialize(k)) })
                    ++i
                }
            }
        }
    }
}
