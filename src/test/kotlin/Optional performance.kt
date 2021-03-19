fun main() {
    val o = Optional("Hi")
    val t1 = System.nanoTime()
    repeat(100) {
        val l = o.ifSome1 { it.length }
    }
    val t2 = System.nanoTime()
    repeat(100) {
        val l = o.ifSome2 { it.length }
    }
    val t3 = System.nanoTime()
    println(t2 - t1)
    println(t3 - t2)
}

sealed class Optional<T> {
    class Some<T>(val value: T) : Optional<T>() {
        override fun <R> ifSome1(block: (T) -> R) = Some(block(value))
        override infix fun or1(block: () -> T): T = value
        override fun toString(): String = value.toString()
    }

    class None<T> : Optional<T>() {
        override fun <R> ifSome1(block: (T) -> R): Optional<R> = None()
        override infix fun or1(block: () -> T): T = block()
        override fun toString(): String = "NONE"
    }

    abstract fun <R> ifSome1(block: (T) -> R): Optional<R>

    inline fun <R> ifSome2(block: (T) -> R): Optional<R> = when (this) {
        is Some -> Some(block(value))
        is None -> None()
    }

    abstract infix fun or1(block: () -> T): T

    inline infix fun or2(block: () -> T): T = when (this) {
        is Some -> value
        is None -> block()
    }

    companion object {
        operator fun <T> invoke(nullable: T?): Optional<T> = nullable?.let { Some(it) } ?: None()
    }
}