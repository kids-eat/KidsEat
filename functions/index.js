
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();
let db = admin.firestore();
let allTokens = [];

async function getAll() {

  const users = db.collection('users');
  const snapshot = await users.get();
  snapshot.forEach(doc => {
    if (doc.data().fcmToken) {
      console.log("Token:", doc.data().fcmToken);
      allTokens.push(doc.data().fcmToken);
      console.log("tokens in getAll", allTokens);
    } else {
      console.log("No Token!");
    }
  });

}


exports.sendNotificationToToken = functions.firestore
  .document('events/{eventId}')
  .onCreate(async (snap, context) => {
    const newDocument = snap.data();

    // access a particular field as you would any JS property
    let name = newDocument.name;
    let content = "Notification content";
    await getAll();
    console.log("tokens in sendNotificationToken", allTokens);

    let message = {
      notification: {
        title: name,
        body: content
      },
      tokens: allTokens,
    };

    let response = await admin.messaging().sendMulticast(message);
    console.log(response);

  });


// exports.sendNotification = functions.firestore
//   .document('events/{eventId}')
//   .onCreate(async (snap, context) => {
//     // Get an object representing the document
//     // e.g. {'name': 'Marie', 'age': 66}
//     const newDocument = snap.data();

//     // access a particular field as you would any JS property
//     let name = newDocument.name;
//     let content = "Notification content";

//     let message = {
//       notification: {
//         title: name,
//         body: content
//       },
//       topic: "New Event",
//     };

//     let response = await admin.messaging().send(message);
//     console.log(response);

//   });