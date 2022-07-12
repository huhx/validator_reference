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
import org.apache.commons.lang3.StringUtils
import java.util.*

class MethodValidateIntentionAction : PsiElementBaseIntentionAction(), IntentionAction {
    private val className = AppSettingsState.getInstance().className

    override fun getText(): String {
        return "Create related filed and method in '$className'"
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
        if (!StringUtils.isAllUpperCase(element.text)) {
            WriteCommandAction.runWriteCommandAction(project) {
                editor.document.replaceString(element.textRange.startOffset, element.textRange.endOffset, name)
            }
        }

        val psiClass = project.getPsiClasses().first()
        val elementFactory = JavaPsiFacade.getElementFactory(project)
        val newFiled = elementFactory.createFieldFromText(getFiledString(name), psiClass)
        val newMethod = elementFactory.createMethodFromText(getMethodString(name), psiClass)

        psiClass.addAfter(newFiled, psiClass.fields.last())
        psiClass.addAfter(newMethod, psiClass.methods.last { it.hasAnotation(VALIDATION_METHOD_NAME) })

        val last = psiClass.methods.last { it.hasAnotation(VALIDATION_METHOD_NAME) }
        PsiNavigateUtil.navigate(last, true)
    }

    private fun getFiledString(filedName: String): String {
        val filedValue = filedName.replace("_", " ").lowercase(Locale.getDefault())
        return """
            public static final String %s = "%s";
        """.trimIndent().format(filedName, filedValue)
    }

    private fun getMethodString(name: String): String {
        return """
                      @ValidationMethod(%s)
                      public static boolean %s(%s value) {
                        // todo
                        return false;
                      }
                """.trimIndent().format(name, toCamelCase(name), "String")
    }

    private fun toCamelCase(string: String): String {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string)
    }
}
