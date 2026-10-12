package com.anistudio.focusmode

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class BlockActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(8,10,20); window.navigationBarColor = Color.rgb(8,10,20)
        val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; gravity=Gravity.CENTER; setPadding(28,28,28,28); setBackgroundColor(Color.rgb(8,10,20)) }
        root.addView(TextView(this).apply { text="FOCUS MODE"; textSize=16f; setTextColor(Color.rgb(125,130,255)); gravity=Gravity.CENTER })
        root.addView(TextView(this).apply { text="Stay with your goal."; textSize=30f; setTextColor(Color.WHITE); gravity=Gravity.CENTER; setPadding(0,22,0,12) })
        root.addView(TextView(this).apply { text="This app is on your distraction list. Return to your focus session and keep going."; textSize=16f; setTextColor(Color.LTGRAY); gravity=Gravity.CENTER; setPadding(0,0,0,26) })
        root.addView(Button(this).apply { text="BACK TO FOCUS"; setOnClickListener { startActivity(Intent(this@BlockActivity, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish() } })
        setContentView(root)
    }
    @Deprecated("Deprecated in Android") override fun onBackPressed() { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish() }
}
