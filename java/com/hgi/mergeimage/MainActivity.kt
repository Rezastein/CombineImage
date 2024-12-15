package com.hgi.mergeimage

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hgi.mergeimage.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var bin : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bin = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bin.root)





        bin.btnclick.setOnClickListener {
            val jpgResId = R.drawable.bg // Ganti dengan resource JPG
            val pngResId = R.drawable.red// Ganti dengan resource PNG
            val outputPath = File(getExternalFilesDir(null), "output.jpg").absolutePath

            try {
                // Atur ukuran PNG (misalnya, 300x300 piksel)
                combineImagesFromDrawable(jpgResId, pngResId, outputPath, pngWidth = 300, pngHeight = 300)

                // Tampilkan hasil di ImageView
                val resultBitmap = BitmapFactory.decodeFile(outputPath)
                bin.imgview.setImageBitmap(resultBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }




    }
    private fun decodeSampledBitmapFromResource(resId: Int, reqWidth: Int, reqHeight: Int): Bitmap {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // Hanya membaca ukuran tanpa memuat gambar
        }
        BitmapFactory.decodeResource(resources, resId, options)

        // Hitung skala pengurangan
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
        options.inJustDecodeBounds = false // Memuat gambar sebenarnya

        return BitmapFactory.decodeResource(resources, resId, options)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Hitung nilai inSampleSize (skala)
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
    private fun combineImagesFromDrawable(
        jpgResId: Int, // Resource ID untuk JPG
        pngResId: Int, // Resource ID untuk PNG
        outputPath: String,
        pngWidth: Int, // Lebar yang diinginkan untuk PNG
        pngHeight: Int // Tinggi yang diinginkan untuk PNG
    ) {
        // Ukuran yang diinginkan untuk memuat gambar latar belakang
        val reqWidth = 1080
        val reqHeight = 1920

        // Load gambar latar belakang (JPG)
        val background = decodeSampledBitmapFromResource(jpgResId, reqWidth, reqHeight)
            ?.copy(Bitmap.Config.ARGB_8888, true)
            ?: throw IllegalArgumentException("Failed to load JPG resource")

        // Load gambar PNG
        val foreground = decodeSampledBitmapFromResource(pngResId, reqWidth, reqHeight)
            ?: throw IllegalArgumentException("Failed to load PNG resource")

        // Atur ukuran PNG ke ukuran yang diinginkan
        val scaledForeground = Bitmap.createScaledBitmap(foreground, pngWidth, pngHeight, true)

        // Canvas untuk menggambar
        val canvas = Canvas(background)
        val paint = Paint()

        // Posisi PNG (contoh: tengah layar)
        val xPosition = 0f
        val yPosition = 0f

        // Gambar PNG di atas JPG
        canvas.drawBitmap(scaledForeground, xPosition, yPosition, paint)

        // Simpan gambar hasil kombinasi
        val outputStream = FileOutputStream(outputPath)
        background.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Bebaskan memori
        background.recycle()
        foreground.recycle()
        scaledForeground.recycle()
    }


}