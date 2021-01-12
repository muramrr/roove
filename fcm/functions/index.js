const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.notifyNewMatch = functions.region('europe-west1')
    .firestore
    .document('users/{userId}/matched/{cardItem}')
    .onCreate((docSnapshot, context) => {

        const cardUserToNotifyItem = docSnapshot.data();
        const cardUserToNotifyId = cardUserToNotifyItem['baseUserInfo'].userId;

        return admin.firestore().doc('users/' + cardUserToNotifyId).get().then(userDoc => {
            const registrationTokens = userDoc.get('installations');

            const notificationBody = "You've got a new match!";
            const payload = {
                data: {
                    CONTENT: notificationBody,
                  	TYPE: "NEW_MATCH"
                }
            }
			//send notification to device and check which notifications delivered with failure
            return admin.messaging().sendToDevice(registrationTokens, payload).then( response => {
                const stillRegisteredTokens = registrationTokens;

                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        const failedRegistrationToken = registrationTokens[index];
                        console.error('mylogs_fcm', failedRegistrationToken, error);
                        if (error.code === 'messaging/invalid-registration-token' || error.code === 'messaging/registration-token-not-registered') {
                            const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken);
                            if (failedIndex > -1) {
                                stillRegisteredTokens.splice(failedIndex, 1);
                            }
                        }
                    }
                })
				//delete obsolete installations to prevent sending notification again to this token
                return admin.firestore().doc("users/" + cardUserToNotifyId).update({
                    registrationTokens: stillRegisteredTokens
                })
            })
        })
    });

exports.notifyNewMessage = functions.region('europe-west1')
    .firestore
    .document('conversations/{conversationId}/messages/{message}')
    .onCreate((docSnapshot, context) => {
        const message = docSnapshot.data();
        const recipientId = message['recipientId'];
        const sender = message['sender'];
        const conversationId = message['conversationId'];
        const photoItem = message['photoItem']

        return admin.firestore().doc('users/' + recipientId).get().then(userDoc => {
            const registrationTokens = userDoc.get('installations');
            var payload;
            if (photoItem === null) {
				const notificationBody = message['text'];
				payload = {
					data: {
					    SENDER_NAME: sender.name,
					    SENDER_CITY: sender.city,
					    SENDER_GENDER: sender.gender,
					    SENDER_PHOTO: sender.mainPhotoUrl,
					    SENDER_ID: sender.userId,
					    CONVERSATION_ID: conversationId,
					    CONTENT: notificationBody,
					    TYPE: "NEW_MESSAGE"
					}
				}
            }
            else {
				const notificationBody = "Photo";
				payload = {
					data: {
					    SENDER_NAME: sender.name,
					    SENDER_CITY: sender.city,
					    SENDER_GENDER: sender.gender,
					    SENDER_PHOTO: sender.mainPhotoUrl,
					    SENDER_ID: sender.userId,
					    CONVERSATION_ID: conversationId,
					    CONTENT: notificationBody,
					    CONTENT_PHOTO: photoItem['fileUrl'],
					    TYPE: "NEW_MESSAGE"
					}
				}
            }


			//send notification to device and check which notifications delivered with failure
            return admin.messaging().sendToDevice(registrationTokens, payload).then( response => {
                const stillRegisteredTokens = registrationTokens;

                response.results.forEach((result, index) => {
                    const error = result.error;
                    if (error) {
                        const failedRegistrationToken = registrationTokens[index];
                        console.error('mylogs_fcm', failedRegistrationToken, error);
                        if (error.code === 'messaging/invalid-registration-token'
                            || error.code === 'messaging/registration-token-not-registered') {
                                const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken);
                                if (failedIndex > -1) {
                                    stillRegisteredTokens.splice(failedIndex, 1)
                                }
                            }
                    }
                })
				//delete obsolete installations to prevent sending notification again to this token
                return admin.firestore().doc("users/" + recipientId).update({
                    registrationTokens: stillRegisteredTokens
                })
            })
        })
    });
