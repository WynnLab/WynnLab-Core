package com.wynnlab.util

sealed class Optional<T> {
    class Some<T>(val value: T) : Optional<T>() {
        override fun <R> ifSome(block: (T) -> R) = Some(block(value))
        override fun toString(): String = value.toString()
    }

    object None : Optional<Nothing>() {
        @Suppress("unchecked_cast")
        override fun <R> ifSome(block: (Nothing) -> R) = None as Optional<R>
        override fun toString(): String = "NONE"
    }

    abstract fun <R> ifSome(block: (T) -> R): Optional<R>

    companion object {
        @Suppress("unchecked_cast")
        operator fun <T> invoke(nullable: T?): Optional<T> = nullable?.let { Some(it) } ?: None as Optional<T> // Sneaky
    }
}

fun <T : Any> T?.optional() = Optional(this)

@Suppress("unchecked_cast")
fun <T : Any> Any?.optionalAs() = Optional(this as T?)