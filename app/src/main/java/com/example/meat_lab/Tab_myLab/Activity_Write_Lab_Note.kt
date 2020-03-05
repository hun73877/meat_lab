package com.example.meat_lab.Tab_myLab

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log.e
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieDrawable
import com.coursion.freakycoder.mediapicker.galleries.Gallery
import com.example.meat_lab.Activity_Main.Companion.GET_ID
import com.example.meat_lab.ApiInterface
import com.example.meat_lab.R
import com.example.meat_lab.Tab_myLab.Split_My_Lab_Tab.Fragment_My_Note.Companion.HANDLER_DONE_LAB_NOTE_WRITE
import com.example.meat_lab.Tab_myLab.itemTouchHelper.AdapterNoteDescription
import com.example.meat_lab.Tab_myLab.itemTouchHelper.ItemDragListener
import com.example.meat_lab.Tab_myLab.itemTouchHelper.ItemTouchHelperCallback
import com.example.meat_lab.Tab_myLab.itemTouchHelper.item_Write_Note
import com.example.meat_lab.Tab_openLab.Fragment_Open_Lab
import com.example.meat_lab.Tab_openLab.Fragment_Open_Lab.Companion.HANDLER_OPEN_LAB
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_write_lab_note.*
import kotlinx.android.synthetic.main.note_image_pager.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class Activity_Write_Lab_Note : AppCompatActivity(), ItemDragListener
{
    //    var viewPager: ViewPagerAdapter? = null
    var TAG: String = "Activity_Write_Lab_Note"

    private val OPEN_MEDIA_PICKER = 1  // Request code
    private val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100 // 외부 저장소 읽기를위한 요청 코드

    private var pagerAdapter: noteWritePagerAdapter? = null
    var imagePath = ArrayList<String>()

    companion object
    {
        var HANDLER_CHANGE_LISTENER: Handler? = null
        val PROGRESS_CODE = 101
        var IS_KEYBOARD_OPEN = false
    }

    private val item_process: MutableList<item_Write_Note> = mutableListOf(
        item_Write_Note(""),
        item_Write_Note(""),
        item_Write_Note("")
                                                                          )

    // 돼지, 소, 닭, 연어, 기타 (돼지는 기본 선택 (true))
    var selectMeats = arrayOf(
        true, false, false,
        false, false
                             )

/*    var selectMeatsName = arrayOf(
        "pork", "beef", "chicken",
        "salmon", "anotherMeats"
                                 )*/

    var selectMeatsName = arrayOf(
        "돼지", "소", "닭",
        "연어", "anotherMeats"
                                 )

    // 후라이팬, 오븐, 튀김기, 에어프라이, 수비드머신, 기타 (후라이팬 기본 선택 (true))
    var selectTools = arrayOf(
        true, false, false,
        false, false, false
                             )

    /*    var selectToolsName = arrayOf(
            "pan", "oven", "fryer",
            "air_fryer", "suvid", "anotherTools"
                                 )*/

    var selectToolsName = arrayOf(
        "후라이팬", "오븐", "튀김기",
        "에어프라이어", "수비드 머신", "anotherTools"
                                 )

    private lateinit var mainAdapter: AdapterNoteDescription
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_lab_note)

        val drawable = // 이미지 영역 테두리 둥글게 깎기
            getDrawable(R.drawable.item_image_round_corner_1) as? GradientDrawable
        write_note_thumb.setBackground(drawable)
        write_note_thumb.setClipToOutline(true)

        mainAdapter = AdapterNoteDescription(item_process, this, this@Activity_Write_Lab_Note)

        recycler_view.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@Activity_Write_Lab_Note)
