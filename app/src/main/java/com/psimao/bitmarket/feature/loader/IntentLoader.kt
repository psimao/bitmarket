package com.psimao.bitmarket.feature.loader

import android.content.Intent

internal object IntentLoader {

    private const val PACKAGE_NAME = "com.psimao.bitmarket"

    fun loadOrNull(className: String): Intent? =
        try {
            Class.forName(className).let { Intent(Intent.ACTION_VIEW).setClassName(PACKAGE_NAME, className) }
        } catch (e: ClassNotFoundException) {
            null
        }
}