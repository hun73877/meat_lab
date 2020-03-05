package com.example.meat_lab.Tab_myLab

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.meat_lab.Activity_Main.Companion.GET_NAME
import com.example.meat_lab.Activity_Main.Companion.GET_PHOTO

import com.example.meat_lab.R
import com.example.meat_lab.Tab_myLab.Split_My_Lab_Tab.Fragment_My_Book_Mark
import com.example.meat_lab.Tab_myLab.Split_My_Lab_Tab.Fragment_My_Note
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

/**
 * A simple [Fragment] subclass.
 */
class Fragment_My_Lab : Fragment()
{
    var TAG: String = "Fragment_My_Lab"
    var mContext: Context? = null
    var button_write_note: ImageView? = null

    var lab_fragment_tab: TabLayout? = null
    var lab_fragment_pager: ViewPager? = null

    var my_profile_name: TextView? = null
    var my_profile_image: CircleImageView? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_my_lab, container, false)

        e(TAG,"onCreateView: ")

        mContext = requireContext()

        // viewFind
        button_write_note = view.findViewById(R.id.button_write_note)
        lab_fragment_tab = view.findViewById(R.id.my_lab_tab)
        lab_fragment_pager = view.findViewById(R.id.my_lab_pager)
        my_profile_name = view.findViewById(R.id.my_profile_name)
        my_profile_image = view.findViewById(R.id.my_profile_image)

        my_profile_name!!.text = GET_NAME

        Picasso.get().load(GET_PHOTO)
            .placeholder(R.drawable.logo_2) //.resize(300,300)       // 300x300 픽셀
            .centerCrop().fit().into(my_profile_image)

        // 글 작성 액티비티로 이동
        button_write_note?.setOnClickListener(View.OnClickListener {
            startActivity(Intent(mContext, Activity_Write_Lab_Note::class.java))
        })

        // 뷰페이저 어댑터 연결
        val fragmentAdapter = privateLabPager(childFragmentManager)
        lab_fragment_pager?.adapter = fragmentAdapter

        // 탭레이아웃 연결
        lab_fragment_tab?.setupWithViewPager(lab_fragment_pager)

        // 탭, 탭 제목 추가
        lab_fragment_tab?.getTabAt(0)?.setText("My Lab Note")
        lab_fragment_tab?.getTabAt(1)?.setText("Book Mark")

        // 페이지 리스너
        lab_fragment_tab?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
        {
            override fun onTabReselected(tab: TabLayout.Tab?)
            {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?)
            {
            }

            override fun onTabSelected(tab: TabLayout.Tab?)
            {
            }
        })

        return view
    } // End onCreateView


    // 뷰페이저 어댑터
    inner class privateLabPager : FragmentPagerAdapter
    {
        // 프래그먼트 연결
        var data1: Fragment = Fragment_My_Note()
        var data2: Fragment = Fragment_My_Book_Mark()

        // 리스트에 프래그먼트 담기
        var mData: ArrayList<Fragment> = arrayListOf(data1, data2)

        constructor(fm: FragmentManager) : super(fm)
        {

        }

        override fun getItem(position: Int): Fragment
        {
            return mData.get(position)
        }

        override fun getCount(): Int
        {
            return mData.size
        }
    }
}
