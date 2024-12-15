package com.hgi.mergeimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint

class merge {
    fun combineImages(jpgPath: String, pngPath: String, outputPath: String) {
        // Load the JPG (background) image
        val background = BitmapFactory.decodeFile(jpgPath)?.copy(Bitmap.Config.ARGB_8888, true)
            ?: throw IllegalArgumentException("Failed to load JPG image")

        // Load the PNG (foreground) image
        val foreground = BitmapFactory.decodeFile(pngPath)
            ?: throw IllegalArgumentException("Failed to load PNG image")

        // Ensure both images are not null
        if (background == null || foreground == null) {
            throw IllegalStateException("Error loading images")
        }

        // Create a canvas to draw on the background
        val canvas = Canvas(background)

        // Draw the PNG on top of the JPG
        val paint = Paint()
        canvas.drawBitmap(foreground, 0f, 0f, paint) // You can adjust the position (x, y) here

        // Save the combined image to the output path
        val outputStream = java.io.FileOutputStream(outputPath)
        background.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        // Close the output stream
        outputStream.flush()
        outputStream.close()

        // Recycle bitmaps to free memory
        background.recycle()
        foreground.recycle()
    }

// Usage example
// combineImages(
//     jpgPath = "path/to/background.jpg",
//     pngPath = "path/to/foreground.png",
//     outputPath = "path/to/output.jpg"
// )

}