import jdk.nashorn.api.scripting.ScriptObjectMirror
import javax.script.ScriptEngineManager

fun main() {
    val js = ScriptEngineManager().getEngineByName("javascript")
    //js.eval("var a = Java.type(\"p.IC\");")
    println(js.get("Java")::class.java)
    /*js.eval("""
        function tick() {
            print(this.str);
        }
    """.trimIndent())
    val tick1 = js.get("tick").also { println(it::class.java.name) }
    js.eval("""
        function tick() {
            print("2: "+this.str);
        }
    """.trimIndent())
    val tick2 = js.get("tick").also { println(it::class.java.name) }
    js.eval("tick()")
    (tick1 as ScriptObjectMirror).call(null)
    println(js["no"])*/
    /*val obj = TestT("1") {
        tick.call(this)
    }
    obj.start()
    val obj2 = TestT("2") {
        tick.call(this)
    }
    obj2.start()*/
    /*js.put("coll", listOf("a", "b", "c") as Collection<String>)
    js.eval("""
        coll.forEach(function (it) {
            print(it);
        });
    """.trimIndent())*/
    /*js.put("IC", p.IC::class.java)
    js.eval("print(IC)")*/
    /*js.put("_ac", ::acceptClass)
    js.eval("function ac(clazz) { _ac.invoke(clazz); }")
    js.eval("ac(Java.type(\"p.IC\"))")*/
}

fun acceptClass(c: Class<*>) {
    print("Class: ${c.name}")
}

class TestT(val str: String, val runnable: TestT.() -> Unit) : Thread() {
    override fun run() {
        repeat(10) {
            this.runnable()
        }
    }
}