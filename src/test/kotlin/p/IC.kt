package p

import org.bukkit.Sound

data class IC(val i: Int)

fun acceptClass(clazz: Class<out Any>) {
    println(clazz.interfaces.contentToString())
}

fun acceptSound(a: Sound) {
    print(a.name)
}