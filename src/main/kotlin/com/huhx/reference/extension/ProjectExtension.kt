package com.huhx.reference.extension

import com.huhx.reference.constant.Constant
import com.huhx.reference.setting.AppSettingsState
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.search.JavaFilesSearchScope
import com.intellij.psi.search.PsiShortNamesCache


fun Project.findPsiElement(value: String): List<PsiElement> {
    val className: String = AppSettingsState.getInstance().className
    val psiClasses = PsiShortNamesCache.getInstance(this).getClassesByName(className, JavaFilesSearchScope(this))
    return psiClasses[0].methods.filter {
        it.hasAnotationValue(Constant.VALIDATION_METHOD_NAME, value)
    }
}

fun Project.hasReference(value: String): Boolean {
    val className: String = AppSettingsState.getInstance().className
    val psiClass = PsiShortNamesCache.getInstance(this).getClassesByName(className, JavaFilesSearchScope(this)).first()
    return psiClass.findFieldByName(value.uppercase(), false) != null
}
