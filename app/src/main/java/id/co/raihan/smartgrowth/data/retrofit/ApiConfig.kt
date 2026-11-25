package id.co.raihan.smartgrowth.data.retrofit

import id.co.raihan.smartgrowth.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    private const val BASE_URL =
        "https://innovillage-backend-125363554402.asia-southeast2.run.app/api/"

    private const val BASE_URL_AI =
        "https://innovillage-stunting-prediction-125363554402.asia-southeast2.run.app/"

    private fun provideClient(): OkHttpClient {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .followRedirects(true)
            .followSslRedirects(true)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun provideRetrofit(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideClient())
            .build()
    }

    fun getApiService(): ApiService {
        return provideRetrofit(BASE_URL)
            .create(ApiService::class.java)
    }

    fun getAiApiService(): AiApiService {
        return provideRetrofit(BASE_URL_AI)
            .create(AiApiService::class.java)
    }

//    fun getApiService(): ApiService {
//        val loggingInterceptor = if (BuildConfig.DEBUG) {
//            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
//        } else {
//            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
//        }
//        val client = OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .followRedirects(true)
//            .followSslRedirects(true)
//            .build()
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//        return retrofit.create(ApiService::class.java)
//    }
}