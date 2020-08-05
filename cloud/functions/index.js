const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
var db = admin.firestore();
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


exports.watchCreated = functions.firestore
    .document('users/{userId}/watchItems/{itemId}')
    .onCreate((snap, context) => {
        const value = snap.data();


        const userId = context.params.userId;
        const itemId = context.params.itemId;
        //write to watchLists
        return db.collection("watchLists").doc(itemId)
            .collection("watchers")
            .doc(userId)
            .set({
                user: userId
            })
            .then(function(){
                console.log("watch list add");
            })
            .catch(function(error){
                console.error("Error add watch list")
            })

    });