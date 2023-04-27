package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.PosRes
import java.util.*
import javax.inject.Inject


@HiltViewModel
class PosResViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    val posRes: LiveData<List<PosRes>> = businessLogic.getAllPosRes()

    fun insertPosRes(posRes: PosRes) {
        businessLogic.insertNewPos(posRes)
    }

    fun updatePosRes(posRes: PosRes) {
        businessLogic.updatePosRes(posRes)
    }

    fun changeDatePosRes(id: Int, newDate: Date) {
        businessLogic.updateDatePosRes(id, newDate)
    }
}