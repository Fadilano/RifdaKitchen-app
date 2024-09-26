package com.submission.rifda_kitchen.admin.retrofit

import android.os.Build
import androidx.annotation.RequiresApi
import com.submission.rifda_kitchen.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Base64


object ApiClient {

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMidtransService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header(
                        "Authorization",
                        "Basic " + Base64.getEncoder()
                            .encodeToString("${BuildConfig.SERVER_KEY}:".toByteArray())
                    )
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.MIDTRANS_BASE_URL)  // Now it should resolve correctly
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        return retrofit

    }
}
