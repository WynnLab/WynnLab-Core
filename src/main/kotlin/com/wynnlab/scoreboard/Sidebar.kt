package com.wynnlab.scoreboard

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Team

class Sidebar(title: Component) {
    private val sb = Bukkit.getScoreboardManager().newScoreboard
    private val o = sb.registerNewObjective("o", "dummy", title)
    init {
        o.displaySlot = DisplaySlot.SIDEBAR
    }

    var title = title
        set(value) {
            o.displayName(value)
            field = value
        }

    private val lines = Array<Component?>(16) { null }
    private val setLines = BooleanArray(16)

    private var displayLines = 0
    val lineCount get() = displayLines

    fun show(player: Player) {
        player.scoreboard = sb
    }

    /**
     * line is top down 0-16
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun set(line: Int, text: Component) {
        if (displayLines <= line) displayLines = line + 1
        lines[line] = text
        setLines[line] = true

        setScore(16 - line, text)

        fillEmptyLines()
    }

    /**
     * line is top down 0-16
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun setForUpdate(line: Int, text: Component) {
        if (displayLines <= line) displayLines = line + 1
        lines[line] = text
        setLines[line] = true

        setScore(16 - line, text)
    }

    /**
     * line is top down 0-16
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun get(line: Int) = lines[line]

    /**
     * line is top down 0-16
     */
    @Throws(ArrayIndexOutOfBoundsException::class)
    fun clear(line: Int) {
        lines[line] = null
        setLines[line] = false

        setDisplayLines()
        fillEmptyLines()
    }

    fun clear() {
        for (i in 0..15) {
            lines[i] = null
            setLines[i] = false
            removeScore(i)
        }
        displayLines = 0
    }

    fun update() = fillEmptyLines()

    private fun setScore(score: Int, text: Component) {
        val t = getTeam(score)
        t.prefix(text)
        o.getScore(colorInt(score)).score = score
    }

    private fun removeScore(score: Int) {
        val name = colorInt(score)
        if (o.getScore(name).isScoreSet)
            return
        sb.resetScores(name)
    }

    private fun getTeam(score: Int): Team {
        val name = colorInt(score)
        return sb.getEntryTeam(name) ?: sb.registerNewTeam(score.toString()).apply {
            addEntry(name)
        }
    }

    private fun setDisplayLines() {
        var i = -1
        var last = i
        for (b in setLines) {
            if (!b)
                last = i

            ++i
        }
        displayLines = last + 1
    }

    private fun fillEmptyLines() {
        var i = -1
        for (b in setLines) {
            ++i
            if (i >= displayLines)
                removeScore(16 - i)
            else if (!b)
                setScore(16 - i, Component.empty())
        }
    }

    private fun colorInt(int: Int) = "§${(int-1).toString(0x10)}"
}