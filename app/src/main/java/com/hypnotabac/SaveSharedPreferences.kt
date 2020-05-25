package com.hypnotabac

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


object SaveSharedPreferences {
    private const val PREF_EMAIL = "email"
    private const val PREF_USER_TYPE = "usertype"
    private const val PREF_DARK_THEME = "darktheme"
    private const val PREF_USER_ID = "userID"
    private const val PREF_HYPNO_ID = "hypnoID"

    private fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun setEmail(ctx: Context?, userName: String?) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_EMAIL, userName)
        editor.apply()
    }

    fun getEmail(ctx: Context?): String {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "")!!
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

    fun setUserType(ctx: Context?, newType: String?) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_USER_TYPE, newType)
        editor.apply()
    }

    fun getUserType(ctx: Context?): String {
        return getSharedPreferences(ctx).getString(PREF_USER_TYPE, "")!!
    }

    fun setUserID(ctx: Context?, newID: String?) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_USER_ID, newID)
        editor.apply()
    }

    fun getUserID(ctx: Context?): String {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "")!!
    }

    fun setHypnoID(ctx: Context?, newHypnoID: String?) {
        val editor = getSharedPreferences(ctx).edit()
        editor.putString(PREF_HYPNO_ID, newHypnoID)
        editor.apply()
    }

    fun getHypnoID(ctx: Context?): String {
        return getSharedPreferences(ctx).getString(PREF_HYPNO_ID, "")!!
    }

    fun resetAll(ctx: Context?) {
        setEmail(ctx, "")
        setUserType(ctx, "")
        setUserID(ctx, "")
        setHypnoID(ctx, "")
    }
}