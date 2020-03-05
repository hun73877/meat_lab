package com.example.meat_lab

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.e
import android.widget.ImageView
import android.widget.TabHost
import com.example.meat_lab.Tab_myLab.Fragment_My_Lab
import com.example.meat_lab.Tab_myPage.Fragment_My_Page
import com.example.meat_lab.Tab_openLab.Fragment_Open_Lab
import kotlinx.android.synthetic.main.activity_main.*

class Activity_Main : AppCompatActivity()
{
    var TAG: String = "Activity_Main"

    companion object
    {
        var sessionManager: SessionManager? = null

        var GET_ID: String? = null
        var GET_NAME: String? = null
        var GET_TYPE: String? = null
        var GET_PHOTO: String? = null
    }

    var TabPsotion: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        e(TAG, "실행됨")

        // 세션 선언
        sessionManager = SessionManager(this)

        var user = sessionManager!!.userDetail
        GET_ID = user.get(SessionManager.ID)
        GET_NAME = user.get(SessionManager.NAME)
        GET_TYPE = user.get(SessionManager.TYPE)
        GET_PHOTO = user.get(SessionManager.PROFILEIMAGE)

        e(TAG, "접속한 유저 정보 불러오기")
        e(TAG, "GET_ID: $GET_ID")
        e(TAG, "GET_NAME: $GET_NAME")
        e(TAG, "GET_TYPE: $GET_TYPE")
        e(TAG, "GET_PHOTO: $GET_PHOTO")

        // todo: 탭호스트 설정하기
        tabhost.setup(this, supportFragmentManager, R.id.content)

        // todo: 오픈 연구소 / 프라이빗 랩 탭
        val tab01 = ImageView(this)
        tab01.setImageResource(R.drawable.tab_01)

        // 프라이빗 랩 탭 연결
        val tabSpec1 = tabhost.newTabSpec("프라이빗 랩") // 구분자
        tabSpec1.setIndicator(tab01)
        tabhost.addTab(tabSpec1, Fragment_Open_Lab::class.java, null)

        // todo: 나의 연구소 탭
        val tab02 = ImageView(this)
        tab02.setImageResource(R.drawable.tab_02)

        // 나의 연구소 탭 연결
        val tabSpec2 = tabhost.newTabSpec("나의 연구소")
        tabSpec2.setIndicator(tab02)
        tabhost.addTab(tabSpec2, Fragment_My_Lab::class.java!!, null)

        // todo: 마이페이지
        val tab04 = ImageView(this)
        tab04.setImageResource(R.drawable.tab_03)

        // 마이페이지 탭 연결
        val tabSpec3 = tabhost.newTabSpec("마이페이지")
        tabSpec3.setIndicator(tab04)
        tabhost.addTab(tabSpec3, Fragment_My_Page::class.java!!, null)

        TabPsotion = tabhost.getCurrentTab()
        e(TAG, "onTabChanged: TabPsotion: $TabPsotion")

        // 이미지 간격 줄이기
        var padding = 20
        for (i in 0 until tabhost.getTabWidget().getChildCount())
        {
            tabhost.getTabWidget().getChildAt(i).setPadding(padding, padding, padding, padding)
        }

        // 탭 배경색
        tabhost.getTabWidget().getChildAt(0).setBackgroundColor(Color.parseColor("#FFFFFF"))
        tabhost.getTabWidget().getChildAt(1).setBackgroundColor(Color.parseColor("#FFFFFF"))
        tabhost.getTabWidget().getChildAt(2).setBackgroundColor(Color.parseColor("#FFFFFF"))

        // 기본 탭 위치 지정
        tabhost.setCurrentTab(0)

        tabhost.setOnTabChangedListener(TabHost.OnTabChangeListener {
            // 탭 변경시 리스너
            TabPsotion = tabhost.getCurrentTab()
            e(TAG, "onTabChanged: TabPsotion: $TabPsotion")
        })
    }

    // 프래그먼트 뒤로가기. 0번 포지션에 있을때 뒤로가기 눌렀을때만 앱 종료하기
    override fun onBackPressed()
    {
        if (TabPsotion != 0)
        {
            e(TAG, "onBackPressed: 0번 포지션으로 이동합니다")
            tabhost.setCurrentTab(0)
        }
        else if (TabPsotion == 0)
        {
            e(TAG, "onBackPressed: 앱을 종료합니다")
            finish()
        }
    }
}
