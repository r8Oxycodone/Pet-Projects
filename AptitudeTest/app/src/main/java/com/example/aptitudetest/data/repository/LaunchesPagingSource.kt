package com.example.aptitudetest.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.aptitudetest.data.Body
import com.example.aptitudetest.data.Docs
import com.example.aptitudetest.data.Options
import com.example.aptitudetest.data.Query
import com.example.aptitudetest.data.Sort
import com.example.aptitudetest.data.SpaceXDataSource
import com.example.aptitudetest.data.UTCLaunchDate
import retrofit2.HttpException
import java.io.IOException

class LaunchesPagingSource(
    private val spaceXDataSource: SpaceXDataSource
) : PagingSource<Int, Docs>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Docs> {
        try {
            val pageNumber = params.key ?: 0
            val query = Query(UTCLaunchDate(utcLaunchDate = "2021-01-01T00:00:00.000Z"))
            val options = Options(limit = 10, page = pageNumber, sort = Sort(dateUtc = "desc"))
            val body = Body(query = query, options = options)
            val response = spaceXDataSource.getLaunches(body = body)
            val responseItems = checkNotNull(response.body()).docs
            val prevKey = if (pageNumber > 0) pageNumber.minus(1) else null
            val nextKey = if (responseItems.isNotEmpty()) pageNumber.plus(1) else null

            return LoadResult.Page(responseItems, prevKey, nextKey)

        } catch (e: IOException) {
            Log.d("IOException", "load: ${e.stackTraceToString()}")
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("HttpException", "load: ${e.stackTraceToString()}")
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Docs>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }
}