package com.huhx.reference.inspection

import com.google.common.base.CaseFormat
import com.huhx.reference.constant.Constant
import com.huhx.reference.extension.getPsiClasses
import com.huhx.reference.extension.hasAnotation
import com.huhx.reference.setting.AppSettingsState
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.util.PsiNavigateUtil
import org.apache.commons.lang3.StringUtils
import java.util.*

class MethodQuickFix(private val string: String, private val filedName: String) : LocalQuickFix {

    override fun getName(): String {
        return "Create field '$filedName' and method in '${AppSettingsState.getInstance().className}'"
    }

    override fun getFamilyName(): String {
        return name
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        val elementFactory = JavaPsiFacade.getElementFactory(project)
        val name = element.text.uppercase(Locale.getDefault())

        if (!StringUtils.isAllUpperCase(element.text)) {
            WriteCommandAction.runWriteCommandAction(project) {
                element.replace(elementFactory.createIdentifier(name))
            }
        }
        val psiClass = project.getPsiClasses().first()
        val newFiled = elementFactory.createFieldFromText(getFiledString(name), psiClass)
        val newMethod = elementFactory.createMethodFromText(getMethodString(string, name), psiClass)

        psiClass.addAfter(newFiled, psiClass.fields.last())
        psiClass.addAfter(newMethod, psiClass.methods.last { it.hasAnotation(Constant.VALIDATION_METHOD_NAME) })

        val last = psiClass.methods.last { it.hasAnotation(Constant.VALIDATION_METHOD_NAME) }
        PsiNavigateUtil.navigate(last, true)

    }

    private fun getFiledString(filedName: String): String {
        val filedValue = filedName.replace("_", " ").lowercase(Locale.getDefault())
        return """
            public static final String %s = "%s";
        """.trimIndent().format(filedName, filedValue)
    }

    private fun getMethodString(type: String, name: String): String {
        return """
                      @ValidationMethod(%s)
                      public static boolean %s(%s value) {
                        // todo
                        return false;
                      }
                """.trimIndent().format(name, toCamelCase(name), type)
    }

    private fun toCamelCase(string: String): String {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, string)
    }
}
