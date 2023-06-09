package com.teamdefine.farmapp.app.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Utility {
    fun Context.toast(message: String) {
        Toast.makeText(
            this, message,
            if (message.length <= 25) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
        ).show()
    }

    fun Fragment.toast(msg: String) {
        requireContext().toast(msg)
    }

    fun Context.loadImageUsingGlide(imageUrl: String, toLoadIv: AppCompatImageView) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
        Glide.with(this).load(imageUrl).apply(options).into(toLoadIv)
    }

    fun Fragment.loadImageUsingGlide(imageUrl: String, toLoadIv: AppCompatImageView) {
        requireContext().loadImageUsingGlide(imageUrl, toLoadIv)
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val dayOfMonth = currentDate.dayOfMonth
        val formatter =
            DateTimeFormatter.ofPattern("d'${getDayOfMonthSuffix(dayOfMonth)}' MMMM, yyyy")

        return currentDate.format(formatter)
    }

    private fun getDayOfMonthSuffix(day: Int): String {
        return when (day) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
    }

    fun Context.updateLocale(language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//            return updateResourcesLocale(this, locale)
//        }

        return updateResourcesLocaleLegacy(this, locale)
    }

    fun Fragment.updateLocale(language: String): Context {
        return requireContext().updateLocale(language)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context {
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}