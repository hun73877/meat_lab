package com.example.meat_lab.Tab_myLab.itemTouchHelper


import android.content.Context
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log.e
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.meat_lab.R
import com.example.meat_lab.Tab_myLab.Activity_Write_Lab_Note.Companion.HANDLER_CHANGE_LISTENER
import com.example.meat_lab.Tab_myLab.Activity_Write_Lab_Note.Companion.PROGRESS_CODE
import kotlinx.android.synthetic.main.item_note_discription.view.*

class AdapterNoteDescription(
    private val items: MutableList<item_Write_Note>,
    private val listener: ItemDragListener,
    private val mContext: Context
) : RecyclerView.Adapter<AdapterNoteDescription.ViewHolder>(), ItemActionListener, ItemDragListener
{
    var TAG: String = "AdapterNoteDescription"

    override fun getItemCount(): Int = items.size
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note_discription, parent, false)
        return ViewHolder(view, listener)
    }

//    var description: Array<String>? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        e(TAG, "onBindViewHolder: ")
        holder.bind(items[position])
        holder.itemView.note_write_number.text = (position + 1).toString() + "."


        if (TextUtils.isEmpty(items.get(position).description))
        {
            e(TAG, "onBindViewHolder: ${(position + 1)}번 리스트가 비어있음")
            holder.itemView.note_write_description.setText(null)

            if (position == 0)
            {
                holder.itemView.note_write_description.setHint("(클릭)")
            }

            else
            {
                holder.itemView.note_write_description.setHint("_")
            }
        } else
        {
            holder.itemView.note_write_description.setText(items.get(position).description)
        }

        // 글 작성 다이얼로그
        holder.itemView.note_write_description.setOnClickListener(View.OnClickListener {
            e(TAG, "onBindViewHolder: setOnClickListener: ${(position + 1)}")

            // todo: 다이얼로그 준비
            val builder = AlertDialog.Builder(mContext)
            val inflater = LayoutInflater.from(mContext)
            val dialog_view = inflater.inflate(R.layout.dialog_note_write, null)
            builder.setView(dialog_view)
            var dialog = builder.create()

            // viewFind
            var dialog_note_number_1: TextView = dialog_view.findViewById(R.id.dialog_note_number_1)
            var dialog_note_number_2: TextView = dialog_view.findViewById(R.id.dialog_note_number_2)
            var dialog_note_cancel: TextView = dialog_view.findViewById(R.id.dialog_note_cancel)
            var dialog_note_confirm: TextView = dialog_view.findViewById(R.id.dialog_note_confirm)
            var dialog_note_edit: EditText = dialog_view.findViewById(R.id.dialog_note_edit)

            // 번호 표시
            dialog_note_number_1.setText("(" + (position + 1).toString() + ") ")
            dialog_note_number_2.setText((position + 1).toString() + ".")

            // 내용 표시
            dialog_note_edit.setText(holder.itemView.note_write_description.text)

            // 취소버튼 클릭
            dialog_note_cancel.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })

            // 작성 완료버튼 클릭
            dialog_note_confirm.setOnClickListener(View.OnClickListener {
                e(TAG, "description: " + dialog_note_edit.text)
                holder.itemView.note_write_description.setText(dialog_note_edit.text)
                items.set(position, item_Write_Note(dialog_note_edit.text.toString()))
                dialog.dismiss()
            })

            dialog.show()
        })
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    {
        itemTouchHelper.startDrag(viewHolder)
    }

    class ViewHolder(itemView: View, listener: ItemDragListener) :
        RecyclerView.ViewHolder(itemView)
    {

        init
        {
            itemView.drag_handle.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) // MotionEvent.ACTION_DOWN
                {
                    e("ViewHolder", "itemView.drag_handle.setOnTouchListener")
                    listener.onStartDrag(this)
                }
                false
            }
        }

        fun bind(item: item_Write_Note)
        {
            itemView.note_write_number.text
            itemView.note_write_description.text /*= item.description*/
        }
    }


    override fun onItemMoved(from: Int, to: Int)
    {
        if (from == to)
        {
            return
        }

        e(TAG, "onItemMoved: 포지션 변경됨 '${from + 1}'번 아이템이 '${to + 1}'번 아이템으로 이동함")

        val fromItem = items.removeAt(from)
        items.add(to, fromItem)
        notifyItemMoved(from, to)
    }

    override fun onItemSwiped(position: Int)
    {
        items.removeAt(position)

        e(TAG, "onItemSwiped: '${position + 1}'번 아이템이 삭제됨")

        notifyItemRemoved(position)

        val message: Message = HANDLER_CHANGE_LISTENER?.obtainMessage()!!
        message.what = PROGRESS_CODE
        message.arg1 = 777
        message.obj = "item_delete"
        HANDLER_CHANGE_LISTENER?.sendMessage(message)
    }
}

/*        // 아이템 드래그 감지하면 현재 입력한 텍스트 저장하기
        HANDLER_CHANGE_LISTENER = @SuppressLint("HandlerLeak") object : android.os.Handler()
        {
            override fun handleMessage(msg: Message)
            {
                super.handleMessage(msg)

                // 아이템 순서 변경 감지
                if (msg.arg1 == 778)
                {
                    if (msg.obj.equals("start_drag"))
                    {
                        for (i in items.indices)
                        {
                            e(
                                TAG,
                                "onBindViewHolder: (${(i + 1)})번 항목 내용 비교: ${items.get(i).description} / ${holder.itemView.note_write_description.text}"
                            )
                            e(
                                TAG,
                                "onBindViewHolder: (${(i + 1)})번 항목 position: ${holder.itemView.note_write_description.text}"
                            )

                            if (i == position)
                            {
                                if (items.get(position).description.equals(holder.itemView.note_write_description.text.toString()))
                                {
                                    e(TAG, "onBindViewHolder: 변경사항 있음")
                                } else
                                {
                                    e(TAG, "onBindViewHolder: 변경사항 없음")
                                }
                            } else
                            {
                                e(TAG, "onBindViewHolder: 변경사항 감지됨 (${(i + 1)})번")
//                                items.set(i, item_Write_Note(holder.itemView.note_write_description.text.toString()))
                            }
                        }

                        e(
                            TAG,
                            "onBindViewHolder: handleMessage: 드래그 감지됨 ${(position + 1)}번 항목 변경사항 저장됨: 내용: ${holder.itemView.note_write_description.text}"
                        )

                    }
                }
            }
        }*/

/*
                // 아이템 순서 변경 감지
            if (msg.arg1 == 778) {
                e(TAG, "handleMessage: msg.obj: ${msg.obj.toString()}")

                if (msg.obj.equals("list_drag_start"))
                {
                    e(TAG, position.toString() + "번 리스트 드래그 감지됨. ${position}번 인덱스에 입력된 값을 업데이트 함: " + holder.itemView.note_write_description.text.toString())
                    val message: Message = HANDLER_CHANGE_LISTENER?.obtainMessage()!!
                    message.what = PROGRESS_CODE
                    message.arg1 = 777
                    message.obj = position.toString() + "│" + holder.itemView.note_write_description.text.toString()
                    HANDLER_CHANGE_LISTENER?.sendMessage(message)
                }
            }
*/
