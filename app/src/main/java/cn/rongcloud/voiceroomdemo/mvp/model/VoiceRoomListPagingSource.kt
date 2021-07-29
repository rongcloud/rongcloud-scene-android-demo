/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.voiceroomdemo.mvp.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.rongcloud.voiceroomdemo.common.LocalDataStore
import cn.rongcloud.voiceroomdemo.net.RetrofitManager
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomBean
import cn.rongcloud.voiceroomdemo.net.api.bean.respond.VoiceRoomListBean
import io.reactivex.rxjava3.core.Observable

/**
 * @author gusd
 * @Date 2021/06/09
 */
private const val TAG = "VoiceRoomListPagingSour"

object VoiceRoomListPagingSource : PagingSource<Int, VoiceRoomBean>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, VoiceRoomBean> {
        return try {
            val page = params.key ?: 0
            var roomListBean = getVoiceRoomList(page, params.loadSize).blockingFirst()
            val result: List<VoiceRoomBean>? =
                roomListBean.data?.rooms
            LocalDataStore.saveBackGroundUrl(roomListBean.data?.images ?: emptyList())
            LoadResult.Page(
                data = result ?: emptyList(),
                nextKey = if (result.isNullOrEmpty() || result.size < params.loadSize) null else page + 1,
                prevKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private suspend fun getVoiceRoomList(page: Int, size: Int): Observable<VoiceRoomListBean> {
        return RetrofitManager.commonService.getRoomList(page, size).toObservable()
    }

    override fun getRefreshKey(state: PagingState<Int, VoiceRoomBean>): Int? {
        return state.anchorPosition
    }

    fun refreshDataDelay(time: Int = 1000) {
        invalidate()
    }


}