package com.huhx.reference.inspection

import com.huhx.reference.extension.firstLowerCase
import com.huhx.reference.extension.isPrimitive
import com.huhx.reference.extension.toCamelCase
import java.util.*

class MethodAndFiled(private val type: String, val name: String) {

    fun getField(): String {
        val filedValue = name.replace("_", " ").lowercase(Locale.getDefault())
        return """
            public static final String %s = "%s";
            """.trimIndent().format(name, filedValue)
    }

    fun getMethod(): String {
        val parameterName = if (type.isPrimitive()) "value" else type.firstLowerCase()
        return """
              @ValidationMethod(%s)
              public static boolean %s(%s %s) {
                // todo
                return false;
              }
              """.trimIndent().format(name, name.toCamelCase(), type, parameterName)
    }

}
