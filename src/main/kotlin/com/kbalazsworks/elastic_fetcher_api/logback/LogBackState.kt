package com.kbalazsworks.elastic_fetcher_api.logback

import org.springframework.stereotype.Service

@Service
class LogBackState {
    // A Kotlin automatikusan elkészíti a gettert és a settert
    var threadLocalLongTermLogState: ThreadLocal<String> = ThreadLocal.withInitial { "false" }

    fun setThreadLocalLongTermLogState(state: Boolean) {
        this.threadLocalLongTermLogState.set(state.toString())
    }
}
