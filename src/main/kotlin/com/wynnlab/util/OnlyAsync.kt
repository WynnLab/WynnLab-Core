package com.wynnlab.util

/**
 * A function/class that should only be executed in async environment
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class OnlyAsync