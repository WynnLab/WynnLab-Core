import org.python.util.PythonInterpreter

fun main() {
    val pythonInterpreter = PythonInterpreter()
    pythonInterpreter.use { py ->
        py.setOut(System.out)

        val code = py.compile("""
            from p import ICKt
            from org.bukkit import Sound
            
            ICKt.acceptSound(Sound.ENTITY_ENDER_DRAGON_GROWL);
        """.trimIndent())

        py.exec(code)
    }
}

data class IC(val i: Int)

fun interface Tick {
    fun tick()
}