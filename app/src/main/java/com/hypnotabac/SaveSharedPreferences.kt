package com.hypnotabac

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


object SaveSharedPreferences {
    private const val PREF_USER_NAME = "username"
    private const val PREF_USER_DESC = "description"
    private const val PREF_DARK_THEME = "darktheme"
    private fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun setEmail(ctx: Context?, userName: String?) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_USER_NAME, userName)
        editor.apply()
    }

    fun getEmail(ctx: Context?): String {
        return getSharedPreferences(ctx).getString(PREF_USER_NAME, "")!!
    }

    fun setDarkTheme(ctx: Context?, theme: Boolean) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putBoolean(PREF_DARK_THEME, theme)
        editor.apply()
    }

    fun isDarkTheme(ctx: Context?): Boolean {
        return getSharedPreferences(ctx)
            .getBoolean(PREF_DARK_THEME, false)
    }

    fun setUserDescription(ctx: Context?, newDesc: String?) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_USER_DESC, newDesc)
        editor.apply()
    }

    fun getUserDescription(ctx: Context?): String {
        return getSharedPreferences(ctx).getString(PREF_USER_DESC, "Add description...")!!
    }
}