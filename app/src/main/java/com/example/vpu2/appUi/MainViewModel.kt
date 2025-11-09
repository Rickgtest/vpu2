package com.example.vpu2.appUi

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.vpu2.data.AppDatabase
import com.example.vpu2.data.UArchRepository
import com.example.vpu2.supabase.SupabaseClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val uArchRepository: UArchRepository
    private val _uArchData = MutableStateFlow<List<UArch>>(emptyList())
    val uArchData = _uArchData.asStateFlow()

    private val _filteredUArchData = MutableStateFlow<List<UArch>>(emptyList())
    val filteredUArchData = _filteredUArchData.asStateFlow()

    private val _selectedArch = MutableStateFlow("x86")
    val selectedArch = _selectedArch.asStateFlow()

    private val _status = MutableStateFlow("")
    val status = _status.asStateFlow()

    private val _isInitialLoadComplete = MutableStateFlow(false)

    init {
        val uArchDao = Room.databaseBuilder(
            application.applicationContext,
            AppDatabase::class.java, "uarch-database"
        ).build().uArchDao()
        uArchRepository = UArchRepository(uArchDao, SupabaseClient)

        viewModelScope.launch {
            uArchRepository.getUArchs().collectLatest {
                _uArchData.value = it
                filterArchitectures()
                if (!_isInitialLoadComplete.value && it.isNotEmpty()) {
                    _isInitialLoadComplete.value = true
                }
            }
        }

        refreshUArchs()
    }

    fun refreshUArchs() {
        viewModelScope.launch {
            try {
                if (isNetworkAvailable()) {
                    if (!_isInitialLoadComplete.value) {
                        _status.value = "Fetching data..."
                    }
                    uArchRepository.refreshUArchs()
                    _status.value = ""

                } else {
                    if (!_isInitialLoadComplete.value) {
                        _status.value = "No network connection, loading from cache."
                        delay(2000)
                        _status.value = ""
                    }
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error refreshing data", e)
                _status.value = "Error: ${e.message}"
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.let {
            it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }

    fun setSelectedArch(arch: String) {
        _selectedArch.value = arch
        filterArchitectures()
    }

    private fun filterArchitectures() {
        _filteredUArchData.value = when (_selectedArch.value) {
            "All" -> _uArchData.value
            else -> _uArchData.value.filter { it.arch == _selectedArch.value }
        }
    }

    fun getUArchByName(name: String): UArch? {
        return _uArchData.value.find { it.name == name }
    }
}