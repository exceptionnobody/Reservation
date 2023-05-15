package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.Struttura
import javax.inject.Inject

@HiltViewModel
class StrutturaViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {
    val structures: LiveData<List<Struttura>> = businessLogic.getAllStructures()

    fun insertStructure(structure: Struttura) {
        businessLogic.insertStructure(structure)
    }
}