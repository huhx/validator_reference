package com.huhx.reference.extension

import com.huhx.reference.constant.Constant
import com.huhx.reference.setting.AppSettingsState
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.search.JavaFilesSearchScope
import com.intellij.psi.search.PsiShortNamesCache


fun Project.findPsiElement(value: String): List<PsiElement> {
    return getPsiClasses().first().methods.filter {
        it.hasAnotationValue(Constant.VALIDATION_METHOD_NAME, value)
    }
}

fun Project.hasReference(value: String): Boolean {
    return getPsiClasses().first().findFieldByName(value.uppercase(), false) != null
}

private fun Project.getPsiClasses(): Array<out PsiClass> {
    val className: String = AppSettingsState.getInstance().className
    return PsiShortNamesCache.getInstance(this).getClassesByName(className, JavaFilesSearchScope(this))
}
