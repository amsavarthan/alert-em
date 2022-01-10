# Alert'em

Emergency Alert Sending App (Alert them)

<b> Problem Statement: </b>

Over a long period the main issue we faced is one click alerting to our loved ones when we are in an emergency.

<b> Proposed Solution : </b>

This project proposes a “Emergency Alerting System” to send an SMS containing the recent call log of the user to their emergency contacts.

<img height="512" alt="screenshot" src="https://github.com/amsavarthan/alert-em/blob/main/screenshots/1.MainScreen.png">

<b> Functionality & Concepts used : </b>

- The App has a very simple and interactive interface which helps the victim to send an alert to their emergency contacts. Following are few android concepts used to achieve the functionalities in app :
- Constraint Layout : Most of the activities in the app uses a flexible constraint layout, which is easy to handle for different screen sizes.
- Simple & Easy Views Design : Use of familiar audience EditText with hints and interactive buttons made it easier for users. App also uses Jetpack Navigation to switch between different screens.
- RecyclerView : To present the list of emergency contacts and helpline numbers we used the efficient recyclerview.
- LiveData & Room Database : We are also using LiveData to update & observe any changes from the local databases using Room. Room is used to store the user details and their emergency contacts.

<b> Application Link & Future Scope : </b>

The app is currently in the Alpha testing phase with a limited no. of users, You can access the app [here](https://github.com/amsavarthan/alert-em/releases/tag/v1.0.0).

Once the app is fully tested and functional to a smaller user base, we plan to add more features to include live location sharing during emergency and favorite contacts realtime location tracking by integrating Firebase Realtime Database.
