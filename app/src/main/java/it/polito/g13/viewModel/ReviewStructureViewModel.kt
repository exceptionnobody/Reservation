package it.polito.g13.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polito.g13.businesslogic.BusinessClass
import it.polito.g13.entities.review_struct
import javax.inject.Inject


@HiltViewModel
class ReviewStructureViewModel @Inject constructor (private val businessLogic: BusinessClass): ViewModel() {

    private val _singleReviewStructure = MutableLiveData<review_struct>()
    val singleReviewStructure: LiveData<review_struct> = _singleReviewStructure

    fun insertReview(review: review_struct) {
        businessLogic.createReview(review)
    }

    fun updateReview(review: review_struct) {
        businessLogic.updateReview(review)
    }

    fun getReviewById(reviewId: Int) {
        _singleReviewStructure.postValue(businessLogic.getReviewById(reviewId))
    }

    fun getReviewByStructureAndUserId(structureId: Int, userId: Int) {
        _singleReviewStructure.postValue(businessLogic.getReviewByStructureAndUserId(structureId, userId))
    }
}