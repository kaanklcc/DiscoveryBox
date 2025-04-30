package com.kaankilic.discoverybox.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kaankilic.discoverybox.R
import com.kaankilic.discoverybox.viewmodel.KayitSayfaViewModel

/*object GoogleSignInHelper {

    fun signInWithGoogle(
        context: Context,
        launcher: ActivityResultLauncher<android.content.Intent>,
        //activity: Activity
    ) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun handleGoogleSignInResult(
        context: Context,
        data: android.content.Intent?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        val userData = hashMapOf(
                            "ad" to (user?.displayName ?: ""),
                            "soyad" to "",
                            "email" to (user?.email ?: ""),
                            "usedFreeTrial" to false,
                            "premium" to false
                        )

                        val userRef = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user!!.uid)

                        userRef.get().addOnSuccessListener { document ->
                            if (!document.exists()) {
                                userRef.set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Google ile giriş başarılı", Toast.LENGTH_SHORT).show()
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Kullanıcı kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                                        onFailure()
                                    }
                            } else {
                                // Kullanıcı zaten kayıtlı
                                Toast.makeText(context, "Google ile giriş başarılı (kayıtlı kullanıcı)", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Kullanıcı bilgisi alınamadı: ${it.message}", Toast.LENGTH_SHORT).show()
                            onFailure()
                        }
                    } else {
                        Toast.makeText(context, "Google giriş başarısız", Toast.LENGTH_SHORT).show()
                        onFailure()
                    }
                }

        } catch (e: ApiException) {
            Toast.makeText(context, "Google giriş hatası: ${e.message}", Toast.LENGTH_SHORT).show()
            onFailure()
        }
    }
}*/

object GoogleSignInHelper {

    fun signInWithGoogle(
        context: Context,
        launcher: ActivityResultLauncher<Intent>
    ) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Firebase'den alınan ID
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, gso)
        launcher.launch(googleSignInClient.signInIntent)
    }

    /*fun handleGoogleSignInResult(
        data: Intent?,
        viewModel: KayitSayfaViewModel,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            viewModel.signInWithGoogleCredential(credential,
                onSuccess = { onSuccess() },
                onFailure = { onFailure() }
            )

        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "ApiException: ${e.message}")
            onFailure()
        }
    }*/

    fun handleGoogleSignInResult(
        data: Intent?,
        viewModel: GoogleAuthViewModel, // ✅ Artık hem kayıt hem giriş ViewModel'i olabilir
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)

            viewModel.signInWithGoogleCredential(
                credential,
                onSuccess = { onSuccess() },
                onFailure = { onFailure() }
            )

        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "ApiException: ${e.message}")
            onFailure()
        }
    }

}

