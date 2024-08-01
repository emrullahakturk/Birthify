package com.yargisoft.birthify

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.findFragmentById(R.id.mainFragmentHost) as NavHostFragment

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri tuşuna basıldığında yapılacak işlemler
                // Örneğin, bir önceki fragment'e geri gitmek:
                val navController = findNavController(R.id.mainFragmentHost)
                if (!navController.popBackStack()) {
                    // Eğer navController ile geri gidilecek bir yer yoksa, activity'i kapat
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

    }

}
