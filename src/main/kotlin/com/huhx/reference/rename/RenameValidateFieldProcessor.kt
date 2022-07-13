package com.huhx.reference.rename

import com.huhx.reference.setting.AppSettingsState
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.refactoring.listeners.RefactoringElementListener
import com.intellij.refactoring.rename.RenamePsiElementProcessor
import com.intellij.usageView.UsageInfo
import org.apache.commons.lang3.StringUtils
import java.util.*

class RenameValidateFieldProcessor : RenamePsiElementProcessor() {

    override fun renameElement(element: PsiElement, newName: String, usages: Array<out UsageInfo>, listener: RefactoringElementListener?) {
        val name = if (!StringUtils.isAllUpperCase(newName)) {
            newName.uppercase(Locale.getDefault())
        } else {
            newName
        }
        super.renameElement(element, name, usages, listener)
    }

    override fun canProcessElement(element: PsiElement): Boolean {
        return element is PsiField && element.containingClass?.name == AppSettingsState.getInstance().className
    }
}
