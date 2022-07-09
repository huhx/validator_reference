package com.huhx.reference.extension

import com.huhx.reference.constant.Constant
import com.huhx.reference.setting.AppSettingsState
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.search.JavaFilesSearchScope
import com.intellij.psi.search.PsiShortNamesCache

private val className: String = AppSettingsState.getInstance().className

fun Project.findPsiElement(value: String): List<PsiElement> {
    val psiClasses = PsiShortNamesCache.getInstance(this).getClassesByName(className, JavaFilesSearchScope(this))
    return psiClasses[0].methods.filter { it.hasAnotationValue(Constant.ANNOTATION_NAME, value) }
}
