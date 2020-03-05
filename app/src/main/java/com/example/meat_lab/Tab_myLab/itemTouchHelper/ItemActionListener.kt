package com.example.meat_lab.Tab_myLab.itemTouchHelper

interface ItemActionListener {
    fun onItemMoved(from: Int, to: Int)
    fun onItemSwiped(position: Int)
}