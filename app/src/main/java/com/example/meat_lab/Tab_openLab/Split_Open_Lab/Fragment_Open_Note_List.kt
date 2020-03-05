package com.example.meat_lab.Tab_openLab.Split_Open_Lab


import android.content.Context
import android.os.Bundle
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meat_lab.R
import com.example.meat_lab.Tab_openLab.item_note_list
import com.example.meat_lab.adapterLabNoteList

//class Fragment_Open_Note_List : Fragment()
class Fragment_Open_Note_List(  // 프래그먼트 실행될 때 필요한
    var mList: ArrayList<item_note_list>,   // 1. 레시피 목록
    var tabName: String,                    // 2. 탭 이름
    var displaywidthPixels: Int             // 3. 디바이스 스크린 가로 사이즈
                             ) : Fragment()
{
    val TAG = "Fragment_Open_Note_(${tabName})"
    var mContext: Context? = null

    var mRecyclerView: RecyclerView? = null
    var open_category_thumb: ImageView? = null
    var open_category_name: TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_open_note, container, false)

        mRecyclerView = view.findViewById(R.id.open_note_list) as RecyclerView
        open_category_thumb = view.findViewById(R.id.open_category_thumb)
        open_category_name = view.findViewById(R.id.open_category_name)

        if (tabName == "돼지")
        {
            open_category_thumb?.setImageResource(R.drawable.ic_pig)
            open_category_name?.text = "돼지 (${mList.size})"
        }
        else if (tabName == "소")
        {
            open_category_thumb?.setImageResource(R.drawable.ic_cow)
            open_category_name?.text = "소 (${mList.size})"
        }
        else if (tabName == "닭")
        {
            open_category_thumb?.setImageResource(R.drawable.ic_chicken)
            open_category_name?.text = "닭 (${mList.size})"
        }
        else if (tabName == "연어")
        {
            open_category_thumb?.setImageResource(R.drawable.ic_salmon)
            open_category_name?.text = "연어 (${mList.size})"
        }
        else
        {
            open_category_name?.text = "${tabName}요리 연구 일지 (${mList.size})"
        }

        e(TAG, "?? 옴?")

        mContext = requireContext()
        mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)
        mRecyclerView!!.adapter = adapterLabNoteList(mContext, mList, displaywidthPixels, "openNote")
        mRecyclerView!!.adapter = mRecyclerView?.adapter
        mRecyclerView!!.adapter!!.notifyDataSetChanged()

        return view
    }
}



/*
        if (tabName == "전체")
        {
            e(TAG, "레시피 ${mList.size}개")
        }
        else
        {
            e(TAG, "레시피 ${includeThisCategoryIndex.size}개")
        }
*/
