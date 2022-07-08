package com.huhx.reference.extension

import com.intellij.psi.PsiMethod

fun PsiMethod.hasAnotation(anotation: String): Boolean {
    return this.modifierList.hasAnnotation(anotation)
}

fun PsiMethod.hasAnotationValue(anotationName: String, value: String): Boolean {
    val annotation = this.modifierList.findAnnotation(anotationName)
    return annotation?.findAttributeValue("value")?.text == value
}
