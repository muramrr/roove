/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.03.20 18:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import android.net.Uri
import android.text.format.DateFormat
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.PhotoItem
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.remote.RemoteUserRepository
import com.mmdev.business.remote.entity.Report
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.schedulers.ExecuteSchedulers
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
class UserRepositoryRemoteImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                                   private val storage: StorageReference,
                                                   private val userWrapper: UserWrapper):
		RemoteUserRepository, BaseRepositoryImpl(firestore, userWrapper) {

	companion object {
		private const val REPORTS_COLLECTION_REFERENCE = "reports"

		private const val USER_BASE_INFO_FIELD = "baseUserInfo"
		private const val USER_MAIN_PHOTO_FIELD = "baseUserInfo.mainPhotoUrl"
		private const val USER_PHOTOS_LIST_FIELD = "photoURLs"

		private const val SECONDARY_FOLDER_STORAGE_IMG = "profilePhotos"
	}


	override fun deleteMatchedUser(matchedUserItem: MatchedUserItem): Completable =
		CompletableCreate { emitter ->
			val matchedUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.city)
				.collection(matchedUserItem.baseUserInfo.gender)
				.document(matchedUserItem.baseUserInfo.userId)

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

			//add to skipped collection
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.userId)
				.set(mapOf(USER_ID_FIELD to matchedUserItem.baseUserInfo.userId))

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


			//mark that conversation no need to be exists
			firestore
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(matchedUserItem.conversationId)
				.set(mapOf(CONVERSATION_DELETED_FIELD to true))
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }


		}.subscribeOn(ExecuteSchedulers.io())

	override fun deletePhoto(photoItem: PhotoItem, userItem: UserItem, isMainPhotoDeleting: Boolean): Completable =
		CompletableCreate { emitter ->
			val ref = fillUserGeneralRef(userItem.baseUserInfo)

			userItem.photoURLs.remove(photoItem)

			if (isMainPhotoDeleting) {
				ref.update(USER_MAIN_PHOTO_FIELD, userItem.photoURLs[0].fileUrl)
				firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
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
			val ref = fillUserGeneralRef(currentUser.baseUserInfo)
			ref.delete()
				.addOnSuccessListener {
					firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
						.document(currentUser.baseUserInfo.userId)
						.delete()
						.addOnCompleteListener { emitter.onComplete() }
						.addOnFailureListener { emitter.onError(it)  }
				}.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())


	override fun getRequestedUserItem(baseUserInfo: BaseUserInfo): Single<UserItem> =
		SingleCreate<UserItem> { emitter ->
			val ref = fillUserGeneralRef(baseUserInfo)
			ref.get()
				.addOnSuccessListener { if (it.exists() && it != null) emitter.onSuccess(it.toObject(UserItem::class.java)!!) }
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())

	override fun submitReport(report: Report): Completable =
		CompletableCreate { emitter ->
			report.reportId = firestore.collection(REPORTS_COLLECTION_REFERENCE).document().id
			firestore.collection(REPORTS_COLLECTION_REFERENCE)
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
					firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
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
							fillUserGeneralRef(userItem.baseUserInfo)
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