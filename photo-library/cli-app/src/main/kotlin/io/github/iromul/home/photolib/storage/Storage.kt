package io.github.iromul.home.photolib.storage

import io.github.iromul.home.photolib.scripts.Result

interface Storage {

    fun clearStorage()

    fun storeCache(result: Result)

    fun retrieveCache(): Result
}
