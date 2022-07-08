package com.huhx.reference.setting

import com.intellij.openapi.options.Configurable
import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.panel
import javax.swing.JComponent
import javax.swing.JTextField

class ApplicationSettingsConfigurable : Configurable {
    private val state = AppSettingsState.getInstance()
    private val pathField = JTextField("")

    override fun createComponent(): JComponent {
        pathField.text = state.className

        return panel {
            noteRow("Please add MethodValidator class")
            row {
                pathField(CCFlags.grow)
            }
        }
    }

    override fun isModified(): Boolean {
        return state.className != pathField.text
    }

    override fun apply() {
        state.className = pathField.text
    }

    override fun getDisplayName(): String {
        return "MethodNavigator"
    }

    override fun reset() {
        pathField.text = state.className
    }
}
