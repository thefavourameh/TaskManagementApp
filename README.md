Task Management Web Application

### OVERVIEW
This is a web-based task management application designed to simplify personal to-do's and tracking for individuals. 
From organizing tasks, setting deadlines, tracking progress, and prioritizing activities. It typically offers features 
such as task creation, assignment, scheduling, collaboration, visualization, and integration with other productivity tools.

### FEATURES

**User Authentication and Security**

- User Registration: Allow users to create accounts by providing their names, email, phone number and password.
- User Login: Authenticate users with their credentials (username/email and password) to access the application.
- Password Encryption: Encrypt user passwords to ensure security and prevent unauthorized access to user accounts.
- Session Management: Maintain user sessions and implement session timeout to enhance security.


**Task Management**

- Category Creation: Ability to create Categories (sections) to contain related tasks.
- Quick Task Creation: Ability to create tasks simply by inputting task titles.
- Update Task: Ability to update task titles
- Detailed Task Creation: Ability to create tasks with titles, descriptions, deadlines, and priority levels.
- Task Tracking: Update task statuses (e.g., in progress, completed).
- Delete Task: User can delete task(s).


### INSTALLATION AND SETUP INSTRUCTIONS
- Clone the repository: git clone [https://github.com/thefavourameh/TaskManagementApp.git]
- Using MySQL RDB, create a schema titled - TaskMgtDB
- Edit Configurations in the backend, username is root and password is your unique mySQL password
- Generate a secret JSON web token key and include in the configuration (check application.properties file)
- Build and run the backend server.
- Test with Postman or Swagger.


### TESTING PROCEDURE
Backend Testing: Unit testing was done using frameworks like JUnit and Mockito for testing service methods, 
to ensure proper functionality and error handling.

### TECHNOLOGIES USED
Backend: Java, Spring Boot
Database: MySQL
Authentication: JSON Web Tokens (JWT).

