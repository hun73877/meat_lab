package com.example.meat_lab.Tab_openLab


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.fragment.app.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.meat_lab.Activity_Main.Companion.GET_ID
import com.example.meat_lab.Activity_Main.Companion.GET_NAME
import com.example.meat_lab.Activity_Main.Companion.GET_PHOTO
import com.example.meat_lab.ApiClient
import com.example.meat_lab.ApiInterface
import com.example.meat_lab.R
import com.example.meat_lab.Tab_openLab.Split_Open_Lab.Fragment_Open_Note_List
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass.
 */
class Fragment_Open_Lab : Fragment()
{
    /** todo: Fragment 카테고리 Pager 생성하는 방법
    1. 서버에서 요리 일지 목록을 받는다.
    2. 예를 들어 '돼지고기'를 사용한 레시피는 '돼지고기 요리목록' Fragment를 생성 후 해당 Fragment로 요리 목록을 보낸다.
    3...
     */

    // 일지 일찌... for - 퍼ㄹ

    companion object
    {
        var HANDLER_OPEN_LAB: Handler? = null
    }

    var TAG: String = "Fragment_Open_Lab"
    var mContext: Context? = null

    var openLab_fragment_tab: TabLayout? = null
    var openLab_fragment_pager: ViewPager? = null

    // 프래그먼트 리스트
    var FragmentList = ArrayList<Fragment>()

    // 요리 일지 목록
    var mList = arrayListOf<item_note_list>()

    var isRefresh: Boolean = false // _: 스네티크 표기법 is...등 줄임 단어: 헝가리안 표기법

    var open_profile_name: TextView? = null
    var open_profile_image: CircleImageView? = null

    // onCreateView 에서 어떤 절차가 있는지 적자
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View?
    {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_fragment_open_lab, container, false)

        e(TAG, "onCreateView: ")

        mContext = requireContext()

        // viewFind
        openLab_fragment_tab = view.findViewById(R.id.open_lab_tab)
        openLab_fragment_pager = view.findViewById(R.id.open_lab_pager)

        open_profile_name = view.findViewById(R.id.open_profile_name)
        open_profile_image = view.findViewById(R.id.open_profile_image)

        open_profile_name!!.text = GET_NAME

        Picasso.get().load(GET_PHOTO)
            .placeholder(R.drawable.logo_2) //.resize(300,300)       // 300x300 픽셀
            .centerCrop().fit().into(open_profile_image)


        // todo: 공개 노트 불러오기 // 서버라는 언급이 없음 (주어). 의미없는 소스입니다.
        getOpenNote()

        HANDLER_OPEN_LAB = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                super.handleMessage(msg)

                // 조건문 앞엔 무조건 상수가 있어야 한다. (중요)
                if (msg.arg1 == 799)
                {
                    e(TAG, "handleMessage: msg.obj: ${msg.obj}")

                    if (msg.obj.equals("change_book_mark"))
                    {
                        e(TAG, "change_book_mark: 수신받음")

                        FragmentList.clear()

                        isRefresh = true

                        // todo: 공개 노트 불러오기
//                        getOpenNote()
                    }
                }
            }
        }

        return view
    } // End onCreateView

    val tabNameList: MutableList<item_tab_name_list> = ArrayList()

