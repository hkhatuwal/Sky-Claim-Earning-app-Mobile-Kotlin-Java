package com.sgamerapps.android.fragments

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.sgamerapps.android.R
import com.sgamerapps.android.activity.CaptchaActivity
import com.sgamerapps.android.activity.MainActivity
import com.sgamerapps.android.activity.QuizActivity
import com.sgamerapps.android.activity.ScratchActivity
import com.sgamerapps.android.activity.SpinActivity
import com.sgamerapps.android.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)



        binding.quiz.setOnClickListener {
            openLink()
        }
        binding.smallBanners.setOnClickListener { openLink() }
        binding.smallBanners2.setOnClickListener { openLink() }

        binding.spinCard.setOnClickListener {
            var mainActivity = activity as MainActivity
            startActivity(Intent(requireContext(), SpinActivity::class.java))
        }
        binding.scratchCard.setOnClickListener {
            startActivity(Intent(requireContext(), ScratchActivity::class.java))
        }

        binding.quizCard.setOnClickListener {
            startActivity(Intent(requireContext(), QuizActivity::class.java))

        }
        binding.captchCard.setOnClickListener {
            startActivity(Intent(requireContext(), CaptchaActivity::class.java))

        }
        binding.telegram.setOnClickListener {
            goTo("https://t.me/twistwallet")
        }
        binding.rating.setOnClickListener {
            goTo("https://play.google.com/store/apps/details?id=" + requireActivity().packageName)
        }
        binding.invite.setOnClickListener {

            (activity as MainActivity).changeFragment(R.id.referFragment)
//            val sendIntent = Intent().apply {
//                action = Intent.ACTION_SEND
//                putExtra(Intent.EXTRA_SUBJECT, "\uD83D\uDD25Daily Loot\uD83D\uDD25!")
//                putExtra(
//                    Intent.EXTRA_TEXT, "🔥Daily Loot🔥\n" +
//                            "Minimum withdraw 1 Rupee😀\n" +
//                            "Instant Payment😍\n" +
//                            "\n" +
//                            "Check out this awesome app:\n\n https://play.google.com/store/apps/details?id=" + requireActivity().packageName
//                )
//                type = "text/plain"
//            }
//            startActivity(Intent.createChooser(sendIntent, "Invite Friends"))
        }
        binding.contact.setOnClickListener {
            try {
                val telegram = Intent(ACTION_VIEW, Uri.parse("https://t.me/msgamer_5"))
                startActivity(telegram)
            } catch (ex: Exception) {
                val myToast = Toast.makeText(
                    context,
                    "Download telegram from play store...",
                    Toast.LENGTH_LONG
                )
                myToast.show()
            }
        }
        return binding.root
    }

    fun goTo(myString: String) {
        val url = myString
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
    private fun openLink(){
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(getString(R.string.promotion_link)))
    }

}