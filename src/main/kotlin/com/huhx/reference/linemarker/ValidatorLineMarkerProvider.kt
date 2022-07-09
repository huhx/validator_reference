package com.huhx.reference.linemarker

import com.huhx.reference.extension.findPsiElement
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameValuePair
import com.intellij.psi.PsiReferenceExpression
import com.huhx.reference.setting.AppSettingsState

class ValidatorLineMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        val className: String = AppSettingsState.getInstance().className

        if (element !is PsiReferenceExpression || element.parent !is PsiNameValuePair) {
            return
        }

        if (!element.text.startsWith(className)) {
            return
        }

        val properties = element.project.findPsiElement(element.referenceName!!)
        if (properties.isNotEmpty()) {
            val lineMarkerInfo = NavigationGutterIconBuilder.create(AllIcons.Chooser.Left)
                .setTargets(properties)
                .setTooltipText("Navigate to MethodValidate")
                .createLineMarkerInfo(element)
            result.add(lineMarkerInfo)
        }
    }
}
