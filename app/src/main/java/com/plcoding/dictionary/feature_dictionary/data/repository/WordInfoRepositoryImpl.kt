package com.plcoding.dictionary.feature_dictionary.data.repository

import com.plcoding.dictionary.core.util.Resource
import com.plcoding.dictionary.feature_dictionary.data.local.WordInfoDao
import com.plcoding.dictionary.feature_dictionary.data.remote.DictionaryApi
import com.plcoding.dictionary.feature_dictionary.domain.model.WordInfo
import com.plcoding.dictionary.feature_dictionary.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.internal.cacheGet
import retrofit2.HttpException
import java.io.IOException

class WordInfoRepositoryImpl(

    private val api: DictionaryApi,
    private val dao: WordInfoDao
) : WordInfoRepository {


    override fun getWordInfo(word: String): Flow<Resource<List<WordInfo>>> = flow {

        emit(Resource.Loading())

        val wordInfos = dao.getWordInfos(word).map {
            it.toWordInfo()
        }

        emit(Resource.Loading(data = wordInfos))


        try {

            val remoteWordInfos = api.getWordInfo(word)
            dao.deleteWordinfos(remoteWordInfos.map { it.word })
            dao.insertWordInfos(remoteWordInfos.map { it.toWordInfoEntity() })

        } catch (e: HttpException) {

            emit(
                Resource.Error(
                    message = "Oops, something went wrong",
                    data = wordInfos
                )
            )

        } catch (e: IOException) {
            emit(
                Resource.Error(
                    message = "Could not reach the server",
                    data = wordInfos
                )
            )
        }

        val newWordsInfos = dao.getWordInfos(word).map {

            it.toWordInfo()
        }
        emit(Resource.Success(newWordsInfos))

    }

}