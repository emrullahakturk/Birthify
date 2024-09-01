<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Birthday Reminder App</title>
    
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            max-width: 800px;
            margin: auto;
            padding: 20px;
        }
        h1, h2 {
            color: #333;
        }
        h1 {
            border-bottom: 2px solid #333;
            padding-bottom: 10px;
        }
        img {
            margin: 10px 0;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
        }
        p, li {
            color: #555;
        }
        ul {
            list-style-type: disc;
            margin-left: 20px;
        }
    </style>
</head>
<body>

<h1>Birthday Reminder App</h1>

<h2><strong>Overview</strong></h2>
<p>The <strong>Birthday Reminder App</strong> is a mobile application designed to help users remember and celebrate the birthdays of their friends and loved ones. The app sends reminders to the user before each birthday, ensuring that they never miss an important day. The app is built using Kotlin with MVVM architecture, ensuring a clean, maintainable, and scalable codebase.</p>

<h2><strong>Features</strong></h2>
<ul>
    <li><strong>User Registration & Authentication</strong>: Users can register and log in securely to the app.</li>
    <li><strong>Birthday Management</strong>: Add, edit, and delete birthdays of friends and family members.</li>
    <li><strong>Custom Reminders</strong>: Set custom reminders for each birthday (e.g., one week before, one day before, or one hour before the event).</li>
    <li><strong>Automated Notifications</strong>: Receive notifications 5 minutes before midnight on the day before the birthday.</li>
    <li><strong>Search Functionality</strong>: Quickly find a birthday entry using the search bar.</li>
    <li><strong>Data Persistence</strong>: All user data is securely stored in Firebase.</li>
</ul>

<h2><strong>Screenshots</strong></h2>
<img src="https://github.com/user-attachments/assets/4589eb1c-2026-46f0-a29e-75e208756681" alt="Home Screen" width="180" height="370"/>
<img src="https://github.com/user-attachments/assets/de378058-4cff-422b-bffb-b4f9f84d0af2" alt="Birthday List" width="180" height="370"/>
<img src="https://github.com/user-attachments/assets/a6d8b652-6627-4a2b-8c8f-c6f2489c15c6" alt="Add Birthday" width="180" height="370"/>
<img src="https://github.com/user-attachments/assets/f51459f3-ab43-42c5-b473-a1f5251318d7" alt="Set Reminder" width="180" height="370"/>

<h2><strong>Tech Stack</strong></h2>
<ul>
    <li><strong>Language</strong>: Kotlin</li>
    <li><strong>Architecture</strong>: MVVM (Model-View-ViewModel)</li>
    <li><strong>Components</strong>:</li>
    <ul>
        <li><strong>Navigation Component</strong>: Manages navigation between different screens within the app.</li>
        <li><strong>LiveData & StateFlow</strong>: Observables used for updating the UI in response to changes in the app's data.</li>
        <li><strong>AlarmManager</strong>: Schedules notifications to remind users of upcoming birthdays.</li>
        <li><strong>RecyclerView & Adapter</strong>: Efficiently displays a list of birthdays and allows for dynamic updates.</li>
    </ul>
</ul>

<h2><strong>Usage</strong></h2>
<ul>
    <li><strong>Register/Login</strong>: Start by registering an account or logging in with your existing credentials.</li>
    <li><strong>Add Birthdays</strong>: Navigate to the "Add Birthday" screen and input the details of your friend’s or family member's birthday.</li>
    <li><strong>Set Reminders</strong>: Choose when you'd like to receive a reminder for each birthday.</li>
    <li><strong>View and Edit Birthdays</strong>: The main screen lists all the birthdays you've added. You can click on any entry to edit or delete it.</li>
</ul>

<h2><strong>Contributing</strong></h2>
<p>Contributions are welcome! If you have suggestions for improvements or new features, please feel free to fork the repository and submit a pull request.</p>

<h2><strong>Contact</strong></h2>
<p>For any inquiries or issues, please contact <a href="mailto:akturk.emrullah@hotmail.com">akturk.emrullah@hotmail.com</a>.</p>

</body>
</html>
