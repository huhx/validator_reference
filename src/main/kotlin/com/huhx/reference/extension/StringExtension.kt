package com.huhx.reference.extension

import com.google.common.base.CaseFormat
import java.util.*

fun String.isPrimitive(): Boolean {
    val primitiveLists = listOf("int", "float", "double", "byte", "char", "long", "bool", "short")
    val wrapperTypeList = listOf("Integer", "Float", "Double", "Byte", "Character", "Long", "Boolean", "Short", "String")

    return wrapperTypeList.contains(this) || primitiveLists.contains(this)
}

fun String.toCamelCase(): String {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this)
}

fun String.firstLowerCase(): String {
    return this.replaceFirstChar { it.lowercase(Locale.getDefault()) }
}
