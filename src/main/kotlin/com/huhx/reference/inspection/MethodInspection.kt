package com.huhx.reference.inspection

import com.huhx.reference.constant.Constant.METHOD_VALIDATION_NAME
import com.huhx.reference.extension.hasReference
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.*

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

                if (METHOD_VALIDATION_NAME == annotation.qualifiedName) {
                    val parentElement = annotation.parent.parent
                    if (parentElement !is PsiField && parentElement !is PsiClass) return
                    val name = annotation.findAttributeValue("method")?.lastChild ?: return
                    if (annotation.project.hasReference(name.text)) return

                    val typeString = getTypeFromElement(parentElement)
                    holder.registerProblem(
                        name,
                        "Create validation filed and method",
                        ProblemHighlightType.ERROR,
                        MethodQuickFix(typeString, name.text)
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
}
