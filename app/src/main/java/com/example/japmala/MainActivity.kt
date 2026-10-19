package com.example.japmala

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.text.NumberFormat
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var mala: MalaView
    private lateinit var roundsText: TextView
    private lateinit var totalText: TextView
    private val nf = NumberFormat.getInstance(Locale.US)

    private var count = 0
    private var rounds = 0
    private var total = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("japmala", Context.MODE_PRIVATE)
        count = prefs.getInt("count", 0)
        rounds = prefs.getInt("rounds", 0)
        total = prefs.getLong("total", 0L)

        val dp = resources.displayMetrics.density
        fun px(v: Int) = (v * dp).toInt()

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#FFF8E1"))
            setPadding(px(20), px(32), px(20), px(20))
        }

        val title = TextView(this).apply {
            text = "Jap Mala"
            textSize = 28f
            setTextColor(Color.parseColor("#BF360C"))
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        root.addView(title, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        mala = MalaView(this)
        mala.setOnClickListener { increment() }
        root.addView(mala, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))

        roundsText = TextView(this).apply {
            textSize = 20f
            setTextColor(Color.parseColor("#4E342E"))
            gravity = Gravity.CENTER
        }
        totalText = TextView(this).apply {
            textSize = 16f
            setTextColor(Color.parseColor("#8D6E63"))
            gravity = Gravity.CENTER
        }
        root.addView(roundsText)
        root.addView(totalText)

        val buttons = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, px(12), 0, 0)
        }
        val undo = Button(this).apply {
            text = "Undo (-1)"
            setOnClickListener { decrement() }
        }
        val reset = Button(this).apply {
            text = "Reset"
            setOnClickListener { confirmReset() }
        }
        buttons.addView(undo, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        buttons.addView(reset, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(buttons)

        setContentView(root)
        refresh()
    }

    private fun increment() {
        count++
        total++
        if (count >= mala.total) {
            count = 0
            rounds++
            vibrate(500)
        } else {
            vibrate(25)
        }
        save()
        refresh()
    }

    private fun decrement() {
        if (count > 0) {
            count--
            if (total > 0) total--
        } else if (rounds > 0) {
            rounds--
            count = mala.total - 1
            if (total > 0) total--
        }
        save()
        refresh()
    }

    private fun confirmReset() {
        AlertDialog.Builder(this)
            .setTitle("Reset counter?")
            .setMessage("Count, malas and total chants will all be set to zero.")
            .setPositiveButton("Yes") { _, _ ->
                count = 0; rounds = 0; total = 0
                save(); refresh()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun refresh() {
        mala.count = count
        roundsText.text = "Malas completed: " + nf.format(rounds)
        totalText.text = "Total chants: " + nf.format(total)
    }

    private fun save() {
        prefs.edit()
            .putInt("count", count)
            .putInt("rounds", rounds)
            .putLong("total", total)
            .apply()
    }

    @Suppress("DEPRECATION")
    private fun vibrate(ms: Long) {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v.vibrate(ms)
        }
    }
}
