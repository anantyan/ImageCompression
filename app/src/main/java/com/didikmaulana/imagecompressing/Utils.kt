package com.didikmaulana.imagecompressing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

fun Bitmap.toBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()

    return "data:image/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun Bitmap.getOrientation(bitmap: Bitmap, width: Int, height: Int): Bitmap {
    if(width>height) {
        bitmap.resize(512, 387)
    } else {
        bitmap.resize(288, 512)
    }
    return this
}

fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    if (maxHeight > 0 && maxWidth > 0) {
        val width = this.width
        val height = this.height
        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight
        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }
        return Bitmap.createScaledBitmap(this, finalWidth, finalHeight, true)
    } else {
        return this
    }
}

fun String.toBitmap(): Bitmap {
    val decodedString = Base64.decode(split(",")[1], Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
}

fun Bitmap.savePath(): FileOutputStream {
    val str_path: String = Environment.getExternalStorageDirectory().absolutePath+"/File Percobaan"
    val dir_path = File(str_path)
    if(!dir_path.exists()) {
        dir_path.mkdirs()
    }
    val file = File(dir_path, generateName()+".jpg")
    val fout = FileOutputStream(file)
    return fout
}

fun Bitmap.generateName(): String {
    val formatter = SimpleDateFormat("yyyyddMMHHmmss");
    val date = Date();
    return formatter.format(date)
}