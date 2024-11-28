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

        val percentRegex = "\\d+(\\.\\d+)?%".toRegex()
        percentRegex.findAll(resultText).forEach { match ->
            val startIndex = match.range.first
            val endIndex = match.range.last + 1
            // make it bold for percent numbers
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        }

        // Check if the result contains the word "normal"
        if (resultText.contains("normal", ignoreCase = true)) {
            // Find the start and end indices of the word "normal"
            val startIndex = resultText.indexOf("normal", ignoreCase = true)
            val endIndex = startIndex + "normal".length

            // Set the color to blue and make it bold
            spannable.setSpan(ForegroundColorSpan(blue), startIndex, endIndex, 0)
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        } else if (resultText.contains("immature cataract", ignoreCase = true)) {
            val startIndex = resultText.indexOf("immature cataract", ignoreCase = true)
            val endIndex = startIndex + "immature cataract".length

            spannable.setSpan(ForegroundColorSpan(yellow), startIndex, endIndex, 0)
            spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        } else if (resultText.contains("mature cataract", ignoreCase = true)) {
                val startIndex = resultText.indexOf("mature cataract", ignoreCase = true)
                val endIndex = startIndex + "mature cataract".length

                spannable.setSpan(ForegroundColorSpan(red), startIndex, endIndex, 0)
                spannable.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, 0)
        }

        // Set the styled text to the TextView
        return spannable
    }

    fun toPercentString(value: Float): String {
        return "%.2f".format(value * 100) + "%"
    }

//    fun setStyledTextWithPlaceholder(
//        context: Context,
//        textResource: Int,
//        percentage: String
//    ): Spanned {
//        val formattedText = String.format(context.getString(textResource), percentage)
//        val styledText = Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY)
//        return styledText
//    }
}