/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.repository.user

import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mmdev.business.data.PhotoItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.business.remote.Report
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.ExecuteSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Current user related access to db to manipulate own data
 */

@Singleton
class UserRepositoryRemoteImpl @Inject constructor(
	private val fs: FirebaseFirestore,
	private val storage: StorageReference,
	private val userWrapper: UserWrapper
): RemoteUserRepository, BaseRepositoryImpl(fs, userWrapper) {

	companion object {
		private const val REPORTS_COLLECTION_REFERENCE = "reports"

		private const val USER_BASE_INFO_FIELD = "baseUserInfo"
		private const val USER_MAIN_PHOTO_FIELD = "baseUserInfo.mainPhotoUrl"
		private const val USER_PHOTOS_LIST_FIELD = "photoURLs"

		private const val SECONDARY_FOLDER_STORAGE_IMG = "profilePhotos"
	}


	override fun deleteMatchedUser(matchedUserItem: MatchedUserItem): Completable =
		CompletableCreate { emitter ->
			reInit()
			val matchedUserDocRef = fs.collection(USERS_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.city)
				.collection(matchedUserItem.baseUserInfo.gender)
				.document(matchedUserItem.baseUserInfo.userId)

			matchedUserDocRef
				.get()
				.addOnSuccessListener {
					if (it.exists()){
						//partner delete from matched list
						matchedUserDocRef
							.collection(USER_MATCHED_COLLECTION_REFERENCE)
							.document(currentUserId)
							.delete()

						//partner delete from conversations list
						matchedUserDocRef
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(matchedUserItem.conversationId)
							.delete()

						//add to skipped collection
						matchedUserDocRef
							.collection(USER_SKIPPED_COLLECTION_REFERENCE)
							.document(currentUserId)
							.set(mapOf(USER_ID_FIELD to currentUserId))

						//add to skipped collection
						currentUserDocRef
							.collection(USER_SKIPPED_COLLECTION_REFERENCE)
							.document(matchedUserItem.baseUserInfo.userId)
							.set(mapOf(USER_ID_FIELD to matchedUserItem.baseUserInfo.userId))
					}
				}

			//current user delete from matched list
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.userId)
				.delete()

			//current user delete from conversations list
			currentUserDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(matchedUserItem.conversationId)
				.delete()
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(ExecuteSchedulers.io())

	override fun deletePhoto(photoItem: PhotoItem, userItem: UserItem, isMainPhotoDeleting: Boolean): Completable =
		CompletableCreate { emitter ->
			reInit()
			val ref = fillUserGeneralRef(userItem.baseUserInfo)

			userItem.photoURLs.remove(photoItem)

			if (isMainPhotoDeleting) {
				ref.update(USER_MAIN_PHOTO_FIELD, userItem.photoURLs[0].fileUrl)
				fs.collection(USERS_BASE_COLLECTION_REFERENCE)
					.document(userItem.baseUserInfo.userId)
					.update(USER_MAIN_PHOTO_FIELD, userItem.photoURLs[0].fileUrl)
				userItem.baseUserInfo.mainPhotoUrl = userItem.photoURLs[0].fileUrl
			}

			if (photoItem.fileName != "facebookPhoto")
				storage
					.child(GENERAL_FOLDER_STORAGE_IMG)
					.child(SECONDARY_FOLDER_STORAGE_IMG)
					.child(userItem.baseUserInfo.userId)
					.child(photoItem.fileName)
					.delete()
					.addOnFailureListener { emitter.onError(it) }

			ref.update(USER_PHOTOS_LIST_FIELD, FieldValue.arrayRemove(photoItem))

			userWrapper.setUser(userItem)

			emitter.onComplete()

		}.subscribeOn(ExecuteSchedulers.io())

	override fun deleteMyself(): Completable =
		CompletableCreate { emitter ->
			reInit()
			val ref = fillUserGeneralRef(currentUser.baseUserInfo)

			val matchedListener =
			ref.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					if (snapshots != null && snapshots.documents.isNotEmpty()) {
						for (doc in snapshots.documents)
							doc.reference.delete()
					}
					else Log.wtf(TAG, "matched empty or deleted")

			}

			val conversationsListener =
			ref.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					if (snapshots != null && snapshots.documents.isNotEmpty()) {
						for (doc in snapshots.documents)
							doc.reference.delete()
					}
					else Log.wtf(TAG, "conversation empty or deleted")
				}

			val skippedListener =
			ref.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					if (snapshots != null && snapshots.documents.isNotEmpty()) {
						for (doc in snapshots.documents)
							doc.reference.delete()
					}
					else Log.wtf(TAG, "skipped empty or deleted")
				}

			val likedListener =
			ref.collection(USER_LIKED_COLLECTION_REFERENCE)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					if (snapshots != null && snapshots.documents.isNotEmpty()) {
						for (doc in snapshots.documents)
							doc.reference.delete()
					}
					else Log.wtf(TAG, "liked empty or deleted")
				}

			//base delete
			fs.collection(USERS_BASE_COLLECTION_REFERENCE)
				.document(currentUserId)
				.delete()
				.addOnSuccessListener {
					//general delete
					matchedListener.remove()
					conversationsListener.remove()
					likedListener.remove()
					skippedListener.remove()
					ref.delete()
					emitter.onComplete()
				}.addOnFailureListener { emitter.onError(it) }

			emitter.setCancellable {
				matchedListener.remove()
				conversationsListener.remove()
				likedListener.remove()
				skippedListener.remove()
			}
		}.subscribeOn(ExecuteSchedulers.io())


	override fun getRequestedUserItem(baseUserInfo: BaseUserInfo): Single<UserItem> =
		SingleCreate<UserItem> { emitter ->
			if (baseUserInfo.city.isNotEmpty() &&
			    baseUserInfo.gender.isNotEmpty() &&
			    baseUserInfo.userId.isNotEmpty()) {
				val ref = fillUserGeneralRef(baseUserInfo)
				ref.get()
					.addOnSuccessListener {
						if (it.exists()) emitter.onSuccess(it.toObject(UserItem::class.java)!!)
						else emitter.onSuccess(UserItem(BaseUserInfo("DELETED")))
					}
					.addOnFailureListener { emitter.onError(it) }
			} else emitter.onSuccess(UserItem(BaseUserInfo("DELETED")))
		}.subscribeOn(ExecuteSchedulers.io())

	override fun submitReport(report: Report): Completable =
		CompletableCreate { emitter ->
			report.reportId = fs.collection(REPORTS_COLLECTION_REFERENCE).document().id
			fs.collection(REPORTS_COLLECTION_REFERENCE)
				.document(report.reportId)
				.set(report)
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())


	override fun updateUserItem(userItem: UserItem): Completable =
		CompletableCreate { emitter ->
			val ref = fillUserGeneralRef(userItem.baseUserInfo)
			ref.set(userItem)
				.addOnSuccessListener {
					fs.collection(USERS_BASE_COLLECTION_REFERENCE)
						.document(userItem.baseUserInfo.userId)
						.update(USER_BASE_INFO_FIELD, userItem.baseUserInfo)
						.addOnSuccessListener {
							userWrapper.setUser(userItem)
							emitter.onComplete()
						}
						.addOnFailureListener { emitter.onError(it) }
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())


	override fun uploadUserProfilePhoto(photoUri: String, userItem: UserItem): Observable<HashMap<Double, List<PhotoItem>>> =
		ObservableCreate<HashMap<Double, List<PhotoItem>>> { emitter ->
			val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss",
			                                  Date()).toString() + "_user_photo.jpg"
			val storageRef = storage
				.child(GENERAL_FOLDER_STORAGE_IMG)
				.child(SECONDARY_FOLDER_STORAGE_IMG)
				.child(userItem.baseUserInfo.userId)
				.child(namePhoto)
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnProgressListener {
					val progress = (99.0 * it.bytesTransferred) / it.totalByteCount
					emitter.onNext(hashMapOf(progress to emptyList()))
				}
				.addOnSuccessListener {
					storageRef
						.downloadUrl
						.addOnSuccessListener {
							val uploadedPhotoItem = PhotoItem(fileName = namePhoto,
							                                  fileUrl = it.toString())
							currentUserDocRef
								.update(USER_PHOTOS_LIST_FIELD, FieldValue.arrayUnion(uploadedPhotoItem))

							userItem.photoURLs.add(uploadedPhotoItem)
							emitter.onNext(hashMapOf(100.00 to userItem.photoURLs))
							emitter.onComplete()
							userWrapper.setUser(userItem)
						}
						.addOnFailureListener { emitter.onError(it) }
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable { uploadTask.cancel() }
		}.subscribeOn(ExecuteSchedulers.io())
}