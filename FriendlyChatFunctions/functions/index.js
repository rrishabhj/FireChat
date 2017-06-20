const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// replaces keywords with emoji in the "text" key of messages
// pushed to /messages
exports.emojify =
    functions.database.ref('/messages/{pushId}/text')
    .onWrite(event => {
        // Database write events include new, modified, or deleted
        // database nodes. All three types of events at the specific
        // database path trigger this cloud function.
        // For this function we only want to emojify new database nodes,
        // so we'll first check to exit out of the function early if
        // this isn't a new message.

        // !event.data.val() is a deleted event
        // event.data.previous.val() is a modified event
        if (!event.data.val() || event.data.previous.val()) {
            console.log("not a new write event");
            return;
        }

        // Now we begin the emoji transformation
        console.log("emojifying!");

        // Get the value from the 'text' key of the message
        const originalText = event.data.val();
        const emojifiedText = emojifyText(originalText);

        // Return a JavaScript Promise to update the database node
        return event.data.ref.set(emojifiedText);
    });

// Returns text with keywords replaced by emoji
// Replacing with the regular expression /.../ig does a case-insensitive
// search (i flag) for all occurrences (g flag) in the string
function emojifyText(text) {
    var emojifiedText = text;
    emojifiedText = emojifiedText.replace(/\blol\b/ig, "😂");
    emojifiedText = emojifiedText.replace(/\bcat\b/ig, "😸");
    return emojifiedText;
}
