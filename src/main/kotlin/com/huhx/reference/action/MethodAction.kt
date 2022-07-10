package com.huhx.reference.action

import com.huhx.reference.constant.Constant.ANNOTATION_NAME
import com.huhx.reference.extension.findPsiElement
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parentOfType
import com.intellij.util.PsiNavigateUtil

class MethodAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document) ?: return

        val offset = editor.caretModel.offset
        val element = PsiManager.getInstance(project).findFile(virtualFile)?.findElementAt(offset)
        if (element !is PsiIdentifier || element.text != ANNOTATION_NAME) {
            return
        }

        val attributeValueElement = element.parentOfType<PsiAnnotation>()?.findAttributeValue("method")?.lastChild
        attributeValueElement?.let {
            val psiElements = project.findPsiElement(attributeValueElement.text)
            PsiNavigateUtil.navigate(psiElements.first(), true)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
