package com.huhx.reference.action

import com.huhx.reference.constant.Constant.METHOD_VALIDATION_NAME
import com.huhx.reference.extension.findPsiElement
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parentOfType
import com.intellij.util.PsiNavigateUtil

class MethodAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val element = findSelectedPsiElement(project) ?: return

        val psiAnnotation = element.parentOfType<PsiAnnotation>(true)
        if (psiAnnotation?.qualifiedName != METHOD_VALIDATION_NAME) {
            return
        }

        val attributeValueElement = psiAnnotation.findAttributeValue("method")?.lastChild
        attributeValueElement?.let {
            val psiElements = project.findPsiElement(attributeValueElement.text)
            PsiNavigateUtil.navigate(psiElements.first(), true)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }

    private fun findSelectedPsiElement(project: Project): PsiElement? {
        val textEditor = FileEditorManager.getInstance(project).selectedTextEditor ?: return null
        val virtualFile = FileDocumentManager.getInstance().getFile(textEditor.document) ?: return null
        val offset = textEditor.caretModel.offset

        return PsiManager.getInstance(project).findFile(virtualFile)?.findElementAt(offset)
    }
}
