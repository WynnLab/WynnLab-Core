package com.wynnlab.util

/**
 * A class that should only be created in async environment
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class OnlyAsync
