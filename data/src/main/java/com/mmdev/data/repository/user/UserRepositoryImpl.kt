/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.datasource.UserDataSource
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.photo.PhotoItem
import com.mmdev.domain.photo.PhotoItem.Companion.FACEBOOK_PHOTO_NAME
import com.mmdev.domain.user.IUserRepository
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.domain.user.data.ReportType
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate
import java.util.*
import javax.inject.Inject

/**
 * [IUserRepository]
 * Current user related access to db to manipulate own data
 */

class UserRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore,
	private val storage: StorageReference,
	private val userDataSource: UserDataSource
): BaseRepository(), IUserRepository {

	companion object {
		private const val REPORTS_COLLECTION = "reports"
		
		private const val USER_MAIN_PHOTO_FIELD = "baseUserInfo.mainPhotoUrl"
		private const val USER_PHOTOS_LIST_FIELD = "photoURLs"

		private const val SECONDARY_FOLDER_STORAGE_IMG = "profilePhotos"
	}


	override fun deleteMatchedUser(
        user: UserItem,
        matchedUserItem: MatchedUserItem
	): Single<Unit> = deleteFromMatch(
		userForWhichDelete = user.baseUserInfo,
		userWhomToDelete = matchedUserItem.baseUserInfo,
		conversationId = matchedUserItem.conversationId
	).zipWith(
		deleteFromMatch(
			userForWhichDelete = matchedUserItem.baseUserInfo,
			userWhomToDelete = user.baseUserInfo,
			conversationId = matchedUserItem.conversationId
		),
		BiFunction { t1, t2 -> return@BiFunction }
	).subscribeOn(MySchedulers.io())
	
	private fun deleteFromMatch(
        userForWhichDelete: BaseUserInfo,
        userWhomToDelete: BaseUserInfo,
        conversationId: String
	) = Single.zip(
		// delete from matches
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(USER_MATCHED_COLLECTION)
			.document(userWhomToDelete.userId)
			.delete()
			.asSingle(),
			
		// delete from conversations
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(conversationId)
			.delete()
			.asSingle(),
			
		// add to skipped collection
		fs.collection(USERS_COLLECTION)
			.document(userForWhichDelete.userId)
			.collection(USER_SKIPPED_COLLECTION)
			.document(userWhomToDelete.userId)
			.set(mapOf(USER_ID_FIELD to userWhomToDelete.userId))
			.asSingle(),
		Function3 { t1, t2, t3 -> return@Function3 }
	)
	
	

	override fun deletePhoto(
        userItem: UserItem,
        photoItem: PhotoItem,
        isMainPhotoDeleting: Boolean
	): Completable = userDataSource.updateFirestoreUserField(
		id = userItem.baseUserInfo.userId,
		field = USER_PHOTOS_LIST_FIELD,
		value = FieldValue.arrayRemove(photoItem)
	).concatWith {
		if (isMainPhotoDeleting) {
			userDataSource.updateFirestoreUserField(
				id = userItem.baseUserInfo.userId,
				field = USER_MAIN_PHOTO_FIELD,
				value = userItem.photoURLs.minus(photoItem)[0].fileUrl
			)
		}
	}.andThen {
		// if we are deleting not a facebook-provided photo -> delete also on storage
		
		if (photoItem.fileName != FACEBOOK_PHOTO_NAME)
			storage
				.child(GENERAL_FOLDER_STORAGE_IMG)
				.child(SECONDARY_FOLDER_STORAGE_IMG)
				.child(userItem.baseUserInfo.userId)
				.child(photoItem.fileName)
				.delete()
	}

	override fun deleteMyself(user: UserItem): Completable = CompletableCreate { emitter ->
		
		val ref = fs.collection(USERS_COLLECTION).document(user.baseUserInfo.userId)

		val matchedListener =
		ref.collection(USER_MATCHED_COLLECTION)
			.addSnapshotListener { snapshots, e ->
				if (e != null) {
					emitter.onError(e)
					return@addSnapshotListener
				}
				if (snapshots != null && snapshots.documents.isNotEmpty()) {
					for (doc in snapshots.documents)
						doc.reference.delete()
				}
				else logDebug(TAG, "matched empty or deleted")

		}

		val conversationsListener =
		ref.collection(CONVERSATIONS_COLLECTION)
			.addSnapshotListener { snapshots, e ->
				if (e != null) {
					emitter.onError(e)
					return@addSnapshotListener
				}
				if (snapshots != null && snapshots.documents.isNotEmpty()) {
					for (doc in snapshots.documents)
						doc.reference.delete()
				}
				else logDebug(TAG, "conversation empty or deleted")
			}

		val skippedListener =
		ref.collection(USER_SKIPPED_COLLECTION)
			.addSnapshotListener { snapshots, e ->
				if (e != null) {
					emitter.onError(e)
					return@addSnapshotListener
				}
				if (snapshots != null && snapshots.documents.isNotEmpty()) {
					for (doc in snapshots.documents)
						doc.reference.delete()
				}
				else logDebug(TAG, "skipped empty or deleted")
			}

		val likedListener =
		ref.collection(USER_LIKED_COLLECTION)
			.addSnapshotListener { snapshots, e ->
				if (e != null) {
					emitter.onError(e)
					return@addSnapshotListener
				}
				if (snapshots != null && snapshots.documents.isNotEmpty()) {
					for (doc in snapshots.documents)
						doc.reference.delete()
				}
				else logDebug(TAG, "liked empty or deleted")
			}

		//base delete
		ref.delete()
			.addOnSuccessListener {
				//general delete
				matchedListener.remove()
				conversationsListener.remove()
				likedListener.remove()
				skippedListener.remove()
				emitter.onComplete()
			}.addOnFailureListener { emitter.onError(it) }

		emitter.setCancellable {
			matchedListener.remove()
			conversationsListener.remove()
			likedListener.remove()
			skippedListener.remove()
		}
	}.subscribeOn(MySchedulers.io())


	override fun getRequestedUserItem(baseUserInfo: BaseUserInfo): Single<UserItem> =
		fs.collection(USERS_COLLECTION)
			.document(baseUserInfo.userId)
			.get()
			.asSingle()
			.map {
				if (it.exists()) it.toObject(UserItem::class.java)
				else UserItem(BaseUserInfo("DELETED"))
			}

	
	override fun submitReport(type: ReportType, baseUserInfo: BaseUserInfo): Completable =
		fs.collection(REPORTS_COLLECTION)
			.document()
			.setAsCompletable(Report(reportType = type, reportedUser = baseUserInfo))


	override fun updateUserItem(userItem: UserItem): Completable =
		userDataSource.writeFirestoreUser(userItem)


	override fun uploadUserProfilePhoto(
        userItem: UserItem,
        photoUri: String
	): Observable<HashMap<Double, List<PhotoItem>>> =
		ObservableCreate<HashMap<Double, List<PhotoItem>>> { emitter ->
			val namePhoto = DateFormat.format(
				"yyyy-MM-dd_hhmmss", Date()
			).toString() + "_user_photo.jpg"
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
							fs.collection(USERS_COLLECTION)
								.document(userItem.baseUserInfo.userId)
								.update(USER_PHOTOS_LIST_FIELD, FieldValue.arrayUnion(uploadedPhotoItem))

							userItem.photoURLs.plus(uploadedPhotoItem)
							emitter.onNext(hashMapOf(100.00 to userItem.photoURLs))
							emitter.onComplete()
							
						}
						.addOnFailureListener { emitter.onError(it) }
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable { uploadTask.cancel() }
		}.subscribeOn(MySchedulers.io())
}