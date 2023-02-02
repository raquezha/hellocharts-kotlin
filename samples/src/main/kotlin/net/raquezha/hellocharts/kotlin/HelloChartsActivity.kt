package net.raquezha.hellocharts.kotlin

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

@Suppress("unused")
abstract class HelloChartsActivity : AppCompatActivity() {
    fun showToast(message: String) {
        Toast.makeText(
            this@HelloChartsActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}