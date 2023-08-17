package com.sgamerapps.android.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.sgamerapps.android.R


class UpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)


        var updateBtn=findViewById<Button>(R.id.updateBtn)

        updateBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+application.packageName))
            startActivity(browserIntent)
        }


    }
}