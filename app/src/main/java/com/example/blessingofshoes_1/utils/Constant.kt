package com.example.blessingofshoes_1.utils

import java.util.concurrent.Executors


private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

class Constant {

    companion object {
        val PREF_IS_LOGIN = "PREF_IS_LOGIN"
        val PREF_USERNAME = "PREF_USERNAME"
        val PREF_EMAIL = "PREF_EMAIL"
        val PREF_PASSWORD = "PREF_PASSWORD"
    }

}
fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}