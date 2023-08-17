package com.sgamerapps.android.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.sgamerapps.android.R
import com.sgamerapps.android.databinding.ActivityStartBinding


class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.shareBtn.setOnClickListener {
            shareApp()
        }
        binding.startAppBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.rateBtn.setOnClickListener {
            rateApp()
        }

        binding.bannerImage.setOnClickListener {
            openLink()
        }
        binding.smallBanners.setOnClickListener { openLink() }
    }

    private fun shareApp() {
        var shareString = getString(R.string.referral_share)
        shareString =
            "https://play.google.com/store/apps/details?id=$packageName\n \n$shareString\n\n"
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareString)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    private fun rateApp() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }

    }

    private fun openLink(){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(getString(R.string.promotion_link)))
    }
}