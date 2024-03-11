package com.example.whatsapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.whatsapp.MainActivity
import com.example.whatsapp.R
import com.example.whatsapp.databinding.ActivityOtpactivityBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var verifyBtn:Button
    private lateinit var resendTv:TextView

    private lateinit var OTP:String
    private lateinit var resendToken:PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNumber:String

    private lateinit var inputOTP1: EditText
    private lateinit var inputOTP2: EditText
    private lateinit var inputOTP3: EditText
    private lateinit var inputOTP4: EditText
    private lateinit var inputOTP5: EditText
    private lateinit var inputOTP6: EditText


    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)

        OTP=intent.getStringExtra("OTP").toString()
        resendToken=intent.getParcelableExtra("resendToken")!!
        phoneNumber=intent.getStringExtra("phoneNumber")!!
        init()
        progressBar.visibility= View.INVISIBLE
        addTextChangeListener()
        resendOTPTvVisibility()

        resendTv.setOnClickListener{
            resendOTP()
            resendOTPTvVisibility()
        }
        verifyBtn.setOnClickListener {
            val typedOTP =
                (inputOTP1.text.toString() + inputOTP2.text.toString() + inputOTP3.text.toString()
                        + inputOTP4.text.toString() + inputOTP5.text.toString() + inputOTP6.text.toString())
            if(typedOTP.isNotEmpty()){
                if(typedOTP.length==6){
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(OTP,typedOTP)
                    signInWithPhoneCredential(credential)
                }
                else{
                    Toast.makeText(this, "Please enter correct otp",Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Please enter otp",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addTextChangeListener() {
        inputOTP1.addTextChangedListener(EditTextWatcher(inputOTP1))
        inputOTP2.addTextChangedListener(EditTextWatcher(inputOTP2))
        inputOTP3.addTextChangedListener(EditTextWatcher(inputOTP3))
        inputOTP4.addTextChangedListener(EditTextWatcher(inputOTP4))
        inputOTP5.addTextChangedListener(EditTextWatcher(inputOTP5))
        inputOTP6.addTextChangedListener(EditTextWatcher(inputOTP6))
    }

    private fun resendOTPTvVisibility() {
        inputOTP1.setText("")
        inputOTP2.setText("")
        inputOTP3.setText("")
        inputOTP4.setText("")
        inputOTP5.setText("")
        inputOTP6.setText("")
        resendTv.visibility = View.INVISIBLE
        resendTv.isEnabled = false

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            resendTv.visibility = View.VISIBLE
            resendTv.isEnabled = true
        }, 60000)
    }

    private fun resendOTP() {
        val options= PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {
                task -> if(task.isSuccessful){
                    Toast.makeText(this,"Authenticate successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProfileActivity::class.java))
            }
                else{
                    Log.d("TAG","signInWithPhoneAuthCredential: ${task.exception.toString()}")

            }
                progressBar.visibility=View.VISIBLE
            }
    }
    private val callbacks= object: PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneCredential(credential)
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            if(p0 is FirebaseAuthInvalidCredentialsException){
                Log.d("TAG", "onVerificationFailed: ${p0.toString()}")
            }
            else if(p0 is FirebaseTooManyRequestsException){
                Log.d("TAG", "onVerificationFailed: ${p0.toString()}")
            }
            progressBar.visibility=View.VISIBLE
        }

        override fun onCodeSent(verificationID: String, token: PhoneAuthProvider.ForceResendingToken) {
            OTP=verificationID
            resendToken=token
        }
    }
    private fun init(){
        auth=FirebaseAuth.getInstance()
        progressBar=findViewById(R.id.otpProgresBar)
        verifyBtn=findViewById(R.id.verifyOtpBtn)
        inputOTP1=findViewById(R.id.otpEditText1)
        inputOTP2=findViewById(R.id.otpEditText2)
        inputOTP3=findViewById(R.id.otpEditText3)
        inputOTP4=findViewById(R.id.otpEditText4)
        inputOTP5=findViewById(R.id.otpEditText5)
        inputOTP6=findViewById(R.id.otpEditText6)
        resendTv=findViewById(R.id.textView)
    }
    inner class EditTextWatcher(private val view:View):TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) inputOTP2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) inputOTP3.requestFocus() else if (text.isEmpty()) inputOTP1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) inputOTP4.requestFocus() else if (text.isEmpty()) inputOTP2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) inputOTP5.requestFocus() else if (text.isEmpty()) inputOTP3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) inputOTP6.requestFocus() else if (text.isEmpty()) inputOTP4.requestFocus()
                R.id.otpEditText6 -> if (text.isEmpty()) inputOTP5.requestFocus()

            }
        }

    }
}