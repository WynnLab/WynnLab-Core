import org.python.util.PythonInterpreter

fun main() {
    val pythonInterpreter = PythonInterpreter()
    pythonInterpreter.use { py ->
        py.setOut(System.out)

        val code = py.compile("""
            class A(S):
                def greet(self):
                    print("Hi, I'm {me}".format(me=self))
                    print(self.zero)
            
            a = A()
        """.trimIndent())

        py.set("S", S::class.java)
        py.set("ic", IC(1))

        py.exec(code)

        py.print("A")
        py.print("S")
        py.print("a")
        py.print("ic")
    }
}

fun PythonInterpreter.print(name: String) {
    val value = get(name)
    println("${value::class.simpleName} $name = $value")
}

abstract class S {
    var zero = 0
    abstract fun greet()
}

fun echo(it: String) = println(it)

data class IC(val i: Int)

fun interface Tick {
    fun tick()
}