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

In July I created my first full-stack project, I made an application called [The GymGoers](https://github.com/parsadanesh/TheGymGoers/blob/main/ProjectREADME.md) and I did this using the `MERN` stack (`MongoDB`, `ExpressJS`, `ReactJS`, `NodeJS`).

Recently, I was given the opportunity to create and showcase a project using Java and Spring. With this project I aim to use `Spring` and `Java` to develop a new backend system for my GymGoers app, my goal is to use the same frontend and database, swapping the old backend created using the MERN stack with a new backend that uses Spring. In the end, since both systems use a REST API and the same routes, the new backend, once developed, should connect seamlessly with the existing frontend and database and the transition should be smooth for those using the app (They should not notice the difference when using the app).

This project is a `Java` and `Spring` implementation of the backend for my app, 'The GymGoers`.

As this was my first time using Spring, I wanted to focus on using this project as an opportunity to help solidify my knowledge and skills with Spring and Spring Boot. My thought is that performing a migration of the backend from the MERN stack to Spring and Spring Boot would provide me with the opportunity to understand the similarities and differences between the two technologies. I think this process will help me better understand how, and why I am using Spring, but also I want to use this as a chance to improve on the technologies I have already used like REST APIs. I am also using this opportunity to improve on my previous implementation and reevaluate decisions I made previously due to inexperience.

## Technologies Used

Built using `Java 17` using `Spring` and `Maven` with `MongoDB` for the data persistence layer.

Built with the following dependencies: Spring Web, Rest Repositories, Spring Security, JWT, JUnit, Mockito, MongoDB

## Architecture

### Project Architecture

![Architecture Diagram](https://github.com/parsadanesh/GymGoers-Spring/blob/main/Docs/BackendArchitecureDiagram.JPG)

### Server Architecture

Draft - React Frontend, Spring Boot backend, MongoDB Database, using rest controllers HTTP request my from the frontend are handled. The controllers are different so depending on the type of request and the payload the request is routed to a specific controller method. That method talks to the service class which communicates with the repository interface which implements the mongoDB repository using spring boot dependence.

### Routing 
One benefit I found of doing a remigration is that I was able to use the routes from my previous backend, I was able to use these previous routes to guide me when developing the system using Spring and helped me when creating my controllers, services and the methods in them. Some changes between the two implementations have been made and these have been due to improving aspects of the previous implementation, such as the way I send the data across with the requests.

### User Stories

### Testing

A massive part of this project has been testing. Since the majority of the workload came from the backend I focused on using test-driven development to develop the user stories and the features of the application. For each user story, I created a set of tests that I could implement, I started with the controllers and used Mockito is mock the responses from the services classes. After that, I worked on testing the services and I used Mockito to mock the interactions with the repositories.



## Running The App

You can use my application in a couple of ways.

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

## Project Review and Future Fetures