//    var fragmentAdapter: FragmentPagerAdapter? = null

    // todo: 공개 노트 불러오기
    private fun getOpenNote()
    {

        e(TAG, "공개 노트 불러오기: ")

        //building retrofit object
        val retrofit =
            Retrofit.Builder().baseUrl(ApiClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()

        //Defining retrofit api service
        val myNoteListRequest = retrofit.create(ApiInterface::class.java)

        //defining the call
        val listCall = myNoteListRequest.getOpenNoteList(GET_ID.toString())

        listCall.enqueue(object : Callback<List<item_note_list>>
        {
            override fun onResponse(
                call: Call<List<item_note_list>>,
                response: Response<List<item_note_list>>
                                   )
            {
                e(
                    TAG,
                    "open lab item_note_list call onResponse = 수신 받음. 결과: ${response.body()?.size}개"
                 )

                // 응답받은 레시피 노트를 리스트에 담기
                mList = response.body() as ArrayList<item_note_list>
                FragmentList.clear()
                tabNameList.clear()

                // todo: 뷰페이저 세팅
                activationViewPager()
            }

            override fun onFailure(call: Call<List<item_note_list>>, t: Throwable)
            {
                e(TAG, "onFailure: t: " + t.message)
            }
        })
    }

    // todo: 뷰페이저 세팅
    private fun activationViewPager()
    {
        var porkCount = 0
        var beefCount = 0
        var chickenCount = 0
        var salmonCount = 0
        var anotherMeatCount = 0

        // 돼지, 소, 닭, 연어 각각의 고기가 사용된 레시피의 개수 세기
        for (i in mList.indices)
        {
            e(TAG, "list_meats[${i}]: ${mList.get(i).mlab_list_meats}")

            if (mList.get(i).mlab_list_meats.contains("돼지"))
            {
                porkCount = (porkCount + 1)
            }

            if (mList.get(i).mlab_list_meats.contains("소"))
            {
                beefCount = (beefCount + 1)
            }

            if (mList.get(i).mlab_list_meats.contains("닭"))
            {
                chickenCount = (chickenCount + 1)
            }

            if (mList.get(i).mlab_list_meats.contains("연어"))
            {
                salmonCount = (salmonCount + 1)
            }

            if (mList.get(i).mlab_list_meats.contains("anotherMeats"))
            {
                anotherMeatCount = (anotherMeatCount + 1)
            }
        }

        e(TAG, "porkCount: ${porkCount}")
        e(TAG, "beefCount: ${beefCount}")
        e(TAG, "chickenCount: ${chickenCount}")
        e(TAG, "salmonCount: ${salmonCount}")
        e(TAG, "anotherMeats: ${anotherMeatCount}")

        // 카테고리 화면 이름과 각 카테고리에 포함되는 레시피의 개수를 리스트에 기록하기
        tabNameList.add(item_tab_name_list("전체", mList.size)) // 전체 탭은 인덱스 무시하기
        tabNameList.add(item_tab_name_list("돼지", porkCount))
        tabNameList.add(item_tab_name_list("소", beefCount))
        tabNameList.add(item_tab_name_list("닭", chickenCount))
        tabNameList.add(item_tab_name_list("연어", salmonCount))
        tabNameList.add(item_tab_name_list("기타", anotherMeatCount))

        // 프래그먼트 화면으로 전달할 디바이스의 가로 해상도를 구함.
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels

        // 정렬 코드. 레시피 개수가 많은 순으로 리스트 정렬하기
//                val sortedList = tabNameList.sortedWith(compareBy({ it.age }, { it.name })) // 정렬 기준을 여러개 지정 할수도 있다.
//                var sortedList = tabNameList.sortedWith(compareBy({ it.noteCount })) // 오름 차순
        var sortedList = tabNameList.sortedWith(compareBy({ -it.noteCount })) // 내림 차순


        // 프래그먼트 화면 생성하기
        for (i in sortedList.indices)
        {
            e(
                TAG,
                "sortedList[${i}]: ${sortedList.get(i).noteCount} / ${sortedList.get(i).tabName}"
             )

            // '전체'카테고리 화면 먼저 생성하기
            if (sortedList.get(i).tabName == "전체")
            {
                // 탭 추가
                FragmentList.add(
                    Fragment_Open_Note_List(
                        mList,
                        sortedList.get(i).tabName,
                        displayMetrics.widthPixels
                                           )
                                ) // 해당 탭에 띄울 리사이클러뷰
            }
            else
            {
                // 각 카테고리 화면에서 사용할 목록 새로 만들기.
                // ex) '고기'카테고리는 고기가 포함된 레시피노트 담기
                val filteredList = mList.filter {
                    it.mlab_list_meats.contains(sortedList.get(i).tabName) // it.mlab_list_meats.equals(result[j])
                }

                // 탭 추가
                FragmentList.add(
                    Fragment_Open_Note_List(
                        filteredList as ArrayList<item_note_list>, // 해당 탭에 띄울 리사이클러뷰
                        sortedList.get(i).tabName,                 // 카테고리 이름 입력
                        displayMetrics.widthPixels                 // 리사이클러뷰 사용할 때 필요한 화면 사이즈를 알리기
                                           )
                                )
            }
        }

        // 뷰페이저 어댑터 연결
        var fragmentAdapter = OpenLabPager(FragmentList, childFragmentManager)
        openLab_fragment_pager?.adapter = fragmentAdapter

        // 뷰페이저 갱신
        openLab_fragment_pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
        {
            override fun onPageScrollStateChanged(state: Int)
            {
                e(TAG, "onPageScrollStateChanged: state: $state")
                // 페이지 상태변화 감지하기
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
                                       )
            {
                if (isRefresh)
                {
                    isRefresh = false
                    /*activationViewPager()*/
                    openLab_fragment_pager!!.adapter!!.notifyDataSetChanged()

                    // 탭에 카테고리 이름 세팅하기
                    for (i in tabNameList.indices)
                    {
                        openLab_fragment_tab!!.getTabAt(i)?.setText(sortedList.get(i).tabName)
                    }

                    e(TAG, "목록 갱신하기")
                }
            }

            override fun onPageSelected(position: Int)
            {
                // 현재 몇 페이지인지 감지하는 부분
                e(TAG, "onPageSelected: position: $position")
            }
        })

        // 탭레이아웃 연결
        openLab_fragment_tab?.setupWithViewPager(openLab_fragment_pager)
        openLab_fragment_pager?.adapter?.notifyDataSetChanged()

        // 탭에 카테고리 이름 세팅하기
        for (i in tabNameList.indices)
        {
            openLab_fragment_tab!!.getTabAt(i)?.setText(sortedList.get(i).tabName)
        }
    }

    // 뷰페이저 어댑터 // FragmentPagerAdapter
    inner class OpenLabPager(var list: List<Fragment>, fm: FragmentManager) :
        FragmentStatePagerAdapter(fm)
    {
/*        constructor(fm: FragmentManager) : super(fm)
        {

        }*/

        override fun getItem(position: Int): Fragment
        {
            return list.get(position)
        }

        override fun getCount(): Int
        {
            return list.size
        }

        override fun getItemPosition(`object`: Any): Int
        {
//            return super.getItemPosition(`object`)
            return PagerAdapter.POSITION_NONE
        }
    }
}