//            addItemDecoration(DividerItemDecoration(this@Activity_Write_Lab_Note, LinearLayoutManager.VERTICAL)) // 경게선 추가
        }

        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(mainAdapter))
        itemTouchHelper.attachToRecyclerView(recycler_view)

        add_note_button.setOnClickListener(View.OnClickListener {
            item_process.add(item_Write_Note(""))
            mainAdapter.notifyDataSetChanged()

            e(TAG, "(아래) 새 항목 추가 후 ")

            for (i in item_process.indices)
            {
                e(TAG, "description: " + (i + 1) + ": " + item_process.get(i).description)
            }
        })

        // todo: 우측 상단 버튼 클릭 로직
        buttonGroupRightTop()

        // todo: 고기, 도구선택 버튼 모음
        buttonSelectGroup()

        note_write_select_image.setOnClickListener(View.OnClickListener {
            if (!permissionIfNeeded())
            {
                val intent = Intent(this, Gallery::class.java)
                // Set the title
                intent.putExtra("title", "Select media")

                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
                // 이미지 및 비디오 선택 모드 1, 이미지 전용 2 및 비디오 3
                intent.putExtra("mode", 1)
                intent.putExtra("maxSelection", 10) // 선택 가능한 이미지 가지수
                intent.putExtra("tabBarHidden", true) //Optional - default value is false
                startActivityForResult(intent, OPEN_MEDIA_PICKER)
            }
            else
            {
                permissionIfNeeded()
            }
        })

        // todo: 작성한 내용 서버로 전송하기
        buttonWriteDoneLogic()
    }

    // 서버로 전송할 배열 값을 String 한 줄로 정리하기
    var sendMeatNameList: String? = null
    var sendToolNameList: String? = null
    var sendProcessList: String? = null

    // todo: 작성한 내용 서버로 전송하기
    private fun buttonWriteDoneLogic()
    {
        write_done_button.setOnClickListener(View.OnClickListener {

            e(TAG, "작성 완료. 작성한 글 목록 불러오기")

            e(TAG, " ==== 이미지 ==== ")
            for (i in imagePath.indices)
            {
                e(TAG, "imagePath[$i]: ${imagePath[i]}")
            }
            e(TAG, "썸네일 인덱스: ${selectThumbIndex}번")

            e(TAG, " ==== 요리명 ==== ")
            e(TAG, " write_title: ${write_title.text}\n\n")

            e(TAG, " ==== 소요시간 ==== ")
            e(TAG, "cook_time: ${cook_time.text}\n\n")

            e(TAG, " ==== 선택한 육류 ==== ")
            for (i in selectMeats.indices)
            {
                if (selectMeats[i])
                {
//                    e(TAG, "selectMeatsName: ${selectMeatsName[i]}")

                    if (sendMeatNameList == null)
                    {
                        sendMeatNameList = selectMeatsName[i]
                    }

                    else
                    {
//                        sendMeatNameList = sendMeatNameList + " │ " + selectMeatsName[i]
                        sendMeatNameList = sendMeatNameList + ", " + selectMeatsName[i]
                    }
                }
            }

            e(TAG, "sendMeatNameList: $sendMeatNameList")

            e(TAG, " ==== 사용할 부위 ==== ")
            e(TAG, "meat_part: ${meat_part.text}\n\n")

            e(TAG, " ==== 선택한 도구 ==== ")
            for (i in selectTools.indices)
            {
                if (selectTools[i])
                {
//                    e(TAG, "selectToolsName: ${selectToolsName[i]}")

                    if (sendToolNameList == null)
                    {
                        sendToolNameList = selectToolsName[i]
                    }
                    else
                    {
//                        sendToolNameList = sendToolNameList + " │ " + selectToolsName[i]
                        sendToolNameList = sendToolNameList + ", " + selectToolsName[i]
                    }
                }
            }

            e(TAG, "sendToolNameList: $sendToolNameList")

            e(TAG, " ==== 사용할 식재료 ==== ")
            e(TAG, "ingredients: ${ingredients.text}\n\n")

            e(TAG, " ==== 요리 설명 및 리뷰 ==== ")
            e(TAG, "cooking_description: ${cooking_description.text}\n\n")

            e(TAG, " ==== 요리 절차 ==== ")
            for (i in item_process.indices)
            {
                if (item_process[i].description == null || item_process[i].description.length == 0)
                {
                    e(TAG, "item_process[$i]: this index is null")
                }
                else if (item_process.size == 0)
                {
                    e(TAG, "item_process[]: null")
                }
                else
                {
//                    e(TAG, "item_process[$i]: ${item_process[i].description}")
                    if (sendProcessList == null)
                    {
                        sendProcessList = item_process[i].description
                    }
                    else
                    {
                        sendProcessList = sendProcessList + " │ " + item_process[i].description
                    }
                }
            }

            e(TAG, "sendProcessList: $sendProcessList")

            e(TAG, "전송 시작")

            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            // todo: 서버로 이미지 전송하기
            for (i in selectionResult!!.indices)
            {
//                var file = File(imagePath.get(i)) /*imagePathList.get(i)*/
                e(TAG, "selectionResult: ${selectionResult!![i]}")
                var file = File(selectionResult!![i]) /*imagePathList.get(i)*/
                file = Compressor(this).setQuality(20).compressToFile(file)
                builder.addFormDataPart("ImageUri[]", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
            }

            // 서버로 전송할 값 selectionThumbIndex
            builder.addFormDataPart("selectThumbIndex", selectThumbIndex.toString())
                .addFormDataPart("mlab_user_index", GET_ID.toString())
                .addFormDataPart("mlab_cook_title", write_title.text.toString())
                .addFormDataPart("mlab_cook_time", cook_time.text.toString())
                .addFormDataPart("mlab_list_meats", sendMeatNameList.toString())
                .addFormDataPart("mlab_list_parts", meat_part.text.toString())
                .addFormDataPart("mlab_list_tools", sendToolNameList.toString())
                .addFormDataPart("mlab_list_ingredients", ingredients.text.toString())
                .addFormDataPart("mlab_cook_description", cooking_description.text.toString())
                .addFormDataPart("mlab_list_cook_process", sendProcessList.toString())

            val requestBody = builder.build()
            val gson = GsonBuilder()
                .setLenient()
                .create()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://115.68.231.84/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            val sendMeatCookNote: ApiInterface =
                retrofit.create<ApiInterface>(ApiInterface::class.java)
            val result: Call<ResponseBody?>? = sendMeatCookNote.multipartUpload(requestBody)

            result?.enqueue(object : Callback<ResponseBody?>
            {
                @SuppressLint("LongLogTag")
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                                       )
                {
                    e(TAG, "response: ${response.body().toString()}")

/*                    Toast.makeText(
                        this@Activity_Write_Lab_Note,
                        "전송완료\n작성중인 화면을 닫습니다",
                        Toast.LENGTH_SHORT
                                  ).show()*/

                    // 완료 애니메이션 재생 후 종료
                    note_write_done_message.visibility = View.VISIBLE
                    note_write_done_message.setAnimation("upload_note.json")
                    note_write_done_message.repeatCount = LottieDrawable.INFINITE
                    note_write_done_message.playAnimation()
                    Timer("SettingUp", false).schedule(2800) {

                        val message: Message = HANDLER_DONE_LAB_NOTE_WRITE?.obtainMessage()!!
                        message.what = PROGRESS_CODE
                        message.arg1 = 779
                        message.obj = "done_write"
                        HANDLER_DONE_LAB_NOTE_WRITE?.sendMessage(message)

                        finish()
                    }
                }

                @SuppressLint("LongLogTag")
                override fun onFailure(
                    call: Call<ResponseBody?>,
                    t: Throwable
                                      )
                {
                    Toast.makeText(this@Activity_Write_Lab_Note, "전송실패", Toast.LENGTH_SHORT).show()
                    e(TAG, "error t: ${t.message}")

                    /*                Toast.makeText(Activity_hostAddWorkSpace_2.this, "전송실패", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Call<ResponseBody> call: " + call.toString());
                    Log.e(TAG, "error t: " + t.getMessage());

                    card_2.setVisibility(View.GONE);
                    card_1.setVisibility(View.VISIBLE);*/
                }
            })

        })
    }

    var selectThumbIndex: Int = 0 // 기본 썸네일 인덱스는 0번임
    var selectionResult: ArrayList<String>? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

