package com.submission.rifda_kitchen.repository

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.submission.rifda_kitchen.R
import com.submission.rifda_kitchen.model.UserModel
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getFirebaseUser() = auth.currentUser

    suspend fun getUserRole(uid: String): String {
        return try {
            val roleSnapshot = database.child("users").child(uid).child("role").get().await()
            roleSnapshot.getValue(String::class.java) ?: "User"  // Default role jika tidak ada
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve role: ${e.message}", e)
            "User"  // Kembalikan default role jika terjadi error
        }
    }


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
                        Log.e(TAG, "Received an invalid Google ID token response", e)
                        false
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential: ${credential.type}")
                    false
                }
            }
            else -> {
                Log.e(TAG, "Invalid credential type")
                false
            }
        }
    }


    suspend fun firebaseAuthWithGoogle(idToken: String): Boolean {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                val roleSnapshot = database.child("users").child(uid).child("role").get().await()
                val existingRole = roleSnapshot.getValue(String::class.java)

                // Simpan data pengguna hanya jika tidak ada di database
                if (existingRole == null) {
                    val name = user.displayName ?: "Unknown Name"
                    val email = user.email ?: "Unknown Email"
                    val photoUrl = user.photoUrl?.toString() ?: "Unknown Photo Url"
                    saveUserData(uid, name, email, photoUrl, "User") // Default role sebagai User
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.w(TAG, "signInWithCredential:failure", e)
            false
        }
    }



    suspend fun saveUserData(
        uid: String,
        name: String,
        email: String,
        photoUrl: String,
        role: String = "User"
    ) {
        val user = UserModel(uid, name, email, photoUrl, role)
        try {
            database.child("users").child(uid).setValue(user).await()
            Log.d(TAG, "User data saved successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user data: ${e.message}", e)
        }
    }



    suspend fun signInWithEmail(email: String, password: String): Boolean {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                val role = getUserRole(uid)  // Ambil role dari database
                user.displayName?.let { name ->
                    val photoUrl = user.photoUrl?.toString() ?: "Unknown Photo Url"
                    // Jangan panggil saveUserData lagi di sini untuk mencegah overwrite
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: ${e.message}", e)
            false
        }
    }


    suspend fun signUpWithEmail(name: String, email: String, password: String): Boolean {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                val photoUrl = "Unknown Photo Url"  // Default jika tidak ada input foto
                val role = "User"  // Role default untuk pengguna baru
                saveUserData(uid, name, email, photoUrl, role)

                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user.updateProfile(profileUpdates).await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: ${e.message}", e)
            false
        }
    }


    companion object {
        private const val TAG = "AuthRepository"
    }
}
