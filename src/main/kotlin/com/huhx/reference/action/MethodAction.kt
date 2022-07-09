package com.huhx.reference.action

import com.huhx.reference.constant.Constant.METHOD_VALIDATION_NAME
import com.huhx.reference.extension.findPsiElement
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiField
import com.intellij.psi.PsiManager
import com.intellij.psi.util.parentOfType
import com.intellij.util.PsiNavigateUtil

class MethodAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project!!
        val editor = FileEditorManager.getInstance(project).selectedTextEditor!!
        val virtualFile = FileDocumentManager.getInstance().getFile(editor.document)!!
        val offset = editor.caretModel.offset

        val element = PsiManager.getInstance(project).findFile(virtualFile)?.findElementAt(offset)
        val annotationElement = element?.parentOfType<PsiField>()?.modifierList?.findAnnotation(METHOD_VALIDATION_NAME)
        val attributeValueElement = annotationElement?.findAttributeValue("method")?.lastChild

        attributeValueElement?.let {
            val psiElements = project.findPsiElement(attributeValueElement.text)
            PsiNavigateUtil.navigate(psiElements.first(), true)
        }
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabledAndVisible = e.project != null
    }
}
