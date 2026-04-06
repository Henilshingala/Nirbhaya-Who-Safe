package com.womensafety.app.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * Retrofit API Interface for WhatsApp Business API
 * Replicates the axios.post call from backend's contact.service.js line 710
 */
interface WhatsAppApiService {
    
    @POST(".")  // The full URL is provided in the base URL
    suspend fun sendMessage(
        @Body request: WhatsAppMessageRequest
    ): Response<WhatsAppMessageResponse>
}

/**
 * WhatsApp API Client Factory
 * Configuration matches backend's axios setup
 */
object WhatsAppApiClient {
    
    private const val TIMEOUT_SECONDS = 30L
    
    fun create(
        apiBaseUrl: String,
        bearerToken: String
    ): WhatsAppApiService {
        
        // Logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        // Authorization header interceptor
        // Matches: Authorization: `Bearer ${Whosafe_Wtalk_Api_Key}`
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .header("Authorization", "Bearer $bearerToken")
                .header("Content-Type", "application/json")
                .build()
            chain.proceed(requestWithAuth)
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        return retrofit.create(WhatsAppApiService::class.java)
    }
}
