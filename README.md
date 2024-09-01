Birthday Reminder App

Overview
The Birthday Reminder App is a mobile application designed to help users remember and celebrate the birthdays of their friends and loved ones. The app sends reminders to the user before each birthday, ensuring that they never miss an important day. The app is built using Kotlin with MVVM architecture, ensuring a clean, maintainable, and scalable codebase.

Features
User Registration & Authentication: Users can register and log in securely to the app.
Birthday Management: Add, edit, and delete birthdays of friends and family members.
Custom Reminders: Set custom reminders for each birthday (e.g., one week before, one day before, or one hour before the event).
Automated Notifications: Receive notifications 5 minutes before midnight on the day before the birthday.
Search Functionality: Quickly find a birthday entry using the search bar.
Data Persistence: All user data is securely stored in Firebase.

Screenshots
![unnamed](https://github.com/user-attachments/assets/4589eb1c-2026-46f0-a29e-75e208756681)


Tech Stack
Language: Kotlin
Architecture: MVVM (Model-View-ViewModel)
Components:
Navigation Component: Manages navigation between different screens within the app.
LiveData & StateFlow: Observables used for updating the UI in response to changes in the app's data.
AlarmManager: Schedules notifications to remind users of upcoming birthdays.
RecyclerView & Adapter: Efficiently displays a list of birthdays and allows for dynamic updates.

Usage
Register/Login: Start by registering an account or logging in with your existing credentials.
Add Birthdays: Navigate to the "Add Birthday" screen and input the details of your friend’s or family member's birthday.
Set Reminders: Choose when you'd like to receive a reminder for each birthday.
View and Edit Birthdays: The main screen lists all the birthdays you've added. You can click on any entry to edit or delete it.

Contributing
Contributions are welcome! If you have suggestions for improvements or new features, please feel free to fork the repository and submit a pull request.

Contact
For any inquiries or issues, please contact akturk.emrullah@hotmail.com