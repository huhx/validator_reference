package com.huhx.reference.action

import com.huhx.reference.extension.hasReference
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReferenceExpression

class MethodValidateIntentionAction : PsiElementBaseIntentionAction(), IntentionAction {

    override fun getText(): String {
        return "Create related filed and method"
    }

    override fun getFamilyName(): String {
        return "Create related filed and method"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element !is PsiIdentifier || element.textMatches("MethodValidator")) {
            return false
        }

        if (element.parent !is PsiReferenceExpression || !element.parent.text.startsWith("MethodValidator")) {
            return false
        }

        return !project.hasReference(element.text)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        BrowserUtil.browse("https://stackoverflow.com/search?q=love")
    }

}
