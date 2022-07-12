package com.huhx.reference.inspection

import com.google.common.base.CaseFormat
import java.util.*

class MethodAndFiled(private val type: String, val name: String) {

    fun getField(): String {
        val filedValue = name.replace("_", " ").lowercase(Locale.getDefault())
        return """
            public static final String %s = "%s";
            """.trimIndent().format(name, filedValue)
    }

    fun getMethod(): String {
        return """
              @ValidationMethod(%s)
              public static boolean %s(%s value) {
                // todo
                return false;
              }
              """.trimIndent().format(name, toCamelCase(name), type)
    }

    private fun toCamelCase(string: String): String {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string)
    }
}
