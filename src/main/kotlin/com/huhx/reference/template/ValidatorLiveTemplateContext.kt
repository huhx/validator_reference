package com.huhx.reference.template

import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

class ValidatorLiveTemplateContext : TemplateContextType("VALIDATOR", "Validator") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        return templateActionContext.file.name.endsWith(".java")
    }

}
