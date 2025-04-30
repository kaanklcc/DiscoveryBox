package com.kaankilic.discoverybox.util

import com.google.firebase.auth.AuthCredential

interface GoogleAuthViewModel {
    fun signInWithGoogleCredential(
        credential: AuthCredential,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    )
}
