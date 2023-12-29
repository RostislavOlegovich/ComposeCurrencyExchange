package com.rostyslavhrebeniuk.currencyexchanger.utils

import kotlin.reflect.KProperty1

inline fun <reified T : Enum<T>> getNames() = enumValues<T>().map { it.name }

@Suppress("UNCHECKED_CAST")
fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
    val property = instance::class.members
        .first { it.name == propertyName } as KProperty1<Any, *>
    return property.get(instance) as R
}

fun String.asDouble() = this.replace(',', '.').toDouble()