//        var selectMeats: ArrayList<String>? = null

        if (requestCode == OPEN_MEDIA_PICKER)
        {
            if (resultCode == Activity.RESULT_OK && data != null)
            {
                selectionResult = data.getStringArrayListExtra("result")
                for (i in selectionResult!!.indices)
                {
                    var file = File(selectionResult!![i])
                    file = Compressor(this).setQuality(20).compressToFile(file)
                    var uri = Uri.fromFile(file)

                    imagePath.add(uri.toString())
                }

                // 뷰페이저 세팅
                var adapter = noteWritePagerAdapter(imagePath)
                write_page_view?.adapter = adapter
                (write_page_view?.adapter as noteWritePagerAdapter).notifyDataSetChanged()

                number_indicator.setText("1 / ${imagePath.size}")
                number_indicator.visibility = View.VISIBLE
                note_write_delete_image.visibility = View.VISIBLE
                note_write_select_thumb.visibility = View.VISIBLE

                selectThumbIndex = 0
                note_write_select_thumb.background = getDrawable(R.drawable.item_text_title_1_2)
                note_write_select_thumb.text = "선택됨"

                write_page_view!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
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
                        if (imagePath.size != 0)
                        {
                            e(TAG, "다시 활성화...")

                            e(TAG, "adapter position: $position")
                            e(TAG, "adapter imagePath.size: ${imagePath.size}")

                            number_indicator.setText("${(position + 1)} / ${imagePath.size}")

                            // todo: 삭제 버튼 클릭
                            note_write_delete_image.setOnClickListener(View.OnClickListener {

                                // 삭제 전
/*                                for (i in imagePath.indices)
                                {
                                    e(TAG, "before imagePath: ${imagePath[i]}")
                                }*/

                                e(TAG, "adapter position remove: $position")

                                imagePath.removeAt(position)

                                // 삭제 후
/*                                for (i in imagePath.indices)
                                {
                                    e(TAG, "after imagePath: ${imagePath[i]}")
                                }*/

                                adapter = noteWritePagerAdapter(null)
                                adapter = noteWritePagerAdapter(imagePath)

                                // 페이지 삭제 후 뷰페이저 다시 세팅
                                write_page_view?.adapter = adapter
                                write_page_view.setCurrentItem(position)
                                (write_page_view?.adapter as noteWritePagerAdapter).notifyDataSetChanged()

                                number_indicator.setText("${(position + 1)} / ${imagePath.size}")

                                // 뷰페이저 비어있을 땐 삭제, 페이지 버튼 숨기기
                                if (imagePath == null || imagePath.size == 0)
                                {
                                    e(TAG, "imagePath.size: ${imagePath.size}")
                                    number_indicator.visibility = View.GONE
                                    note_write_delete_image.visibility = View.GONE
                                    note_write_select_thumb.visibility = View.GONE
                                    selectThumbIndex = 0
                                }

                                // 선택했던 썸네일이 삭제되고,
                                // 선택했던 썸네일의 배열 인덱스가 존재하지 않으면
                                // 현재 존재하는 마지막 배열 인덱스의 이미지를 썸네일로 시정하기
                                if (imagePath.size < (selectThumbIndex + 1))
                                {
                                    selectThumbIndex = (imagePath.size - 1)
                                    note_write_select_thumb.background = getDrawable(R.drawable.item_text_title_1_2)
                                    note_write_select_thumb.text = "선택됨"
                                }
                            })

                            // todo: 썸네일 표시하기
                            if (position == selectThumbIndex)
                            {
                                e(TAG, "현재 이미지가 썸네일 입니다: selectionThumbIndex: $selectThumbIndex")
                                note_write_select_thumb.background = getDrawable(R.drawable.item_text_title_1_2)
                                note_write_select_thumb.text = "선택됨"
                            }

                            else
                            {
                                note_write_select_thumb.background = getDrawable(R.drawable.item_text_title_1)
                                note_write_select_thumb.text = "썸네일"
                            }

                            // todo: 썸네일 이미지 표시하기
                            note_write_select_thumb.setOnClickListener(View.OnClickListener {
                                selectThumbIndex = position
                                note_write_select_thumb.text = "선택됨"
                                note_write_select_thumb.background = getDrawable(R.drawable.item_text_title_1_2)
                            })
                        }
                        else
                        {
                            number_indicator.visibility = View.GONE
                            note_write_delete_image.visibility = View.GONE
                        }

                        e(TAG, "onPageScrolled: $position")
                        // 이 부분을 선언하지 않으면 액티비티에 접근할 때
                        // 현재 페이지 수가 뜨지 않음.
                    }

                    override fun onPageSelected(position: Int)
                    {
                        // 현재 몇 페이지인지 감지하는 부분
                        e(TAG, "onPageSelected: position: $position")
                    }
                })

            }
        }
    }

    // todo: 고기, 도구선택 버튼 모음
    private fun buttonSelectGroup()
    {
        // todo: 고기
        // 돼기고기 선택
        select_meat_pork.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(select_meat_pork, selectMeats, 0, "meats") // todo: 뷰 상태 변경
        })

        // 소고기 선택
        select_meat_beef.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(select_meat_beef, selectMeats, 1, "meats") // todo: 뷰 상태 변경
        })

        // 닭 선택
        select_meat_chicken.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(
                select_meat_chicken,
                selectMeats,
                2,
                "meats"
                                       ) // todo: 뷰 상태 변경
        })

        // 연어 선택
        select_meat_salmon.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(
                select_meat_salmon,
                selectMeats,
                3,
                "meats"
                                       ) // todo: 뷰 상태 변경
        })

        // 기타 (true일 때 EditText 활성화 해야됨...)
        select_meat_another.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(
                select_meat_another,
                selectMeats,
                4,
                "meats"
                                       ) // todo: 뷰 상태 변경
        })


        // todo: 도구
        // 후라이팬 선택
        select_tool_pan.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(select_tool_pan, selectTools, 0, "tools") // todo: 뷰 상태 변경
        })

        // 오븐
        select_tool_oven.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(select_tool_oven, selectTools, 1, "tools") // todo: 뷰 상태 변경
        })

        // 튀김기
        select_tool_fryer.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(select_tool_fryer, selectTools, 2, "tools") // todo: 뷰 상태 변경
        })

        // 에어프라이
        select_tool_air_fryer.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(
                select_tool_air_fryer,
                selectTools,
                3,
                "tools"
                                       ) // todo: 뷰 상태 변경
        })

        // 수비드
        select_tool_suvid.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(select_tool_suvid, selectTools, 4, "tools") // todo: 뷰 상태 변경
        })

        // 기타
        select_tool_another.setOnClickListener(View.OnClickListener {
            buttonSelectGroupViewChange(
                select_tool_another,
                selectTools,
                5,
                "tools"
                                       ) // todo: 뷰 상태 변경
        })
    }

    // todo: 뷰 상태 변경
    private fun buttonSelectGroupViewChange(
        view: View,
        array: Array<Boolean>,
        index: Int,
        tvType: String
                                           )
    {
        if (array[index])
        {
            view.background = getDrawable(R.drawable.item_selector_off)
            array[index] = false
        }
        else
        {
            view.background = getDrawable(R.drawable.item_selector_on)
            array[index] = true
        }

        // todo: 선택 개수 세기
        selectCount(array, tvType)
    }

    // todo: 선택 개수 세기
    var selectCount: Int = 0

    fun selectCount(array: Array<Boolean>, tvType: String)
    {
        selectCount = 0

        for (i in array.indices)
        {
            if (array[i] == true)
            {
                selectCount++

                e(TAG, "array: $selectCount")
            }

            if (array.size == (i + 1))
            {
                var index = 0

                if (tvType.equals("meats"))
                {
                    index = 4
                }
                else
                {
                    index = 5
                }

                if (array[index] == true)
                {
                    e(TAG, "'기타'항목 선택됨")

                    if (selectCount == 1)
                    {
                        if (tvType.equals("meats"))
                        {
                            // 고기 선택 가지수 표시
                            select_meat_count.text = "기타 항목 선택됨"
                        }
                        else
                        {
                            // 도구 선택 가지수 표시
                            select_tool_count.text = "기타 항목 선택됨"
                        }

                    }
                    else
                    {
                        if (tvType.equals("meats"))
                        {
                            // 고기 선택 가지수 표시
                            select_meat_count.text = "기타 항목 외 ${selectCount - 1}가지 선택됨"
                        }
                        else
                        {
                            // 도구 선택 가지수 표시
                            select_tool_count.text = "기타 항목 외 ${selectCount - 1}가지 선택됨"
                        }
                    }
                }
                else
                {
                    if (selectCount == 0)
                    {
                        if (tvType.equals("meats"))
                        {
                            // 고기 선택 가지수 표시
                            select_meat_count.text = ""
                        }
                        else
                        {
                            // 도구 선택 가지수 표시
                            select_tool_count.text = ""
                        }
                    }
                    else
                    {
                        if (tvType.equals("meats"))
                        {
                            // 고기 선택 가지수 표시
                            select_meat_count.text = "${selectCount}가지 선택됨"
                        }
                        else
                        {
                            // 도구 선택 가지수 표시
                            // 고기 선택 가지수 표시
                            select_tool_count.text = "${selectCount}가지 선택됨"
                        }
                    }
                }
            }
        }
    }

    // todo: 우측 상단 버튼 클릭 로직
    private fun buttonGroupRightTop()
    {
        // 닫기버튼 클릭
        button_close.setOnClickListener(View.OnClickListener {

            val builder = AlertDialog.Builder(
                ContextThemeWrapper(
                    this@Activity_Write_Lab_Note,
                    R.style.Theme_AppCompat_Light_Dialog
                                   )
                                             )

            builder.setIcon(R.drawable.logo_7)
            builder.setTitle("화면을 닫습니다")
            builder.setMessage("작성하지 않은 내용은 삭제 됩니다")

            builder.setPositiveButton("확인") { _, _ ->
                finish()
            }

            builder.setNegativeButton("취소") { _, _ ->
                e(TAG, "취소")
            }

            builder.show()

        })

        // 저장버튼 클릭
        button_save_temporary.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@Activity_Write_Lab_Note, "저장 기능은 준비중입니다", Toast.LENGTH_LONG)
                .show() // 토스트 메시지를 띄우고
        })


        // 핸들러 수신 받기
        HandelrReceuver()
    }

    // 핸들러 수신 받기
    private fun HandelrReceuver()
    {
        e(TAG, "핸들러 신호 수신받음")
        HANDLER_CHANGE_LISTENER = @SuppressLint("HandlerLeak") object : Handler()
        {
            override fun handleMessage(msg: Message)
            {
                super.handleMessage(msg)

                // 아이템 순서 변경 감지
                if (msg.arg1 == 777)
                {
                    e(TAG, "handleMessage: msg.obj: ${msg.obj.toString()}")

                    if (msg.obj.equals("item_delete"))
                    {
                        mainAdapter.notifyDataSetChanged()
                        e(TAG, "(아래) 항목 삭제 후 ")

                        for (i in item_process.indices)
                        {
                            e(
                                TAG,
                                "description: " + (i + 1) + ": " + item_process.get(i).description
                             )
                        }
                    }
                    else if (msg.obj.equals("item_position_change"))
                    {
                        mainAdapter.notifyDataSetChanged()
                        e(TAG, "(아래) 항목 순서 변경 후 ")

                        for (i in item_process.indices)
                        {
                            e(
                                TAG,
                                "description: " + (i + 1) + ": " + item_process.get(i).description
                             )
                        }

                    }
                    else
                    {

                        var result = msg.obj.toString().split("│".toRegex())
                        item_process.set(
                            result[0].toInt(),
                            item_Write_Note(result[1])
                                        )

                        e(TAG, "리스트 갱신함: " + result[0] + "번 인덱스를 " + result[1] + "로")
                    }
                }
            }
        }

    }

    // 리사이클러뷰 드래그 처리
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    {
        e(TAG, "onStartDrag")
        itemTouchHelper.startDrag(viewHolder)
    }

    // 뒤로가기 막음 (스낵바)
    override fun onBackPressed()
    {
//        super.onBackPressed()

        e(TAG, "onBackPressed: ")

//        Snackbar.make(write_root,"작성중에 나갈 수 없습니다!", Snackbar.LENGTH_SHORT).show()

        // Initialize a new snack bar instance
        val snackbar = Snackbar.make(
            write_root,
            "X 아이콘을 눌러 종료하세요 (우측 상단)",
            Snackbar.LENGTH_INDEFINITE
                                    )

        // Get the snack bar root view
        val snack_root_view = snackbar.view

        // Get the snack bar text view
        val snack_text_view = snack_root_view
            .findViewById<TextView>(R.id.snackbar_text) //

        // Get the snack bar action view
        val snack_action_view = snack_root_view
            .findViewById<Button>(R.id.snackbar_action)

        // Change the snack bar root view background color
        snack_root_view.setBackgroundColor(Color.parseColor("#313131"))

        // Change the snack bar text view text color
        snack_text_view.setTextColor(Color.parseColor("#ffffff"))

        // Change snack bar text view text style
        snack_text_view.setTypeface(Typeface.DEFAULT, Typeface.NORMAL)

        // Change the snack bar action button text color
        snack_action_view.setTextColor(Color.WHITE)

        // Set an action for snack bar
        snackbar.setAction("[ 확인 ]", {
            snackbar.dismiss()
        })

        // Finally, display the snack bar
        snackbar.show()

        return
    }

    // 이미지 퍼미션 요청
    private fun permissionIfNeeded(): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            )
            {
                // 설명을 보여줘야합니까?
                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                                                        )
                )
                {
                    // 연락처를 읽어야하는 이유를 사용자에게 설명
                    // Explain to the user why we need to read the contacts
                }

                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                                  )
                return true
            }
        }
        return false
    }

    // todo: 이미지 뷰페이저 어댑터
    class noteWritePagerAdapter(private val list: ArrayList<String>?) : PagerAdapter()
    {
        override fun instantiateItem(container: ViewGroup, position: Int): Any
        {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.note_image_pager, container, false)

//            view.imagePage.setImageResource() // = list[position].name
//            view.ivItem.setImageResource(list[position].getImageId(container.context))
//            view.tvItemContent.text = list[position].content

            val imgUri = Uri.parse(list?.get(position))
            view.imagePage.setImageURI(null)
            view.imagePage.setImageURI(imgUri)

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any)
        {
            container.removeView(obj as View?)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean
        {
            return view == obj
        }

        override fun getCount(): Int
        {
            return list!!.size
        }
    }
}


// 다이얼로그 사용방법 2
/*

                    val builder = AlertDialog.Builder(
        ContextThemeWrapper(
                this@Activity_Cart,
                R.style.Theme_AppCompat_Light_Dialog
                           )
                                 )

builder.setIcon(R.drawable.logo_1)
builder.setTitle("삭제")
builder.setMessage("선택한 메뉴를 삭제합니다")

builder.setPositiveButton("확인") { _, _ ->
    e(TAG, "확인")
}

builder.setNegativeButton("취소") { _, _ ->
    e(TAG, "취소")
}

builder.show()
*/
