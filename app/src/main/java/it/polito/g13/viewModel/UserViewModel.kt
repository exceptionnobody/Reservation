package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.PosRes
import it.polito.g13.entities.Reservation
import it.polito.g13.entities.User
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    private val _singleUser = MutableLiveData<User>()
    val user: MutableLiveData<User> = _singleUser

    fun getPosResById(id: Int) {
        _singleUser.postValue(businessLogic.getUserById(id))
    }

    fun updateUser(user:User) {
        businessLogic.changeUserInfo(user)
    }

    fun insertUser(user:User) {
        businessLogic.insertUser(user)
    }

}