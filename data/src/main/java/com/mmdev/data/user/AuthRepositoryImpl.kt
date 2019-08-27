package com.mmdev.data.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.domain.user.model.User
import com.mmdev.domain.user.repository.AuthRepository
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


//todo: save user locally

@Singleton
class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,
                                             private val db: FirebaseFirestore) : AuthRepository {

	companion object {
		private const val GENERAL_COLLECTION_REFERENCE = "users"

	}

	override fun handleUserExistence(userId: String): Single<User> {
		return Single.create(SingleOnSubscribe<User> { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE).document(userId)
			ref.get().addOnCompleteListener { task ->
				if (task.isSuccessful) {
					val document = task.result
					if (document!!.exists()) {
						val user = document.toObject(User::class.java)
						//saveProfile(applicationContext, mProfileModel)
						//profileViewModel!!.setProfileModel(mProfileModel)
						emitter.onSuccess(user!!)
					}
					else emitter.onError(Exception("User do not exist"))
				}
				else emitter.onError(Exception("task is not successful"))
			}
		}).subscribeOn(Schedulers.io())
	}

	override fun signup(user: User): Single<User> {
		return Single.create(SingleOnSubscribe<User> { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE).document(user.userId)
			ref.set(user)
				.addOnSuccessListener { emitter.onSuccess(user) }
				.addOnFailureListener{ task -> emitter.onError(task) }
		}).subscribeOn(Schedulers.io())
	}



	private fun saveProfile(user: User){

	}
}


