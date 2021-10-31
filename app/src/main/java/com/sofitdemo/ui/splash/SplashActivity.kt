package com.sofitdemo.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils.loadAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sofitdemo.R
import com.sofitdemo.databinding.ActivitySplashBinding
import com.sofitdemo.ui.mainclass.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startAnimations()
        loadScreen()

    }

    private fun loadScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
             val loginIntent = Intent(this@SplashActivity, MainActivity::class.java)
             startActivity(loginIntent)
             finish()
        }, 3000)
    }

    private fun startAnimations() {
        var anim = loadAnimation(this, R.anim.alpha)
        anim.reset()
        binding.constraintLayout.clearAnimation()
        binding.constraintLayout.startAnimation(anim)
        anim = loadAnimation(this, R.anim.translate)
        anim.reset()
        binding.imageView.clearAnimation()
        binding.imageView.startAnimation(anim)
    }
}
