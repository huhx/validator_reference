package com.huhx.reference.inspection

import com.huhx.reference.constant.Constant
import com.huhx.reference.extension.getPsiClasses
import com.huhx.reference.extension.hasAnotation
import com.huhx.reference.setting.AppSettingsState
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.util.PsiNavigateUtil
import org.apache.commons.lang3.StringUtils
import java.util.*

class MethodQuickFix(private val filedType: String, private val filedName: String) : LocalQuickFix {

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
            element.replace(elementFactory.createIdentifier(name))
        }

        val psiClass = project.getPsiClasses().first()
        val methodAndFiled = MethodAndFiled(type = filedType, name = name)

        val newFiled = elementFactory.createFieldFromText(methodAndFiled.getField(), psiClass)
        psiClass.addAfter(newFiled, psiClass.fields.last())

        val newMethod = elementFactory.createMethodFromText(methodAndFiled.getMethod(), psiClass)
        psiClass.addAfter(newMethod, psiClass.methods.last { it.hasAnotation(Constant.VALIDATION_METHOD_NAME) })

        val last = psiClass.methods.last { it.hasAnotation(Constant.VALIDATION_METHOD_NAME) }
        PsiNavigateUtil.navigate(last, true)
    }

}
