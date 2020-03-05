package com.example.meat_lab

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log

class Activity_Intro : AppCompatActivity()
{

    var handler: Handler? = null

    var TAG: String = "Activity_Intro"
    var nextIntent: Intent? = null
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 세로고정

        handler = Handler()
        var thread = ThreadClass()
        handler?.postDelayed(thread, 1000)

        nextIntent = Intent(this, Activity_Main::class.java)
    }

    inner class ThreadClass : Thread()
    {
        override fun run()
        {
            Log.e(TAG, "인트로 핸들러 시작")

            sessionManager = SessionManager(this@Activity_Intro)
            sessionManager!!.checkLogin()
        }
    }


    override fun onPause()
    {
        super.onPause()
        finish()
    }
}
