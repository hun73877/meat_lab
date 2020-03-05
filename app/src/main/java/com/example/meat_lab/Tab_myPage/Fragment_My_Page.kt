package com.example.meat_lab.Tab_myPage


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meat_lab.R
import com.example.meat_lab.SessionManager
import kotlinx.android.synthetic.main.fragment_fragment_my_page.view.*

/**
 * A simple [Fragment] subclass.
 */
class Fragment_My_Page : Fragment()
{

    var sessionManager: SessionManager? = null
    var mContext: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fragment_my_page, container, false)

        mContext = mContext?.applicationContext

        // 세션 선언
        sessionManager = SessionManager(requireContext())

        view.button_logout.setOnClickListener(View.OnClickListener {
            sessionManager?.logout()
            activity?.finish()
        })

        return view
    } // End onCreate
}
