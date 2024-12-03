package com.capstone.catascan

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun getBitmapFromUri(contentResolver: ContentResolver, uri: Uri): Bitmap {
        val imageBitmap: Bitmap
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            @Suppress("DEPRECATION")
            imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            imageBitmap = ImageDecoder.decodeBitmap(source)
        }
        return imageBitmap
    }

    fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    fun formatDateTime(dateTimeString: String): String {
        val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("MMMM dd, yyyy, 'at' h:mm a", Locale.getDefault())
        val date = inputFormatter.parse(dateTimeString)!!
        return outputFormatter.format(date)
    }

    fun resultCustomStyling(context: Context, resultTemplate: String, vararg args: String): Spannable {
        val resultText = if (args.isEmpty()) resultTemplate else String.format(resultTemplate, *args)

        // Create a SpannableString from resultText
        val spannable = SpannableString(resultText)


        val yellow = ContextCompat.getColor(context, R.color.yellow)
        val red = ContextCompat.getColor(context, R.color.red)
        val blue = ContextCompat.getColor(context, R.color.blue)

        val percentRegex = "\\(confidence score : \\d+(\\.\\d+)?%\\)".toRegex()
        percentRegex.findAll(resultText).forEach { match ->
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            // make it bold for percent numbers
            spannable.setSpan(StyleSpan(Typeface.BOLD_ITALIC), startIndex, endIndex, 0)
        }

        // Apply styling for every occurrence of "normal"
        "normal".toRegex(RegexOption.IGNORE_CASE).findAll(resultText).forEach { match ->
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            // Set the color to blue and make it bold
            spannable.setSpan(ForegroundColorSpan(blue), startIndex, endIndex, 0)
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        }


        // Apply styling for every occurrence of "mature cataract"
        "mature cataract".toRegex(RegexOption.IGNORE_CASE).findAll(resultText).forEach { match ->
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            // Set the color to red and make it bold
            spannable.setSpan(ForegroundColorSpan(red), startIndex, endIndex, 0)
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        }

        // Apply styling for every occurrence of "immature cataract"
        "immature cataract".toRegex(RegexOption.IGNORE_CASE).findAll(resultText).forEach { match ->
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            // Set the color to yellow and make it bold
            spannable.setSpan(ForegroundColorSpan(yellow), startIndex, endIndex, 0)
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        }

        // Set the styled text to the TextView
        return spannable
    }

    fun toPercentString(value: Float): String {
        return "%.2f".format(value * 100) + "%"
    }

    fun setFullScreen(window: Window) {
        @Suppress("DEPRECATION")
        window.apply {
            statusBarColor = android.graphics.Color.TRANSPARENT
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    fun setLoading(progressBar: ProgressBar, isLoading: Boolean) {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun setBtnRetryAndErrorMsg(btn: Button, errorTv: TextView, msg: String) {
        if (msg.isNotBlank()) {
            btn.visibility = View.VISIBLE
            errorTv.visibility = View.VISIBLE
        } else {
            btn.visibility = View.GONE
            errorTv.visibility = View.GONE
        }
    }
}