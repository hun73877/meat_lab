package com.example.meat_lab.Tab_myLab.itemTouchHelper

import android.os.Message
import android.util.Log.e
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.meat_lab.Tab_myLab.Activity_Write_Lab_Note.Companion.HANDLER_CHANGE_LISTENER
import com.example.meat_lab.Tab_myLab.Activity_Write_Lab_Note.Companion.PROGRESS_CODE

class ItemTouchHelperCallback(val listener: ItemActionListener) : ItemTouchHelper.Callback()
{

    var TAG: String = "ItemTouchHelperCallback"

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int
    {
        val dragFlags = ItemTouchHelper.DOWN or ItemTouchHelper.UP
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
//        e(TAG, "getMovementFlags")
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean
    {
        listener.onItemMoved(viewHolder.adapterPosition, target.adapterPosition)

//        e(TAG, "onMove")

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    {
        e(TAG, "onSwiped")
        listener.onItemSwiped(viewHolder.adapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
    {
        super.clearView(recyclerView, viewHolder)

        if (HANDLER_CHANGE_LISTENER == null)
        {
            e(TAG, "삭제 취소")
        }
        else
        {
            e(TAG, "clearView: 포지션 변경 종료. 리사이클러뷰 갱신됨")

            val message: Message = HANDLER_CHANGE_LISTENER?.obtainMessage()!!
            message.what = PROGRESS_CODE
            message.arg1 = 777
            message.obj = "item_position_change"
            HANDLER_CHANGE_LISTENER?.sendMessage(message)
        }
    }

    override fun isLongPressDragEnabled(): Boolean = false

}