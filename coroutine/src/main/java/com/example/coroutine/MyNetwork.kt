package com.example.coroutine

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * 网络操作模块
 *
 * @author 高超（gaochao.cc）
 * @since 2023/12/14
 */

interface ITitleService {
    @GET("next/title/json")
    suspend fun nextTitle(): String
}

val titleService: ITitleService by lazy {
    val okhttpClient = OkHttpClient.Builder()
        .addInterceptor(SkipNetworkInterceptor())
        .build()

    val retrofit = Retrofit.Builder()
        .client(okhttpClient)
        .baseUrl("https://localhost/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    retrofit.create(ITitleService::class.java)
}


private val FAKE_TITLES = listOf(
    "Hello, coroutines!",
    "My favorite feature",
    "Async made easy",
    "Coroutines by example",
    "Check out the Advanced Coroutines codelab next!"
)

class SkipNetworkInterceptor : Interceptor {
    private val gson = Gson()

    private var lastTitle = ""
    private var attempts = 0

    private fun wantError() = attempts++ % 5 == 0

    // 模拟网络请求的耗时操作
    private fun pretendToBlockNetwork() = Thread.sleep(500)

    override fun intercept(chain: Interceptor.Chain): Response {
        pretendToBlockNetwork()
        return if (wantError()) {
            makeErrorResult(chain.request())
        } else {
            makeOkResult(chain.request())
        }
    }

    private fun makeOkResult(request: Request): Response {
        var nextTitle = lastTitle
        while (nextTitle == lastTitle) {
            nextTitle = FAKE_TITLES.random()
        }
        lastTitle = nextTitle
        return Response.Builder()
            .code(200)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("OK")
            .body(
                ResponseBody.create(
                    MediaType.get("application/json"),
                    gson.toJson(nextTitle)
                )
            )
            .build()
    }

    private fun makeErrorResult(request: Request): Response {
        return Response.Builder()
            .code(500)
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .message("Server is dead.")
            .body(
                ResponseBody.create(
                    MediaType.get("application/json"),
                    gson.toJson(mapOf("cause" to "I don't know why."))
                )
            )
            .build()
    }
}

