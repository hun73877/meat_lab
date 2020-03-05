package com.example.meat_lab.Tab_openLab

import com.google.gson.annotations.SerializedName

data class item_note_list(
    @SerializedName("idx") var idx: String,
    @SerializedName("mlab_user_index") var mlab_user_index: String,
    @SerializedName("name") var name: String,
    @SerializedName("photo") var photo: String,
    @SerializedName("mlab_image_thumb") var mlab_image_thumb: String,
    @SerializedName("mlab_cook_title") var mlab_cook_title: String,
    @SerializedName("mlab_cook_time") var mlab_cook_time: String,
    @SerializedName("mlab_list_meats") var mlab_list_meats: String,
    @SerializedName("mlab_list_parts") var mlab_list_parts: String,
    @SerializedName("mlab_list_tools") var mlab_list_tools: String,
    @SerializedName("mlab_note_isPublic") var mlab_note_isPublic: Boolean,
    @SerializedName("mlab_note_isBookMark") var mlab_note_isBookMark: Boolean,
    @SerializedName("mlab_note_BookMark_Count") var mlab_note_BookMark_Count: Int,

    @SerializedName("mlab_write_date") var mlab_write_date: String,
    @SerializedName("mlab_list_ingredients") var mlab_list_ingredients: String,
    @SerializedName("mlab_cook_description") var mlab_cook_description: String,
    @SerializedName("mlab_image_list") var mlab_image_list: String,

    @SerializedName("mlab_list_cook_process") var mlab_list_cook_process: String
                         )
