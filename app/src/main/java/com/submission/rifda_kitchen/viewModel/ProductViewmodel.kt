package com.submission.rifda_kitchen.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.submission.rifda_kitchen.model.MakananBeratModel
import com.submission.rifda_kitchen.model.MakananRinganModel
import com.submission.rifda_kitchen.repository.Repository
import kotlinx.coroutines.launch

class ProductViewmodel(private val repository: Repository) : ViewModel() {
    private val _makananBeratList = MutableLiveData<List<MakananBeratModel>>()
    var makananBeratList: LiveData<List<MakananBeratModel>> = _makananBeratList

    private val _makananRinganList = MutableLiveData<List<MakananRinganModel>>()
    var makananRinganList: LiveData<List<MakananRinganModel>> = _makananRinganList

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchMakananBerat()
        fetchMakananRingan()
    }

    fun fetchMakananBerat() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.fetchMakananBerat { list ->
                    _makananBeratList.postValue(list)
                    _isLoading.value = false
                }

            } catch (e: Exception) {
                _isLoading.value = true
            }
        }
    }

    fun fetchMakananRingan() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
               repository.fetchMakananRingan { list ->
                   _makananRinganList.postValue(list)
                   _isLoading.value = false
               }

            } catch (e: Exception) {
                _isLoading.value = true
                Log.d("MakananRingan", "Fetching gagal ")
            }
        }
    }
}