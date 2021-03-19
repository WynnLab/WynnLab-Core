package com.wynnlab

enum class WynnClass(val className: String, val cloneName: String) {
    Warrior("Warrior", "Knight"),
    Archer("Archer", "Hunter"),
    Mage("Mage", "Dark Wizard"),
    Assassin("Assassin", "Ninja"),
    Shaman("Shaman", "Skyseer");

    fun both() = "$className/$cloneName"
}