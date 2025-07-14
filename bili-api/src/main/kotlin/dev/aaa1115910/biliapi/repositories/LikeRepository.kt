package dev.aaa1115910.biliapi.repositories

import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.video.OneClickTripleAction
import org.koin.core.annotation.Single

@Single
class LikeRepository(
    private val authRepository: AuthRepository
) {
    suspend fun checkVideoLike(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ): Boolean {
        return when (preferApiType) {
            ApiType.Web -> BiliHttpApi.checkVideoLiked(
                avid = aid,
                sessData = authRepository.sessionData
            )

            ApiType.App -> BiliHttpApi.checkVideoLiked(
                avid = aid,
                accessKey = authRepository.accessToken
            )
        }
    }

    suspend fun addVideoLike(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ) {
        val (success, message) = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = true,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = true,
                accessKey = authRepository.accessToken
            )
        }
        if (!success) {
            throw Exception("点赞失败: $message")
        }
    }

    suspend fun delVideoLike(
        aid: Long,
        preferApiType: ApiType = ApiType.Web
    ) {
        val (success, message) = when (preferApiType) {
            ApiType.Web -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = false,
                sessData = authRepository.sessionData,
                csrf = authRepository.biliJct
            )

            ApiType.App -> BiliHttpApi.sendVideoLike(
                avid = aid,
                like = false,
                accessKey = authRepository.accessToken
            )
        }
        if (!success) {
            throw Exception("取消点赞失败: $message")
        }
    }

    suspend fun sendVideoOneClickTripleAction(
        aid: Long,
        bvid: String? = null
    ): OneClickTripleAction? {
        val (success, message, data)
                = BiliHttpApi.sendVideoOneClickTripleAction(
            avid = aid,
            bvid = bvid, csrf = authRepository.biliJct ?: "",
            sessData = authRepository.sessionData!!,
        )
        if (!success) throw Exception("投币失败：$message")
        return data
    }

}
