package com.huhx.reference.action

import com.google.common.base.CaseFormat
import com.huhx.reference.constant.Constant.VALIDATION_METHOD_NAME
import com.huhx.reference.extension.getPsiClasses
import com.huhx.reference.extension.hasAnotation
import com.huhx.reference.extension.hasReference
import com.huhx.reference.setting.AppSettingsState
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiReferenceExpression
import com.intellij.util.PsiNavigateUtil
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
        if (editor == null) return
        val name = element.text.uppercase(Locale.getDefault())
        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(element.textRange.startOffset, element.textRange.endOffset, name)
        }

        val fieldString = """
            public static final String %s = "%s";
        """.trimIndent().format(name, name.replace("_", " ").lowercase(Locale.getDefault()))
        val methodString = """
              @ValidationMethod(%s)
              public static boolean %s(%s value) {
                // todo
                return false;
              }
        """.trimIndent().format(name, toCamelCase(name), "String")

        val psiClass = project.getPsiClasses().first()
        val newFiled = JavaPsiFacade.getElementFactory(project).createFieldFromText(fieldString, psiClass)
        val newMethod = JavaPsiFacade.getElementFactory(project).createMethodFromText(methodString, psiClass)

        val lastFiled = psiClass.fields.last()
        val lastMethod = psiClass.methods.last { it.hasAnotation(VALIDATION_METHOD_NAME) }

        psiClass.addAfter(newFiled, lastFiled)
        psiClass.addAfter(newMethod, lastMethod)

        val last = psiClass.methods.last() { it.hasAnotation(VALIDATION_METHOD_NAME) }
        PsiNavigateUtil.navigate(last, true)
    }

    private fun toCamelCase(string: String): String {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string)
    }

}
