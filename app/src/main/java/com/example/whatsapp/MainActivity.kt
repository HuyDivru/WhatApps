package com.example.whatsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.whatsapp.activity.NumberActivity
import com.example.whatsapp.adapter.ViewPageAdapter
import com.example.whatsapp.databinding.ActivityMainBinding
import com.example.whatsapp.ui.CallFragment
import com.example.whatsapp.ui.ChatFragment
import com.example.whatsapp.ui.StatusFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val fragmentArrayList = ArrayList<Fragment>()
        fragmentArrayList.add(ChatFragment())
        fragmentArrayList.add(CallFragment())
        fragmentArrayList.add(StatusFragment())

        auth=FirebaseAuth.getInstance()
        if(auth.currentUser==null){
            startActivity(Intent(this,NumberActivity::class.java))
            finish()
        }

        val adapter =ViewPageAdapter(this,supportFragmentManager, fragmentArrayList)
        binding.viewPager.adapter=adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }
}