package com.example.meat_lab

import com.example.meat_lab.Tab_openLab.item_note_list
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface
{
    // 리사이클러뷰로
/*    @FormUrlEncoded
    @POST("getCoffeeMenu.php")
    fun getNestMenuList(@Field("SortRequest") id: String): Call<List<Item_Nest_Menu>>*/

    @FormUrlEncoded
    @POST("getMyBookMarkNote.php")
    fun getMyBookMarkNoteList(@Field("getId") id: String): Call<List<item_note_list>>

    @FormUrlEncoded
    @POST("getMyMeatCookNote.php")
    fun getMyNoteList(@Field("getId") id: String): Call<List<item_note_list>>

    @FormUrlEncoded
    @POST("getOpenMeatCookNote.php")
    fun getOpenNoteList(@Field("getId") id: String): Call<List<item_note_list>>

    @POST("addMeatCookNote.php")
    fun multipartUpload(@Body file: RequestBody?): Call<ResponseBody?>?
}
