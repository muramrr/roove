const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

exports.notifyNewMessage = functions.firestore
    .document('conversations/{conversationId}/messages/{message}')
    .onCreate((docSnapshot, context) => {
        const message = docSnapshot.data();
        const recipientId = message['recipientId'];
        const sender = message['sender'];

        return admin.firestore().doc('usersBase/' + recipientId).get().then(userDoc => {
            const registrationTokens = userDoc.get('registrationTokens')

            const notificationBody = (message['photoAttachmentItem'] === null) ? message['text'] : "You received a new image message."
            const payload = {
                notification: {
                    title: sender.name + " sent you a message.",
                    body: notificationBody,
                    clickAction: "ChatFragment"
                },
                data: {
                    USER_NAME: sender.name,
                    USER_ID: sender.userId
                }
            }

            return admin.messaging().sendToDevice(registrationTokens, payload).then( response => {
                const stillRegisteredTokens = registrationTokens

                response.results.forEach((result, index) => {
                    const error = result.error
                    if (error) {
                        const failedRegistrationToken = registrationTokens[index]
                        console.error('mylogs_fcm', failedRegistrationToken, error)
                        if (error.code === 'messaging/invalid-registration-token'
                            || error.code === 'messaging/registration-token-not-registered') {
                                const failedIndex = stillRegisteredTokens.indexOf(failedRegistrationToken)
                                if (failedIndex > -1) {
                                    stillRegisteredTokens.splice(failedIndex, 1)
                                }
                            }
                    }
                })

                return admin.firestore().doc("usersBase/" + recipientId).update({
                    registrationTokens: stillRegisteredTokens
                })
            })
        })
    })
