<h1>Expense Management App</h1>
<p>This app is designed to help groups of friends or users track shared expenses, similar to how Splitwise works. It simplifies the process of splitting bills and ensures transparency in tracking who owes what.</p>

<h2>Features</h2>
<ul>
    <li><strong>Add Friends/Groups:</strong> Easily create groups and add friends to keep track of shared expenses.</li>
    <li><strong>Track Expenses:</strong> Record expenses by selecting the payer and splitting costs among group members.</li>
    <li><strong>Automated Calculations:</strong> Automatically calculate how much each member owes, reducing manual work.</li>
    <li><strong>Expense History:</strong> View a history of all transactions and settled amounts.</li>
    <li><strong>Settle Debts:</strong> Easily keep track of settled amounts between users.</li>
    <li><strong>User Authentication:</strong> Secure login via Firebase.</li>
    <li><strong>Real-time Sync:</strong> Data is stored and synced across all devices using Firebase.</li>
</ul>

<h2>Technology Stack</h2>
<ul>
    <li><strong>Frontend:</strong> Android (Java/Kotlin)</li>
    <li><strong>Backend:</strong> Firebase (Firestore for real-time database, Firebase Authentication)</li>
    <li><strong>UI Components:</strong> RecyclerView, Spinners, Material Design Components</li>
</ul>

<h3>Libraries Used:</h3>
<ul>
    <li>Firebase SDK</li>
    <li>RecyclerView Adapter</li>
    <li>Glide for image loading (if applicable)</li>
    <li>Retrofit for API calls (if applicable)</li>
</ul>

<h2>Setup and Installation</h2>
<ol>
    <li>Clone the repository:</li>
</ol>

<pre><code>git clone [https://github.com/Harshgupta1064/Settle.it]</code></pre>

<ol start="2">
    <li>Open the project in Android Studio.</li>
    <li>Configure Firebase:</li>
</ol>
<ul>
    <li>Create a Firebase project.</li>
    <li>Add your Android app to Firebase.</li>
    <li>Download the <code>google-services.json</code> file and place it in the <code>app/</code> directory.</li>
</ul>

<ol start="4">
    <li>Build and run the project on an emulator or physical device.</li>
</ol>


Usage
Sign Up/Log In: New users need to sign up with an email or Google account.
Create Groups: Add friends by selecting from contacts or entering details manually.
Add Expenses: Record expenses and select the payer. Split the cost equally or manually adjust shares.
View Summary: Check how much each member owes or is owed in real-time.
Settle Up: Once debts are paid, mark them as settled to update the balance.
