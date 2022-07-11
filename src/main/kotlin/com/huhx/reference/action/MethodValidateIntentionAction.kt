package com.huhx.reference.action

import com.huhx.reference.constant.Constant.VALIDATION_METHOD_NAME
import com.huhx.reference.extension.getPsiClasses
import com.huhx.reference.extension.hasAnotation
import com.huhx.reference.extension.hasReference
import com.huhx.reference.setting.AppSettingsState
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReferenceExpression
import java.util.*

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
        val name = element.text
        val fieldString = """
            public static final String %s = "%s";
        """.trimIndent().format(name, name.replace("_", " ").lowercase(Locale.getDefault()))
        val methodString = """
              @ValidationMethod(%s)
              public static boolean %s(%s value) {
                // todo
                return false;
              }
        """.trimIndent().format(name, name.replace("_", "").lowercase(Locale.getDefault()), "String")

        val psiClass = project.getPsiClasses().first()
        val newFiled = JavaPsiFacade.getElementFactory(project).createFieldFromText(fieldString, psiClass)
        val newMethod = JavaPsiFacade.getElementFactory(project).createMethodFromText(methodString, psiClass)

        val lastFiled = psiClass.fields.last()
        val lastMethod = psiClass.methods.last { it.hasAnotation(VALIDATION_METHOD_NAME) }

        psiClass.addAfter(newFiled, lastFiled)
        psiClass.addAfter(newMethod, lastMethod)
    }

}
