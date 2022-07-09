package com.huhx.reference.action

import com.huhx.reference.constant.Constant
import com.huhx.reference.extension.hasAnotationValue
import com.huhx.reference.setting.AppSettingsState
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.search.JavaFilesSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import com.intellij.util.PsiNavigateUtil

class MethodAction : AnAction() {
    private var className: String = AppSettingsState.getInstance().className

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val editor = FileEditorManager.getInstance(project).selectedTextEditor!!
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)!!
        val offset = editor.caretModel.offset

        val element = PsiManager.getInstance(project).findFile(virtualFile)?.findElementAt(offset)
        if (element !is PsiIdentifier || element.text != "MethodValidate") {
            return
        }
        val attributeValue = (element.parent.parent as PsiAnnotation).findAttributeValue("method")?.lastChild
        attributeValue?.let {
            val psiElements = findProperties(project, attributeValue.text)
            PsiNavigateUtil.navigate(psiElements.first(), true)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    private fun findProperties(project: Project, value: String): List<PsiElement> {
        val psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, JavaFilesSearchScope(project))
        return psiClasses[0].methods.filter { it.hasAnotationValue(Constant.ANNOTATION_NAME, value) }
    }
}
