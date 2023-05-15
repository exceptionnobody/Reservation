package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import java.util.*
import javax.inject.Inject


@HiltViewModel
class PosResViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    val posRes: LiveData<List<PosRes>> = businessLogic.getAllPosRes()

    private val _listPosRes = MutableLiveData<List<PosRes>>()
    val listPosRes: LiveData<List<PosRes>> = _listPosRes

    private val _singlePosRes = MutableLiveData<PosRes>()
    val singlePosRes: LiveData<PosRes> = _singlePosRes

    fun insertPosRes(posRes: PosRes) {
        businessLogic.insertNewPos(posRes)
    }

    fun updatePosRes(posRes: PosRes) {
        businessLogic.updatePosRes(posRes)
    }

    fun changeDatePosRes(id: Int, newDate: Date) {
        businessLogic.updateDatePosRes(id, newDate)
    }

    fun getPosRes(sport: String, date: Date) {
        _listPosRes.postValue(businessLogic.getPosResSportDate(sport, date))
    }

    fun getPosResBySport(sport: String) {
        _listPosRes.postValue(businessLogic.getPosResBySport(sport))
    }

    fun getPostResBySportTime(sport: String, from: String, to: String) {
        _listPosRes.postValue(businessLogic.getPosResSportTime(sport, from, to))
    }

    fun getPosResById(posResId: Int) {
        _singlePosRes.postValue(businessLogic.getPosResSportById(posResId))
    }

    fun getPosResByStructure(sport: String, date: Date, struct: String) {
        _listPosRes.postValue(businessLogic.getPosResSportDateAndStruct(sport, date, struct))
    }

    fun getPosResByStructureSportDateAndTime(sport: String, date: Date, from: String, to: String, struct: String) {
        _listPosRes.postValue(businessLogic.getPosResStructureSportDateAndTime(sport, date, from, to, struct))
    }
}