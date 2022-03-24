package com.plcoding.dictionary.feature_dictionary.domain.model

import com.plcoding.dictionary.feature_dictionary.data.remote.dto.LicenseXDto

data class Phonetic(
    val audio: String,
    val license: LicenseXDto,
    val sourceUrl: String,
    val text: String
)
