package com.huhx.reference

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "com.huhx.reference.AppSettingsState",
    storages = [Storage("AppSettingsState.xml")]
)
class AppSettingsState : PersistentStateComponent<AppSettingsState> {
    var className: String = "MethodValidator"

    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): AppSettingsState = service()
    }
}
