package com.wynnlab.util

sealed class Optional<T> {
    class Some<T>(val value: T) : Optional<T>() {
        override fun toString(): String = value.toString()
    }

    object None : Optional<Nothing>() {
        override fun toString(): String = "NONE"
    }

    @Suppress("unchecked_cast")
    inline fun <R> ifSome(block: (T) -> R): Optional<R> = when (this) {
        is Some -> Some(block(value))
        is None -> None as Optional<R>
    }

    inline infix fun or(block: () -> T): T = when (this) {
        is Some -> value
        is None -> block()
    }

    companion object {
        @Suppress("unchecked_cast")
        operator fun <T> invoke(nullable: T?): Optional<T> = nullable?.let { Some(it) } ?: None as Optional<T>
    }
}

fun <T : Any> T?.optional() = Optional(this)

@Suppress("unchecked_cast")
fun <T : Any> Any?.optionalAs() = Optional(this as T?)