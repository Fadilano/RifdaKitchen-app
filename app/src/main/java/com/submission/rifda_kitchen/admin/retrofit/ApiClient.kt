package com.submission.rifda_kitchen.admin.retrofit

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64

object RetrofitClient {

    private const val MIDTRANS_BASE_URL = "https://api.sandbox.midtrans.com/"

    // Use your Midtrans server key
    private const val SERVER_KEY = "SB-Mid-server-Zbsb-T_jALFkRUFnHZadfbd2"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMidtransService(): MidtransApiService {
        // Logging for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Adding Basic Authorization with the server key
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header(
                        "Authorization",
                        "Basic " + Base64.getEncoder().encodeToString("$SERVER_KEY:".toByteArray())
                    )
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(MIDTRANS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MidtransApiService::class.java)
    }
}
