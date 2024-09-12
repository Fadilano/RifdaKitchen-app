package com.submission.rifda_kitchen.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.view.LoginActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun signInWithGoogleIdToken(context: Context): Boolean {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            handleSignIn(result)
        } catch (e: GetCredentialException) {
            Log.d(TAG, "Error retrieving credential: ${e.message}")
            false
        }
    }

    suspend fun signOut(context: Context) {
        val credentialManager = CredentialManager.create(context)
        auth.signOut()
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }


    suspend fun handleSignIn(result: GetCredentialResponse): Boolean {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        firebaseAuthWithGoogle(idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                        false
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                    false
                }
            }

            else -> false
        }
    }

    suspend fun firebaseAuthWithGoogle(idToken: String): Boolean {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(credential).await().user != null
        } catch (e: Exception) {
            Log.w(TAG, "signInWithCredential:failure", e)
            false
        }
    }

    companion object {
        private const val TAG = "AuthRepository"
    }
}
