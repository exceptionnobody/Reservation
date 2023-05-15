package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Reservation
import it.polito.g13.entities.Sports
import it.polito.g13.entities.User
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SportsViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel(){

    private val _singleSports = MutableLiveData<Sports>()
    val sports: MutableLiveData<Sports> = _singleSports

    fun getSportsById(id: Int) {
        _singleSports.postValue(businessLogic.getSportsById(id))
    }

    fun updateSports(sports:Sports) {
        businessLogic.changeSports(sports)
    }

    fun insertSports(sports: Sports){
        businessLogic.insertSports(sports)
    }
}