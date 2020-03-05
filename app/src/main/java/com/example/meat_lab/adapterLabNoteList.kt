package com.example.meat_lab

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Message
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.meat_lab.Activity_Main.Companion.GET_ID
import com.example.meat_lab.Tab_myLab.Activity_Write_Lab_Note.Companion.PROGRESS_CODE
import com.example.meat_lab.Tab_openLab.Fragment_Open_Lab.Companion.HANDLER_OPEN_LAB
import com.example.meat_lab.Tab_openLab.item_note_list
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_write_lab_note.*
import kotlinx.android.synthetic.main.item_list_note.view.*
import kotlinx.android.synthetic.main.note_image_pager.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class adapterLabNoteList(
    var context: Context?,
    var list: List<item_note_list>?,
    var deviceWidth: Int,
    var mode: String
                        ) : RecyclerView.Adapter<adapterLabNoteList.ViewHolder>()
{
    var TAG: String = "adapterNoteList"

    override fun getItemCount(): Int
    {
        return list!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_note,
                parent,
                false
                                                )
                         )
    }

    var Public = "공개중인 일지"
    var notPublic = "비공개 일지"
    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        e(TAG, "onBindViewHolder: ")
//        e(TAG, "onBindViewHolder: deviceWidth: $deviceWidth")

        // 썸네일 사진의 가로 값을 디바이스 가로 폭의 50%로 지정함. 높이는 가로 폭 50% 값을 그대로 적용한다.
        holder.note_list_thumb.layoutParams.width = (deviceWidth / 2)
        holder.note_list_thumb.layoutParams.height = (deviceWidth / 2)

        Picasso.get().load("http://115.68.231.84/${list?.get(position)?.mlab_image_thumb}")
            .placeholder(R.drawable.logo_2) //.resize(300,300)       // 300x300 픽셀
            .centerCrop().fit().into(holder.note_list_thumb)

        holder.note_list_title.text = list?.get(position)?.mlab_cook_title
        holder.note_list_meat_name.text = list?.get(position)?.mlab_list_meats
        holder.note_list_use_tool.text = list?.get(position)?.mlab_list_tools
        holder.note_list_cooking_time.text = "(${list?.get(position)?.mlab_cook_time}분)"

        // todo: 상세 페이지 실행
        holder.itemView.setOnClickListener(View.OnClickListener
        {
            val builder = AlertDialog.Builder(context!!)
            val inflater = LayoutInflater.from(context)
            val dialog_view = inflater.inflate(R.layout.dialog_lab_note, null)
            builder.setView(dialog_view)
            var dialog = builder.create()

            // 다이얼로그 테두리 둥글게
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)


            val d_lab_note_root: FrameLayout =
                dialog_view.findViewById(R.id.d_lab_note_root)
            val d_lab_note_button_close: ImageView =
                dialog_view.findViewById(R.id.d_lab_note_button_close)
            val d_lab_note_pager_view: ViewPager =
                dialog_view.findViewById(R.id.d_lab_note_pager_view)
            val d_lab_note_pager_view_number_indicator: TextView =
                dialog_view.findViewById(R.id.d_lab_note_pager_view_number_indicator)
            val d_lab_note_profile_area: LinearLayout =
                dialog_view.findViewById(R.id.d_lab_note_profile_area)
            val d_lab_note_profile_image: CircleImageView =
                dialog_view.findViewById(R.id.d_lab_note_profile_image)

            val d_lab_note_profile_name: TextView =
                dialog_view.findViewById(R.id.d_lab_note_profile_name)
            val d_lab_note_write_date: TextView =
                dialog_view.findViewById(R.id.d_lab_note_write_date)
            var d_lab_note_mark_area: LinearLayout =
                dialog_view.findViewById(R.id.d_lab_note_mark_area)
            val d_lab_note_button_book_mark: ImageView =
                dialog_view.findViewById(R.id.d_lab_note_button_book_mark)
            val d_lab_note_title: TextView = dialog_view.findViewById(R.id.d_lab_note_title)
            val d_lab_note_button_book_mark_count: TextView = dialog_view.findViewById(R.id.d_lab_note_button_book_mark_count)

            val d_lab_note_time: TextView = dialog_view.findViewById(R.id.d_lab_note_time)
            val d_lab_note_meat_name: TextView = dialog_view.findViewById(R.id.d_lab_note_meat_name)
            val d_lab_note_tools: TextView = dialog_view.findViewById(R.id.d_lab_note_tools)
            val d_lab_note_material: TextView = dialog_view.findViewById(R.id.d_lab_note_material)
            val d_lab_note_cooking_process: TextView =
                dialog_view.findViewById(R.id.d_lab_note_cooking_process)

            val d_lab_note_meat_part: TextView = dialog_view.findViewById(R.id.d_lab_note_meat_part)

            val drawable = // 이미지 영역 테두리 둥글게 깎기
                context!!.getDrawable(R.drawable.item_image_round_corner_1) as? GradientDrawable
            d_lab_note_pager_view.setBackground(drawable)
            d_lab_note_pager_view.setClipToOutline(true)

            Picasso.get().load(list?.get(position)?.photo)
                .placeholder(R.drawable.logo_2) //.resize(300,300)       // 300x300 픽셀
                .centerCrop().fit().into(d_lab_note_profile_image)
            d_lab_note_profile_name.text = list?.get(position)?.name
            d_lab_note_title.text = list?.get(position)?.mlab_cook_title
            d_lab_note_time.text = list?.get(position)?.mlab_cook_time
            d_lab_note_meat_part.text = list?.get(position)?.mlab_list_parts
            d_lab_note_material.text = list?.get(position)?.mlab_list_ingredients
            d_lab_note_write_date.text = "작성: " + list?.get(position)?.mlab_write_date
            d_lab_note_button_book_mark_count.text = "이 노트를 북마크한 유저: ${list?.get(position)?.mlab_note_BookMark_Count}명"

            // 사용하는 고기
            if (list?.get(position)?.mlab_list_meats == null)
            {
                d_lab_note_meat_name.text = list?.get(position)?.mlab_list_meats
            }
            else
            {
                var result = list?.get(position)?.mlab_list_meats?.split(", ".toRegex())

                var meats: String? = null

                for (i in result!!.indices)
                {
                    if (meats == null)
                    {
                        meats = "${(i+1)}. ${result[i]}"
                    }
                    else
                    {
                        meats = "${meats}\n${(i+1)}. ${result[i]}"
                    }
                }
                d_lab_note_meat_name.text = meats
            }

            // 도구 목록
            if (list?.get(position)?.mlab_list_tools == null)
            {
                d_lab_note_tools.text = list?.get(position)?.mlab_list_tools
            }
            else
            {
                var result = list?.get(position)?.mlab_list_tools?.split(", ".toRegex())

                var tools: String? = null

                for (i in result!!.indices)
                {
                    if (tools == null)
                    {
                        tools = "${(i+1)}. ${result[i]}"
                    }
                    else
                    {
                        tools = "${tools}\n${(i+1)}. ${result[i]}"
                    }
                }
                d_lab_note_tools.text = tools
            }


            // 요리 절차
            if (list?.get(position)?.mlab_list_cook_process == null)
            {
                d_lab_note_cooking_process.text = list?.get(position)?.mlab_list_cook_process
            }
            else
            {
                var result = list?.get(position)?.mlab_list_cook_process?.split(" │ ".toRegex())

                var process: String? = null

                for (i in result!!.indices)
                {
                    if (process == null)
                    {
                        process = "${(i+1)}. ${result[i]}"
                    }
                    else
                    {
                        process = "${process}\n${(i+1)}. ${result[i]}"
                    }
                }
                d_lab_note_cooking_process.text = process
            }

            // 내 레시피는 프로필 영역, 북마크 아이콘 가리기. 북마크 수만 표시하기
            if (list?.get(position)?.mlab_user_index == GET_ID || GET_ID.equals(list?.get(position)?.mlab_user_index))
            {
                d_lab_note_profile_area.visibility = View.VISIBLE // 작성자 가리기
                d_lab_note_mark_area.visibility = View.GONE // 북마크 가리기
            }
            else
            {
                d_lab_note_profile_area.visibility = View.VISIBLE   //
                d_lab_note_mark_area.visibility = View.VISIBLE      // 북마크 활성화

                e(TAG, "isBookMark: ${list?.get(position)?.mlab_note_isBookMark}")

                // todo: 북마크 버튼 클릭
                d_lab_note_button_book_mark.setOnClickListener(View.OnClickListener {

                    // Initialize a new snack bar instance
                    val snackbar:Snackbar

                    if (list?.get(position)?.mlab_note_isBookMark!!)
                    {
                        d_lab_note_button_book_mark.setImageResource(R.drawable.ic_book_mark_off_2)
                        updateBookMarkState(
                            GET_ID.toString(),
                            list?.get(position)?.idx.toString()
                                           ) // 북마크 변경 상태 저장
                        list?.get(position)?.mlab_note_isBookMark = false
                        list?.get(position)?.mlab_note_BookMark_Count = (list?.get(position)?.mlab_note_BookMark_Count!! - 1)
                        d_lab_note_button_book_mark_count.text = "이 노트를 북마크한 유저: ${list?.get(position)?.mlab_note_BookMark_Count}명"

                        snackbar = Snackbar.make(
                        d_lab_note_root,
                        "나의 북마크 목록에서 제거되었습니다",
                        Snackbar.LENGTH_SHORT
                                       )
                    }
                    else
                    {
                        d_lab_note_button_book_mark.setImageResource(R.drawable.ic_book_mark_on_2)
                        updateBookMarkState(GET_ID.toString(), list?.get(position)?.idx.toString())
                        list?.get(position)?.mlab_note_isBookMark = true
                        list?.get(position)?.mlab_note_BookMark_Count = (list?.get(position)?.mlab_note_BookMark_Count!! + 1)
                        d_lab_note_button_book_mark_count.text = "이 노트를 북마크한 유저: ${list?.get(position)?.mlab_note_BookMark_Count}명"

                        snackbar = Snackbar.make(
                            d_lab_note_root,
                            "나의 북마크 목록에 추가되었습니다",
                            Snackbar.LENGTH_SHORT
                                                )
                    }

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
/*                    snackbar.setAction("[ 확인 ]", {
                        snackbar.dismiss()
                    })*/

                    // Finally, display the snack bar
                    snackbar.show()
                })

                if (list?.get(position)?.mlab_note_isBookMark!!)
                {
                    d_lab_note_button_book_mark.setImageResource(R.drawable.ic_book_mark_on_2)
                }
                else
                {
                    d_lab_note_button_book_mark.setImageResource(R.drawable.ic_book_mark_off_2)
                }

            }

            var imagePath = list?.get(position)?.mlab_image_list!!.split("│")

            // todo: 뷰페이저 세팅
            var adapter = noteWritePagerAdapter(imagePath)
            d_lab_note_pager_view.adapter = adapter
            (d_lab_note_pager_view.adapter as noteWritePagerAdapter).notifyDataSetChanged()
            d_lab_note_pager_view.addOnPageChangeListener(object : ViewPager.OnPageChangeListener
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

                        d_lab_note_pager_view_number_indicator.setText("${(position + 1)} / ${imagePath.size}")
                    }
                }

                override fun onPageSelected(position: Int)
                {
                    // 현재 몇 페이지인지 감지하는 부분
                    e(TAG, "onPageSelected: position: $position")
                }
            })

            // 다이얼로그 닫기 버튼
            d_lab_note_button_close.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })

            dialog.setOnDismissListener(DialogInterface.OnDismissListener {
            })

            // 다이얼로그 실행
            dialog.show()
        })

        // todo: openLab, 북마크 화면에선 '공개 / 비공개'버튼 숨기기
        if (mode == "openNote" || mode == "myBookMark")
        {
            holder.note_list_profile_area.visibility = View.VISIBLE     //

            Picasso.get().load(list?.get(position)?.photo)
                .placeholder(R.drawable.logo_2) //.resize(300,300)       // 300x300 픽셀
                .centerCrop().fit().into(holder.note_list_profile_image)

            holder.note_list_profile_name.text = list?.get(position)?.name
        }

        // 나의 노트 화면 처리
        else
        {
            holder.note_list_public_area.visibility = View.VISIBLE
            holder.note_list_profile_area.visibility = View.GONE

            // todo: 공개 / 비공개 버튼 활성화
            holder.note_list_public_area.setOnClickListener(View.OnClickListener {

                if (list?.get(position)?.mlab_note_isPublic!!)
                {
                    holder.note_list_public_text.text = Public
                    //                    holder.note_list_public_switch_on.background = context?.getDrawable(R.drawable.item_switch_theme)
                    holder.note_list_public_switch_on.setBackgroundColor(Color.parseColor("#000000"))
                    holder.note_list_public_switch_off.setBackgroundColor(Color.parseColor("#63000000"))
                    list?.get(position)?.mlab_note_isPublic = false

                    // todo: 해당 노트의 공개여부 상태 변경
                    updatePublicState("true", list?.get(position)?.idx!!)
                }
                else
                {
                    holder.note_list_public_text.text = notPublic
                    //                    holder.note_list_public_switch_on.background = context?.getDrawable(R.drawable.item_switch_theme)
                    holder.note_list_public_switch_off.setBackgroundColor(Color.parseColor("#000000"))
                    holder.note_list_public_switch_on.setBackgroundColor(Color.parseColor("#63000000"))
                    list?.get(position)?.mlab_note_isPublic = true

                    // todo: 해당 노트의 공개여부 상태 변경
                    updatePublicState("false", list?.get(position)?.idx!!)
                }

            })

            // todo: 공개 / 비공개 상태 유지
            if (list?.get(position)?.mlab_note_isPublic!!)
            {
                //                    holder.note_list_public_switch_on.background = context?.getDrawable(R.drawable.item_switch_theme)
                holder.note_list_public_text.text = Public
                holder.note_list_public_switch_on.setBackgroundColor(Color.parseColor("#000000"))
                holder.note_list_public_switch_off.setBackgroundColor(Color.parseColor("#63000000"))
                list?.get(position)?.mlab_note_isPublic = false
            }
            else
            {
                holder.note_list_public_text.text = notPublic
                holder.note_list_public_switch_off.setBackgroundColor(Color.parseColor("#000000"))
                holder.note_list_public_switch_on.setBackgroundColor(Color.parseColor("#63000000"))
                list?.get(position)?.mlab_note_isPublic = true
            }
        }
    }

    // todo: 공개 / 비공개 상태 변경
    private fun updatePublicState(b: String, getIndex: String)
    {
        e(TAG, "updatePublicState: 전송 시작")
        val stringRequest = object : StringRequest(Method.POST,
            "http://115.68.231.84/getMeatNoteStatusUpdate.php",
            com.android.volley.Response.Listener { response ->
                e(TAG, "onResponse: response = $response")

                try
                {
                    val jsonObject = JSONObject(response)

                    val success = jsonObject.getString("success")

                    if (success == "1")
                    {
                        e(TAG, "onResponse: 상태변경 완료")

                        val message: Message = HANDLER_OPEN_LAB?.obtainMessage()!!
                        message.what = PROGRESS_CODE
                        message.arg1 = 799
                        message.obj = "change_book_mark"
                        HANDLER_OPEN_LAB?.sendMessage(message)
                    }
                    else
                    {
                        e(TAG, "onResponse: 문제발생")
                    }
                }
                catch (e: JSONException)
                {
                    e.printStackTrace()
                    e(TAG, "onResponse: JSONException e: $e")
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                e(TAG, "onErrorResponse: error: $error")
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()

                params.put("updatePublicState", b.toString())
                params.put("getIndex", getIndex)

                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    // todo: 북마크 변경 상태 저장
    private fun updateBookMarkState(getUserIndex: String, getNoteIndex: String)
    {
        e(TAG, "updateBookMarkState: 전송 시작")
        val stringRequest = object : StringRequest(Method.POST,
            "http://115.68.231.84/getBookMarkStatusUpdate.php",
            com.android.volley.Response.Listener { response ->
                e(TAG, "onResponse: response = $response")

                try
                {
                    val jsonObject = JSONObject(response)

                    val success = jsonObject.getString("success")

                    if (success == "1")
                    {
                        e(TAG, "onResponse: 북마크 상태변경 완료")

                        val message: Message = HANDLER_OPEN_LAB?.obtainMessage()!!
                        message.what = PROGRESS_CODE
                        message.arg1 = 799
                        message.obj = "change_book_mark"
                        HANDLER_OPEN_LAB?.sendMessage(message)
                    }
                    else
                    {
                        e(TAG, "onResponse: 문제발생")
                    }
                }
                catch (e: JSONException)
                {
                    e.printStackTrace()
                    e(TAG, "onResponse: JSONException e: $e")
                }
            },
            com.android.volley.Response.ErrorListener { error ->
                e(TAG, "onErrorResponse: error: $error")
            })
        {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>
            {
                val params = HashMap<String, String>()

                params.put("getUserIndex", getUserIndex)
                params.put("getNoteIndex", getNoteIndex)

                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }

    // todo: 이미지 뷰페이저 어댑터
    class noteWritePagerAdapter(private val list: List<String>) : PagerAdapter()
    {
        override fun instantiateItem(container: ViewGroup, position: Int): Any
        {
            val inflater = LayoutInflater.from(container.context)
            val view = inflater.inflate(R.layout.note_image_pager, container, false)

            Picasso.get().load("http://115.68.231.84/${list.get(position)}")
                .placeholder(R.drawable.logo_2) //.resize(300,300)       // 300x300 픽셀
                .centerCrop().fit().into(view.imagePage)

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
            return list.size
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val item = view

        val note_list_thumb = view.note_list_thumb
        val note_list_book_mark_button = view.note_list_book_mark_button
        val note_list_public_area = view.note_list_public_area

        val note_list_public_text = view.note_list_public_text
        val note_list_title = view.note_list_title

        val note_list_public_switch_on = view.note_list_public_switch_on
        val note_list_public_switch_off = view.note_list_public_switch_off

        val note_list_meat_name = view.note_list_meat_name
        val note_list_cooking_time = view.note_list_cooking_time
        val note_list_use_tool = view.note_list_use_tool

        val note_list_profile_area = view.note_list_profile_area
        val note_list_profile_image = view.note_list_profile_image
        val note_list_profile_name = view.note_list_profile_name
    }
}
