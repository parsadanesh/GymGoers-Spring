# Java Backend Implementation For The GymGoers
This is a Java and Spring boot implementation of The Gymgoers backend.

<details>
    <summary>Table of Content</summary>
    <ol>
        <li><a href="#about-the-project">About The Project</a></li>
        <li><a href="#technologies-used">Technologies Used</a></li>
        <li><a href="#architecture">Architecture</a></li>
        <li><a href="#Running-the-app">Running The App</li>
    </ol>
</details>

---

## About The Project

In July I created my first full-stack project, I made an application called [The GymGoers](https://github.com/parsadanesh/TheGymGoers/blob/main/ProjectREADME.md) and I did this using the `MERN` stack (`MongoDB`, `ExpressJS`, `ReactJS`, `NodeJS`). This project is a `Java` and `Spring` implementation of the backend for the 'The GymGoers` app.

Recently, I was given the opportunity to create and showcase a project using Java and Spring. With this project I aim to use `Spring` and `Java` to develop a new backend system for my GymGoers app, my goal is to use the same frontend and database, swapping the old backend created using the MERN stack with a new backend that uses Spring. In the end, since both systems use a REST API and the same routes, the new backend, once developed, should connect seamlessly with the existing frontend and database and the transition should be smooth for those using the app (They should not notice the difference when using the app).

As this was my first time using Spring, I wanted to focus on using this project as an opportunity to help solidify my knowledge and skills with Spring and Spring Boot. My thought is that performing a migration of the backend from the MERN stack to Spring and Spring Boot would provide me with the opportunity to understand the similarities and differences between the two technologies. I think this process will help me better understand how, and why I am using Spring, but also I want to use this as a chance to improve on the technologies I have already used like REST APIs. I am also using this opportunity to improve on my previous implementation and reevaluate decisions I made previously due to inexperience.

## Technologies Used

Built using `Java 17` using `Spring` and `Maven` with `MongoDB` for the data persistence layer.

Built with the following dependencies: Spring Web, Rest Repositories, Spring Security, JWT, JUnit, Mockito, MongoDB

## Architecture

### Project Architecture

![Architecture Diagram](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/BackendArchitecureDiagram.JPG)

### Routing 

**Auth**
- POST ("/api/auth/signup") - Creates a new user
- POST ("/api/auth/signin") - Attempts to login a user 

**User**
- GET ("/users/{username}/workouts") - Get a users list of workouts
- POST ("/users/{username}/workouts") - Add a new workout to users list
- DELETE ("/users/{username}/workouts/{_id}") - Delete a workout from the users list based on the workout ID

**Gymgroups**
- POST ("/gymgroups/{username}") - Creates a new GymGroup
- POST ("/gymgroups/{username}/{groupName}") - Adds a user trying to join the GymGroup
- GET ("/gymgroups/{username}") - Retrieves all the GymGroups a user in a part of
- GET ("/gymgroups/group/{groupName}") - Goes through the GymGroup, find all users and gets their list of workouts in order to calculate their number of consecutive days for the leaderboard

One benefit I found of doing a remigration is that I was able to use the routes from my previous backend, I was able to use these previous routes to guide me when developing the system using Spring and helped me when creating my controllers, services and the methods in them. Some changes between the two implementations have been made and these have been due to improving aspects of the previous implementation, such as the way I send the data across with the requests through payloads.

**User Route Diagrams**
### Add Workout
![Add Workout Diagram](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/AddWorkoutRoute.JPG)
### Get workouts
![GetWorkoutRoute](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/GetWorkoutsRoute.JPG)
### Delete Workout
![DeleteWorkoutRoute](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/DeleteWorkoutRoute.JPG)

**Auth Diagrams**
### Sign Up New User
![SignUpUser](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/AddNewUserRoute.JPG)
### Sign In User
![SignInUser](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/SignInUserRoute.JPG)

### User Stories

![image](https://github.com/user-attachments/assets/0515554d-a727-48fb-b699-5dd67a203260)


### Testing

A massive part of this project has been testing. Since the majority of the workload came from the backend I focused on using test-driven development to develop the user stories and the application's features. For each user story, I created a set of tests that I could implement, I started with the controllers and used Mockito is mock the responses from the services classes. After that, I worked on testing the services and I used Mockito to mock the interactions with the repositories. 

One area I did not use TDD for development was the authentication for the sign-in and sign-up up and it is because I followed a JWT authentication guide recommended by another software engineer, it was my first time implementing JWT auth which is why I followed the guide. I created tests for the method once I finished developing them so it did not follow the TDD method.


## Running The App

**You can use my application in a couple of ways**

## Using the website
To begin with, I have deployed my application to the cloud, I used Netlify to deploy the frontend and, I used Docker to containerise my backend to deploy on Render. 

This is the link: https://thegymgoerapp.netlify.app 

I am using a free tier for the cloud services so the backend server spins down after a period of no use, so when accessing the website you will potentially have to wait a couple of minutes for the services to start functioning.

Another option is cloning this Git repo and running the projects manually. I used VSCode to develop the frontend and IntelliJ to develop the backend.
## To run the frontend:
### Clone the Repository
- `git clone <repository-url>`
- `cd <repository-directory>`

### Install Dependencies
- Once in the correct directory type, `npm install`
### Run The Development Server
- Type: `npm run dev`
### Run Tests
- Type: `npm run coverage`
  
## To run the backend
To run the backend you can either use an IDE and run the main method or use docker:
### Clone the Repository
- `git clone <repository-url>`
- `cd <repository-directory>`

## Using Docker 
### Build the Docker image
- Type: `docker build -t parsadanesh20/app:latest .`
### Run the Docker Container
- Type: `docker run -p 8080:8080 parsadanesh20/app:latest`

## Using Maven
### Build Project
- `mvn clean install`
### Run the Application
- `mvn spring-boot:run`

## Project Review and Future 

Moving forward with the project, I have deployed the application to the cloud and sent the link to friends and colleagues I know will benefit from using the app. I have asked those who are using my app to think about the app while using it to provide me with features they would like to see added, and any improvements they might have noticed could be implemented, but also to get feedback on the parts and features of the app that they liked and thought were nice features of the app. 

I see real potential with this app and the concept, my goal, through the feedback I get from my friends who use the app, is to make it into something that can be used by all with no issues. I want to make it as easy to navigate and as simple as possible while still providing all existing and any additional features/functionalities. 

In the meantime, there are improvements that I have planned, mostly to the UI, to improve further the experience of using the frontend. For example, since I am now using the cloud, there could be a delay with requests until the backend starts up again. I want to display a loading icon for moments like signing in where a user is still waiting for their requests to go through and receive a response. 

Another small improvement I believe will significantly improve UX is to improve the way the list of exercises is displayed, my idea is to categorise the exercises and display them as so, this will make it much easier to navigate the exercises when logging a workout and will make it easier to store a lot more exercises in the list without making more difficult to navigate.
