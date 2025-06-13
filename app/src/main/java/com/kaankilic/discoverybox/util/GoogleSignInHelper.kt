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

