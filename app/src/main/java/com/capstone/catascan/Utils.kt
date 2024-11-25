package com.capstone.catascan

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
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

    fun setAndGetTextAndSetColorText(context: Context, text: String, confidence: String, rColor: Int): SpannableString {
        val info: String = when (text) {
            "immature cataract" -> {
                context.getString(R.string.info_immature)
            }
            "mature cataract" -> {
                context.getString(R.string.info_mature)
            }
            else -> {
                context.getString(R.string.infor_normal)
            }
        }

        // Get the base string
        val baseText = context.getString(R.string.result, text, confidence, info)

        val spannable = SpannableString(baseText)

        // Apply color to the 'text' part
        val startIndexText = baseText.indexOf(text)
        val endIndexText = startIndexText + text.length
        val colorText = ContextCompat.getColor(context, rColor) // Define color for 'text' in colors.xml
        val colorSpanText = ForegroundColorSpan(colorText)
        spannable.setSpan(colorSpanText, startIndexText, endIndexText, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Apply color to the 'confidence' part
        val startIndexConfidence = baseText.indexOf(confidence)
        val endIndexConfidence = startIndexConfidence + confidence.length
        val colorConfidence = ContextCompat.getColor(context, rColor) // Define color for 'confidence' in colors.xml
        val colorSpanConfidence = ForegroundColorSpan(colorConfidence)
        spannable.setSpan(colorSpanConfidence, startIndexConfidence, endIndexConfidence, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // return the SpannableString to the TextView
        return spannable
    }


    fun toPercentString(value: Float): String {
        return "%.2f".format(value * 100) + "%"
    }
}