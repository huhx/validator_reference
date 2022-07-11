package com.huhx.reference.action

import com.huhx.reference.extension.hasReference
import com.huhx.reference.setting.AppSettingsState
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReferenceExpression

class MethodValidateIntentionAction : PsiElementBaseIntentionAction(), IntentionAction {
    private val className = AppSettingsState.getInstance().className

    override fun getText(): String {
        return "Create related filed and method in $className"
    }

    override fun getFamilyName(): String {
        return "Create related filed and method"
    }

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {

        if (element !is PsiIdentifier || element.textMatches(className)) {
            return false
        }

        if (element.parent !is PsiReferenceExpression || !element.parent.text.startsWith(className)) {
            return false
        }

        return !project.hasReference(element.text)
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        BrowserUtil.browse("https://stackoverflow.com/search?q=love")
    }

}
