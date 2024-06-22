package com.example.splitwise

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.media.FaceDetector.Face
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.splitwise.databinding.ActivityLoginScreenBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginClient
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import io.realm.Realm.Transaction.OnSuccess
import io.realm.Realm.init
import org.json.JSONException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.facebook.FacebookCallback as FacebookCallback

class LoginScreen : AppCompatActivity() {
    val binding: ActivityLoginScreenBinding by lazy {
        ActivityLoginScreenBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(R.string.your_web_client_id.toString()).requestEmail().build()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
//      for google sign in
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
//      for facebook sign in
        FacebookSdk.sdkInitialize(LoginScreen@ this)
        callbackManager = CallbackManager.Factory.create()
        facebookLogin()

//      No Account Setup
        binding.DontHaveAccountButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))

        }
//      Google Button Setup
        binding.googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

//       Login Button Setup
        binding.loginButton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "All Fields are Required", Toast.LENGTH_SHORT).show()
            }
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()

                }
            }
        }


    }


    private fun facebookLogin() {
        loginManager = LoginManager.getInstance()
        callbackManager = CallbackManager.Factory.create()
        loginManager.run {
            registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val request =
                        GraphRequest.newMeRequest(result.accessToken) { `object`, response ->
                            `object`?.let {
                                try {
                                    val name = it.getString("name")
                                    val email = it.getString("email")
                                    val fbUserId = it.getString("id")

                                    disconnectFromFacebook()
                                    startActivity(
                                        Intent(
                                            this@LoginScreen, MainActivity::class.java
                                        )
                                    )
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    val parameters = Bundle().apply {
                        putString("fields", "id, name, email, gender, birthday")
                    }
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                    Log.v("LoginScreen", "---onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.v("LoginScreen", "----onError: ${error.message}")
                }
            })
        }
    }

    private fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),
            "me/permissions",
            null,
            HttpMethod.DELETE,
            GraphRequest.Callback { Response ->
                LoginManager.getInstance().logOut()
            }).executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    //    Launcher for google sign In
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(
                                this, "Successfully Signed in with Google", Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


//    For Printing Hashkey for facebook authentication
//    private fun printHashKeys() {
//        try{
//            val info:PackageInfo = packageManager.getPackageInfo("com.example.splitwise",PackageManager.GET_SIGNATURES)
//            for(signature in info.signatures){
//                val md = MessageDigest.getInstance("SHA")
//                md.update(signature.toByteArray())
//                Log.d("KeyHash :" , Base64.encodeToString(md.digest(),Base64.DEFAULT))
//            }
//        }
//        catch (e:PackageManager.NameNotFoundException) {
//        }
//        catch (e:NoSuchAlgorithmException){
//
//        }
//
//    }
//    Helping URL for facebook authentication:: https://www.geeksforgeeks.org/how-to-create-a-facebook-login-using-an-android-app/
}