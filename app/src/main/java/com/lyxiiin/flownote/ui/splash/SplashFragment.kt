package com.lyxiiin.flownote.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lyxiiin.flownote.R

class SplashFragment : Fragment(R.layout.fragment_splash){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.postDelayed({
            if (isAdded){
                findNavController().navigate(R.id.action_splash_to_home)
            }
        },1500L)
    }
}
