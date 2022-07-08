package com.huhx.reference.linemarker

import com.huhx.reference.constant.Constant.ANNOTATION_NAME
import com.huhx.reference.extension.hasAnotation
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch

class MethodLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element !is PsiClass || element.name != "MethodValidator") {
            return
        }

        element.methods.filter {
            it.hasAnotation(ANNOTATION_NAME)
        }.mapNotNull {
            it.modifierList.findAnnotation(ANNOTATION_NAME)!!
        }.forEach { psiMethod ->
            val attributeValue = psiMethod.findAttributeValue("value")
            attributeValue?.text?.let { filedName ->
                val references = findReferences(element, filedName).filter { it != attributeValue }
                if (references.isNotEmpty()) {
                    val lineMarkerInfo = NavigationGutterIconBuilder.create(AllIcons.Chooser.Right)
                        .setTargets(references)
                        .setTooltipText("Navigate to Usage")
                        .createLineMarkerInfo(attributeValue)
                    result.add(lineMarkerInfo)
                }
            }
        }
    }

    private fun findReferences(psiClass: PsiClass, filedName: String): List<PsiElement> {
        val psiElement = psiClass.findFieldByName(filedName, false)
        return ReferencesSearch.search(psiElement!!).map { it.element }
    }
}