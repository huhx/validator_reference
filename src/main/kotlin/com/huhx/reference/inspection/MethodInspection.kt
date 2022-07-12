package com.huhx.reference.inspection

import com.google.common.base.CaseFormat
import com.huhx.reference.constant.Constant
import com.huhx.reference.constant.Constant.METHOD_VALIDATION_NAME
import com.huhx.reference.extension.getPsiClasses
import com.huhx.reference.extension.hasAnotation
import com.huhx.reference.extension.hasReference
import com.huhx.reference.setting.AppSettingsState
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInspection.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.util.PsiNavigateUtil
import org.apache.commons.lang3.StringUtils
import java.util.*

class MethodInspection : AbstractBaseJavaLocalInspectionTool(), HighPriorityAction {
    override fun getDisplayName(): String {
        return "Create validation field and mehtod"
    }

    override fun getGroupDisplayName(): String {
        return "Compiler issues"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JavaElementVisitor() {

            override fun visitAnnotation(annotation: PsiAnnotation) {
                super.visitAnnotation(annotation)
                val qualifiedName: String? = annotation.qualifiedName
                if (METHOD_VALIDATION_NAME == qualifiedName) {
                    val parentElement = annotation.parent.parent
                    if (parentElement !is PsiField && parentElement !is PsiClass) {
                        return
                    }
                    val name = annotation.findAttributeValue("method")?.lastChild ?: return
                    val project = annotation.project
                    if (project.hasReference(name.text)) return

                    val type = getTypeFromElement(parentElement)
                    holder.registerProblem(
                        name,
                        "Create validation filed and method",
                        ProblemHighlightType.ERROR,
                        DeleteQuickFix(type, name.text)
                    )
                }
            }
        }
    }

    private fun getTypeFromElement(parentElement: PsiElement): String {
        if (parentElement is PsiField) {
            return parentElement.type.presentableText
        }

        if (parentElement is PsiClass) {
            return parentElement.name!!
        }
        return "String"
    }

    private class DeleteQuickFix(val string: String, val filedName: String) : LocalQuickFix {

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
}
