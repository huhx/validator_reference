package com.huhx.reference.linemarker

import com.huhx.reference.constant.Constant.ANNOTATION_NAME
import com.huhx.reference.extension.hasAnotationValue
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameValuePair
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.impl.search.JavaFilesSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.huhx.reference.setting.AppSettingsState

class ValidatorLineMarkerProvider : RelatedItemLineMarkerProvider() {
    private var className: String = AppSettingsState.getInstance().className

    override fun collectNavigationMarkers(element: PsiElement, result: MutableCollection<in RelatedItemLineMarkerInfo<*>>) {
        if (element !is PsiReferenceExpression || element.parent !is PsiNameValuePair) {
            return
        }

        if (!element.text.startsWith(className)) {
            return
        }

        val properties = findProperties(element.project, element.referenceName!!)
        if (properties.isNotEmpty()) {
            val lineMarkerInfo = NavigationGutterIconBuilder.create(AllIcons.Chooser.Left)
                .setTargets(properties)
                .setTooltipText("Navigate to MethodValidate")
                .createLineMarkerInfo(element)
            result.add(lineMarkerInfo)
        }
    }

    private fun findProperties(project: Project, value: String): List<PsiElement> {
        val psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, JavaFilesSearchScope(project))
        return psiClasses[0].methods.filter { it.hasAnotationValue(ANNOTATION_NAME, value) }
    }
}
