package com.huhx.reference.linemarker

import com.huhx.reference.constant.Constant.METHOD_VALIDATION_NAME
import com.huhx.reference.extension.findPsiElement
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement

class ValidatorLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element !is PsiAnnotation || !hasMethodAnnotation(element)) {
            return
        }
        val referenceName = element.findAttributeValue("method")?.lastChild?.text ?: return

        val properties = element.project.findPsiElement(referenceName)
        if (properties.isNotEmpty()) {
            val lineMarkerInfo = NavigationGutterIconBuilder.create(AllIcons.Chooser.Left)
                .setTargets(properties)
                .setTooltipText("Navigate ⌃⇧M")
                .createLineMarkerInfo(element)
            result.add(lineMarkerInfo)
        }
    }

    private fun hasMethodAnnotation(element: PsiAnnotation): Boolean {
        return element.nameReferenceElement?.qualifiedName.equals(METHOD_VALIDATION_NAME)
    }
}
