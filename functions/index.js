
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();
let allTokens = [];

async function getAllTokens() {
  // retrieves all the fcm tokens from the 'users' collection
  
  const users = db.collection('users');
  const snapshot = await users.get();
  snapshot.forEach(doc => {
    let fcmToken = doc.data().fcmToken;
    if (fcmToken && !allTokens.includes(fcmToken)) {
      allTokens.push(fcmToken);
    } 
  });
}


exports.sendNotificationToToken = functions.firestore.document('events/{eventId}')
  .onCreate(async (snap, context) => {
    const newDocument = snap.data();
    // Access event information needed for the notification payload
    let title = newDocument.name;
    let content = "On " + newDocument.date + " at " + newDocument.address;
    await getAllTokens();
    let message = {
      notification: {
        title: title,
        body: content
      },
      tokens: allTokens,
    };

    let response = await admin.messaging().sendMulticast(message);
    console.log(response);

  });


