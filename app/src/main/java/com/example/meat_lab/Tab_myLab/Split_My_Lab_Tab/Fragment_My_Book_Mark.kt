package com.example.meat_lab.Tab_myLab.Split_My_Lab_Tab


import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log.e
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meat_lab.*

import com.example.meat_lab.Tab_openLab.item_note_list
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A simple [Fragment] subclass.
 */
class Fragment_My_Book_Mark : Fragment()
{



    var TAG: String = "Fragment_My_Note"

    var mContext: Context? = null

    var mRecyclerView: RecyclerView? = null
    var mList= arrayListOf<item_note_list>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_my_book_mark, container, false)



        mContext = requireContext()

        mRecyclerView = view.findViewById(R.id.book_mark_note_list) as RecyclerView
        mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)

        // todo: 나의 북마크 요리일지 불러오기
        getMyBookMarkNoteList()

        return view
    }

    // todo: 나의 북마크 요리일지 불러오기
    private fun getMyBookMarkNoteList()
    {
        e(TAG, "getMyBookMarkNoteList: 나의 요리 일지 불러오기")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val myNoteListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = myNoteListRequest.getMyBookMarkNoteList(Activity_Main.GET_ID.toString())

        listCall.enqueue(object: Callback<List<item_note_list>>
        {
            override fun onResponse(call: Call<List<item_note_list>>, response: Response<List<item_note_list>>)
            {
                e(TAG, "item_note_list call onResponse = 수신 받음. 결과: ${response.body()?.size}개")

                // todo: 화면 비율 구하기
                val displayMetrics = DisplayMetrics()
                activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
                displayMetrics.widthPixels // 핸드폰의 가로 해상도를 구함.
                displayMetrics.heightPixels // 핸드폰의 가로 해상도를 구함.
                e(TAG, "deviceWidth: ${displayMetrics.widthPixels}")
                e(TAG, "deviceHeight: ${displayMetrics.heightPixels }")
                e(TAG, "deviceWidth / 2: ${(displayMetrics.widthPixels / 2)}")

                mList = response.body() as ArrayList<item_note_list>
//                mRecyclerView!!.adapter = adapterNoteList(mContext, mList, (displayMetrics.widthPixels / 2))
                mRecyclerView!!.adapter = adapterLabNoteList(mContext, mList, displayMetrics.widthPixels, "myBookMark")
                mRecyclerView!!.adapter = mRecyclerView?.adapter
            }

            override fun onFailure(call: Call<List<item_note_list>>, t: Throwable)
            {
                e(TAG, "onFailure: t: " + t.message)
            }
        })
    }
}
