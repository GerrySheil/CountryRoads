const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotificationsNewUser = functions.firestore.document('users/{userId}')
	.onCreate(event => {
		const userRef2 = admin.firestore().collection('users');
		const ID = event.params.id;
		const eventToken = event.data.get('token');
		var fcmToken;
		var count = 0;
		var currentNode = event.data.get('cNlatitude');
		var nextNode = event.data.get('nNlatitude');
		const payload = {
			notification: {
				title: 'User approaching!',
				body: 'Another user is approaching, please take care'
			}
		};
		var usersToNotify = userRef2.where('cNlatitude', '==', nextNode).where('nNlatitude', '==', currentNode);
		return Promise.all([usersToNotify.get()]).then(res => {
			res.forEach(r => {
				r.forEach(d => {
					fcmToken = d.get('token');
					console.log('FCMToken = ', fcmToken);
					admin.messaging().sendToDevice(fcmToken, payload)
			
					count++;
					console.log("Get: ", d);
				});
				const payloadInvoker = {
					notification: {
						title: 'New Road Entered!',
						body: count + ' users are oncoming'
					}
				};
				admin.messaging().sendToDevice(eventToken, payloadInvoker);
				console.log('Get: ', r);
			});
			return usersToNotify;
		});	 
	});	
	
exports.sendNotificationsUpdatedUser = functions.firestore.document('/users/{userId}')
	.onUpdate(event => {
		const userRef = admin.firestore().collection('users');
		const ID = event.params.id;
		const eventToken = event.data.get('token');
		var currentNode = event.data.get('cNlatitude');
		var previousCurrentNode = event.data.previous.get('cNlatitude');
		var nextNode = event.data.get('nNlatitude');
		var distToNext = event.data.get('nNDist');
		var distFromLast = 0;
		var distBetween = 0;
		var count = 0;
		var fcmToken;
		const payload1 = {
			notification: {
				title: 'User less than 50m away',
				body: 'You are very close to an oncoming user'
			}
		};
		const payload2 = {
			notification: {
				title: 'User approaching!',
				body: 'Another user is approaching, please take care'
			}
		};
		
		//No Need for if. Just check it all in the one promise! 
			//Query Db for everyone whos moving in the opposite direction 
			//See if their distance is within 50m	 
			var usersToNotify = userRef.where('cNlatitude', '==', nextNode).where('nNlatitude', '==', currentNode);
		return Promise.all([usersToNotify.get()]).then(res => {
			res.forEach(r => {
				r.forEach(d => {
					distFromLast = d.get('cNDist');
					fcmToken = d.get('token');
					distBetween = distToNext - distFromLast;
					Math.abs(distBetween);
					fcmToken = d.get('token');
					console.log('Dist = ', distBetween);
					console.log('FCMToken = ', fcmToken);
					if(distBetween < 50){
						admin.messaging().sendToDevice(fcmToken, payload1)
			
						admin.messaging().sendToDevice(eventToken, payload1)
					}
					else if(currentNode !== previousCurrentNode){
					admin.messaging().sendToDevice(fcmToken, payload2)
					}
					count++;
					console.log("Get: ", d);
				});
				const payloadInvoker = {
					notification: {
						title: 'New Road Entered!',
						body: count + ' users are oncoming'
					}
				};
				if(currentNode !== previousCurrentNode){
				admin.messaging().sendToDevice(eventToken, payloadInvoker);
				}
				console.log('Get: ', r);
			});
			return usersToNotify;
		});	 
	});
		

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
