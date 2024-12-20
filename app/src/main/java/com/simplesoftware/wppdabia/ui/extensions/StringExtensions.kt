package com.simplesoftware.wppdabia.ui.extensions

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun String.getInitials(): String {
    val names = this.split(" ")
    var initials = ""
    var letterNumbers = 0

    if (names.size > 1) {
        initials = names.mapNotNull { it.firstOrNull() }.joinToString("")
        letterNumbers = 2
    } else {
        initials = names.firstOrNull()?.substring(0).toString()
        letterNumbers = 1
    }
    return initials.uppercase().take(letterNumbers)
}

fun String.getFirstName(): String {
    return this.split(" ").firstOrNull() ?: this
}

fun Bitmap.toUri(context: Context): Uri {
    val file = File(context.cacheDir, "temp_image.jpg")
    FileOutputStream(file).use { output ->
        this.compress(Bitmap.CompressFormat.JPEG, 100, output)
    }
    return file.toUri()
}

fun handleTimeStamp(timestamp: String): String {
    val messageDate =
        SimpleDateFormat("HH:mm - dd/MM/yy", Locale.getDefault()).parse(timestamp) ?: Date()
    val currentDate = Date()

    val calendarMessage = Calendar.getInstance().apply {
        time = messageDate
    }
    val calendarToday = Calendar.getInstance().apply {
        time = currentDate
    }
    val calendarYesterday = Calendar.getInstance().apply {
        time = currentDate
        add(Calendar.DAY_OF_YEAR, -1)
    }

    val today = calendarMessage.get(Calendar.YEAR) == calendarToday.get(Calendar.YEAR) &&
            calendarMessage.get(Calendar.DAY_OF_YEAR) == calendarToday.get(Calendar.DAY_OF_YEAR)
    val yesterday =
        calendarMessage.get(Calendar.YEAR) == calendarYesterday.get(Calendar.YEAR) &&
                calendarMessage.get(Calendar.DAY_OF_YEAR) == calendarYesterday.get(Calendar.DAY_OF_YEAR)

    return when {
        today -> {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageDate)
        }

        yesterday -> {
            "Ontem"
        }

        else -> {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(messageDate)
        }
    }
}

fun String.getHourFromTimeStamp(): String {
    return this.split(" - ").firstOrNull() ?: this
}

fun String.getDateFromTimeStamp(): String {
    return this.split(" - ").lastOrNull() ?: this
}

fun String.ignoreLineBreak(): String {
    return this.replace("\\r?\\n".toRegex(), " ")
}