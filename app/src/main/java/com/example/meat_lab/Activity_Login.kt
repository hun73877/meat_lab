package com.example.meat_lab

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Log.e
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity__login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Activity_Login : AppCompatActivity()
{
    private var TAG: String = "Activity_Login"

    // 로그인 PHP의 주소를 담을 변수 선언
    private val URL_LOGIN = "http://115.68.231.84/login.php" //iwinv

    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__login)
        e(TAG, "실행됨")

        sessionManager = SessionManager(this)

        loggin_button?.setOnClickListener(View.OnClickListener {
            val mEmail = login_mail?.getText().toString().trim { it <= ' ' }
            val mPass = login_password?.getText().toString().trim { it <= ' ' }

            e(TAG, "onResponse: button_login mEmail: = $mEmail" + ", mPass: $mPass")

            if (TextUtils.isEmpty(mEmail))
            {
                val anim =
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        R.anim.alpha
                                                ) // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                loggin_button?.startAnimation(anim) // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(this@Activity_Login, "이메일을 입력하세요!", Toast.LENGTH_SHORT)
                    .show() // 토스트 메시지를 띄우고
                login_mail?.requestFocus() // 해당 EditText로 커서를 포커스 한다.
                return@OnClickListener  // 포커스 하러 돌아가! return 안하면 제기능 못 함
            }

            // 패스워드 빈 칸 체크
            if (TextUtils.isEmpty(mPass))
            // 패스워드 입력칸이 비어있으면 경고하기
            { // 로그인 실패
                val anim =
                    AnimationUtils.loadAnimation(
                        applicationContext,
                        R.anim.alpha
                                                ) // 애니메이션 설정이 담긴 리소스 파일을 anim에 담는다
                loggin_button?.startAnimation(anim)  // 애니메이션 설정이 담긴 anim을 로그인 버튼에 적용한다. 로그인 버튼 흔들기

                Toast.makeText(this@Activity_Login, "패스워드를 입력하세요!", Toast.LENGTH_SHORT)
                    .show() // 토스트 메시지를 띄우고
                login_password?.requestFocus() // 해당 EditText로 커서를 포커스 한다.
                return@OnClickListener  // return 안하면 제기능 못 함
            }

            // 이메일과 패스워드를 모두 입력 받으면
            // 로그인 메소드 실행하기
            if (!mEmail.isEmpty() || !mPass.isEmpty())
            {
                // 로그인 메소드는 onCreate 바로 아래 위치에 작성됨.
                Login(mEmail, mPass)
            }
            else
            {
                // 이메일이나 패스워드가 입력되지 않으면 에러로 간주하기
                login_mail?.setError("이메일을 입력해주세요")
                login_password?.setError("패스워드를 입력해주세요")
                Toast.makeText(this@Activity_Login, "문제 발생.\n다시 시도해주세요.", Toast.LENGTH_SHORT).show()

                // 로딩바 비활성
                login_loading?.setVisibility(View.GONE)

                // 로그인 재시도를 위해 로그인 버튼 활성화
                login_password?.setVisibility(View.VISIBLE)
            }
        })

        // 회원가입 액티비티로 이동하기
        go_reg?.setOnClickListener(View.OnClickListener {
            e(TAG, "button click go_reg")
        })
    }

    // 로그인 메소드
    private fun Login(email: String, password: String)
    {
        e(TAG, "onResponse: Login()실행됨")

        // 로그인 버튼을 누르면 프로그레스 다이얼로그를 활성화 하기.
        login_loading?.setVisibility(View.VISIBLE)

        // 로그인 버튼을 누르면 로그인 버튼을 비활성화 하기.
        loggin_button?.setVisibility(View.GONE)

        val stringRequest =
            object : StringRequest(Method.POST, URL_LOGIN, Response.Listener { response ->
                try
                {
                    e(TAG, "onResponse: Login = $response")

                    // JSONObject = json에 담긴 값에서 필요한 문자열을 추출하기 위해 필요한 메소드
                    val jsonObject = JSONObject(response)

                    // json에 담긴 문자열 중 success를 추출한다.
                    val success = jsonObject.getString("success") // = 1
                    e(TAG, "onResponse: jsonObject.getString success = $success")

                    val jsonArray = jsonObject.getJSONArray("login")
                    e(TAG, "onResponse: jsonObject.getJSONArray login = $jsonArray")

                    if (success == "1")
                    {
                        for (i in 0 until jsonArray.length())
                        {
                            val `object` = jsonArray.getJSONObject(i)

                            val name = `object`.getString("name").trim { it <= ' ' } // 김성훈
                            val email = `object`.getString("email").trim { it <= ' ' } // aa@aa.com
                            val photo = `object`.getString("photo").trim { it <= ' ' } // 사진
                            val id = `object`.getString("id").trim { it <= ' ' } // 유저번호
                            val type = `object`.getString("type")
                                .trim { it <= ' ' } // 유저 타입 구분하기(1: 클라이언트, 2: 바리스타)

                            e(TAG, "type: $type")
                            e(TAG, "photo: $photo")

                            sessionManager?.createSession(name, email, id, type, photo)

                            // 로그인 액티비티에서 홈 액티비티로 이동하기.
                            val intent = Intent(this@Activity_Login, Activity_Main::class.java)

                            intent.putExtra("name", name)
                            intent.putExtra("email", email)
                            startActivity(intent)

                            // 로그인 완료 후 로딩 비활성화
                            login_loading?.setVisibility(View.GONE)

                            // 액티비티 종료
                            finish()
                        }
                    }
                    else
                    {
                        Toast.makeText(
                            this@Activity_Login,
                            "이메일 또는 비밀번호가 일치하지 않습니다.",
                            Toast.LENGTH_SHORT
                                      ).show()
                        login_loading?.setVisibility(View.GONE)
                        loggin_button?.setVisibility(View.VISIBLE)
                    }
                }
                catch (e: JSONException)
                {
                    // json 결과값에 아무것도 담겨있지 않거나
                    // success에 포함된 결과값이 0이면 로그인 실패로 간주함.
                    e.printStackTrace()

                    // 로딩 비활성화 후 로그인 버튼 다시 활성화
                    login_loading?.setVisibility(View.GONE)
                    loggin_button?.setVisibility(View.VISIBLE)

                    // 로그인 실패결과 알림.
                    Toast.makeText(
                        this@Activity_Login,
                        "문제발생\n 다시 시도해주세요\n$e",
                        Toast.LENGTH_SHORT
                                  )
                        .show()

                    // 문제 확인용 로그 기록
                    Log.e("JSONException e", "에러: LOGIN = $e")
                }
            },
                Response.ErrorListener { error ->
                    login_loading?.setVisibility(View.GONE)
                    loggin_button?.setVisibility(View.VISIBLE)
                    Toast.makeText(
                        this@Activity_Login,
                        "이메일 또는 비밀번호가 일치하지 않습니다.",
                        Toast.LENGTH_SHORT
                                  ).show()

                    e("VolleyError", "에러: $error")
                })
            {

                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String>
                {
                    val params = HashMap<String, String>()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }

        // requestQueue로 로그인 결과값 요청을 시작한다.
        val requestQueue = Volley.newRequestQueue(this)

        // stringRequest메소드에 기록한 내용들로 requestQueue를 시작한다.
        requestQueue.add(stringRequest)
    }
}
