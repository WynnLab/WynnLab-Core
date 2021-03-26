import org.python.util.PythonInterpreter

fun main() {
    val pythonInterpreter = PythonInterpreter()
    pythonInterpreter.use { py ->
        py.setOut(System.out)

        val code = py.compile("""
            print('Hello World!')
        """.trimIndent())

        py.exec(code)
        py.exec(code)
    }
}

data class IC(val i: Int)

fun interface Tick {
    fun tick()
}