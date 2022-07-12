package com.huhx.reference.inspection

import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.codeInspection.LocalInspectionTool

class MethodInspectionProvider: InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<out LocalInspectionTool>> {
        return arrayOf(MethodInspection::class.java)
    }
}
