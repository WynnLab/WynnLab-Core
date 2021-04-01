import org.python.util.PythonInterpreter

fun main() {
    val pythonInterpreter = PythonInterpreter()
    pythonInterpreter.use { py ->
        py.setOut(System.out)

        val code = py.compile("""
            def doSth(ic):
                print(ic)
            
            s = S(doSth)
            
            s.fn.invoke('Hi')
        """.trimIndent())

        py.set("S", S::class.java)

        py.exec(code)
    }
}

fun PythonInterpreter.print(name: String) {
    val value = get(name)
    println("${value::class.simpleName} $name = $value")
}

class S(val fn: (String) -> Unit)

fun echo(it: String) = println(it)

data class IC(val i: Int)

fun interface Tick {
    fun tick()
}