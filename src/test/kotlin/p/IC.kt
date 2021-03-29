package p

data class IC(var i: Int) {
    data class U(val a: String)
}

data class FC(var f: Float)

fun acceptClass(clazz: Class<out Any>) {
    println(clazz.interfaces.contentToString())
}