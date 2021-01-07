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
import com.mmdev.data.core.firebase.deleteAsCompletable
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.datasource.UserDataSource
import com.mmdev.domain.photo.PhotoItem
import com.mmdev.domain.user.ISettingsRepository
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate
import java.util.*
import javax.inject.Inject

/**
 * Repository to your info
 */

class SettingsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore,
	private val storage: StorageReference,
	private val userDataSource: UserDataSource
): BaseRepository(), ISettingsRepository {
	
	
	companion object {
		private const val USER_MAIN_PHOTO_FIELD = "baseUserInfo.mainPhotoUrl"
		private const val USER_PHOTOS_LIST_FIELD = "photoURLs"
		
		private const val PROFILE_FOLDER_STORAGE_IMG = "profilePhotos"
	}
	
	override fun deletePhoto(
		userItem: UserItem,
		photoItem: PhotoItem,
		isMainPhotoDeleting: Boolean
	): Completable = Completable.concatArray(
		//update list field
		userDataSource.updateFirestoreUserField(
			id = userItem.baseUserInfo.userId,
			field = USER_PHOTOS_LIST_FIELD,
			value = FieldValue.arrayRemove(photoItem)
		),
		//update mainPhoto url field
		if (isMainPhotoDeleting) {
			userDataSource.updateFirestoreUserField(
				id = userItem.baseUserInfo.userId,
				field = USER_MAIN_PHOTO_FIELD,
				value = userItem.photoURLs.minus(photoItem)[0].fileUrl
			)
		}
		else Completable.complete(),
		//delete photo from storage if it is not from facebook
		if (photoItem.fileName != PhotoItem.FACEBOOK_PHOTO_NAME)
			storage
				.child(GENERAL_FOLDER_STORAGE_IMG)
				.child(PROFILE_FOLDER_STORAGE_IMG)
				.child(userItem.baseUserInfo.userId)
				.child(photoItem.fileName)
				.deleteAsCompletable()
		else Completable.complete()
	)
	
	override fun deleteMyself(user: UserItem): Completable = CompletableCreate{ emitter ->
		
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
	
	override fun uploadUserProfilePhoto(
		userItem: UserItem,
		photoUri: String
	): Observable<Array<PhotoItem>> {
		val namePhoto = "${DateFormat.format("yyyy-MM-dd_hhmmss", Date())}_user_photo.jpg"
		
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(PROFILE_FOLDER_STORAGE_IMG)
			.child(userItem.baseUserInfo.userId)
			.child(namePhoto)
		
		return storageRef.putFile(Uri.parse(photoUri))
			.asSingle()
			.concatMap { storageRef.downloadUrl.asSingle() }
			.map { downloadUrl -> PhotoItem(fileName = namePhoto, fileUrl = downloadUrl.toString()) }
			.flatMap {
				userDataSource.updateFirestoreUserField(
					userItem.baseUserInfo.userId, USER_PHOTOS_LIST_FIELD, FieldValue.arrayUnion(it)
				).toSingle {
					arrayOf(it)
				}
			}
			.toObservable()
			.onErrorReturn { emptyArray<PhotoItem>() }
	}
	
	
}