package com.wynnlab.spells

enum class SpellData(val spellName: String, val cloneSpellName: String, val cost: Int) {
    BASH("Bash", "Holy Blast", 6),
    CHARGE("Charge", "Leap", 4),
    UPPERCUT("Uppercut", "Heaven Jolt", 10),
    WAR_SCREAM("War Scream", "Cry Of The Gods", 6),

    ARROW_STORM("Arrow Storm", "Bolt Blizzard", 6),
    ESCAPE("Escape", "Spider Jump", 3),
    BOMB_ARROW("Bomb Arrow", "Creeper Dart", 8),
    ARROW_SHIELD("Arrow Shield", "Dagger Aura", 10),

    HEAL("Heal", "Remedy", 6),
    TELEPORT("Teleport", ""/*TODO*/, 4),
    METEOR("Meteor", "Death Star", 8),
    ICE_SNAKE("Ice Snake", "" /*TODO*/, 4),

    SPIN_ATTACK("Spin Attack", "Whirlwind", 6),
    VANISH("Vanish", "Shadow Clone", 1),
    MULTIHIT("Multihit", "Shadow Assault", 8),
    SMOKE_BOMB("Smoke Bomb", "Noxious Cloud", 8),

    TOTEM("Totem", "Sky Emblem", 4),
    HAUL("Haul", "Soar", 1),
    AURA("Aura", "Wind Surge", 8),
    UPROOT("Uproot", "" /*TODO*/, 6),
}