/*    val tabNameList: MutableList<item_tab_name_list> = arrayListOf(
        item_tab_name_list("전체",0),
        item_tab_name_list("돼지",0),
        item_tab_name_list("소",0),
        item_tab_name_list("연어",0),
        item_tab_name_list("닭",0),
        item_tab_name_list("기타",0)
                                                                  )*/

/*var result = tabNameList.get(i).tabName.split("│".toRegex())
for (j in result.indices)
{
    if (tabNameList.get(i).tabName == result[result.lastIndex])
    {
        val filteredList = mList.filter {
            //                                it.mlab_list_meats.contains(tabNameList.get(i).tabName)
            it.mlab_list_meats.equals(result[j])
        }

        // 탭 추가
        FragmentList.add(Fragment_Open_Note_List(filteredList as ArrayList<item_note_list>, tabNameList.get(i).tabName, tabNameList.get(i).categoryIndex, displayMetrics.widthPixels)) // 해당 탭에 띄울 리사이클러뷰
    }
    else
    {
        val filteredList = mList.filter {
            it.mlab_list_meats.contains(tabNameList.get(i).tabName)
            // it.mlab_list_meats.equals(result[j])
        }
        // 탭 추가
        FragmentList.add(Fragment_Open_Note_List(filteredList as ArrayList<item_note_list>, tabNameList.get(i).tabName, tabNameList.get(i).categoryIndex, displayMetrics.widthPixels)) // 해당 탭에 띄울 리사이클러뷰
    }
    e(TAG, "result[${j}]: ${result[j]}")
} */


/*              // fragment file name 추출
                for (i in FragmentList!!.indices)
                {
                    e(
                        TAG,
                        "FragmentList.get(i).getText(id): ${FragmentList!!.get(i).javaClass.simpleName}"
                     )
                }*/
