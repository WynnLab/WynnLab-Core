import com.wynnlab.util.Optional
import com.wynnlab.util.optionalAs

fun main() {
    val some: Optional<String> = Optional("Hello")
    val none: Optional<String> = Optional(null)

    println(some)
    println(none)

    val l1 = some.ifSome { it.length }
    val l2 = none.ifSome { it.length }

    println(l1)
    println(l2)

    val any1: Any = "Any"
    val any2: Any? = null
    val a1Opt = any1.optionalAs<String>()
    val a2Opt = any2.optionalAs<String>()

    println(a1Opt)
    println(a2Opt)
}