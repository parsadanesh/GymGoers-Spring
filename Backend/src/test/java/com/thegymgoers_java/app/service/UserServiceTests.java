package com.thegymgoers_java.app.service;

import com.thegymgoers_java.app.model.Exercise;
import com.thegymgoers_java.app.model.User;
import com.thegymgoers_java.app.model.Workout;
import com.thegymgoers_java.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(passwordEncoder, userRepository);
    }

    @Nested
    class registerUser {

        @Test
        void testRegisterUser_Success() {
            // Arrange
            User user = new User("testname", "testemail@dom.com", "fakepass");
            // Mock repository responses
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(user);
            // Act
            User registeredUser = userService.register(user);
            // Assert
            assertNotNull(registeredUser);
            assertEquals(user.getUsername(), registeredUser.getUsername());
            assertEquals(user.getEmailAddress(), registeredUser.getEmailAddress());
            assertTrue(passwordEncoder.matches("fakepass", registeredUser.getPassword()));
        }

        @Test
        void testRegisterUser_EmptyUsername() {
            // Arrange
            User user = new User("", "testemail@dom.com", "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.register(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testRegisterUser_EmptyEmail() {
            // Arrange
            User user = new User("testname", " ", "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.register(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testRegisterUser_NullUsername() {
            // Arrange
            User user = new User(null, "testemail@dom.com", "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.register(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testRegisterUser_NullEmail() {
            // Arrange
            User user = new User("testname", null, "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.register(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testRegisterUser_UserAlreadyExists() {
            // Arrange
            User user = new User("testname", "testemail@dom.com", "fakepass");
            // Mock repository responses
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(Optional.empty());
            // Act
            User registeredUser = userService.register(user);
            // Assert
            assertNull(registeredUser);
        }

        @Test
        void testRegisterUser_EmailAlreadyExists() {
            // Arrange
            User user = new User("testname", "testemail@dom.com", "fakepass");
            // Mock repository responses
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(Optional.of(user));
            // Act
            User registeredUser = userService.register(user);
            // Assert
            assertNull(registeredUser);
        }
    }

    @Nested
    class loginUser {
        @Test
        void testLoginUser_Success() {
            // Arrange
            User user = new User("testname", "testemail@dom.com", passwordEncoder.encode("fakepass"));
            // Mock repository response
            when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
            // Act
            User loggedInUser = userService.login(new User("testname", "testemail@dom.com", "fakepass"));
            // Assert
            assertNotNull(loggedInUser);
            assertEquals(user.getUsername(), loggedInUser.getUsername());
            assertEquals(user.getEmailAddress(), loggedInUser.getEmailAddress());
        }

        @Test
        void testLoginUser_EmptyUsername() {
            // Arrange
            User user = new User("", "testemail@dom.com", "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.login(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testLoginUser_EmptyEmail() {
            // Arrange
            User user = new User("testname", "", "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.login(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testLoginUser_NullUsername() {
            // Arrange
            User user = new User(null, "testemail@dom.com", "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.login(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testLoginUser_NullEmail() {
            // Arrange
            User user = new User("testname", null, "fakepass");
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.login(user);
            });
            assertEquals("User details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testLoginUser_IncorrectPassword() {
            // Arrange
            User user = new User("testname", "testemail@dom.com", passwordEncoder.encode("fakepass"));
            // Mock repository response
            when(userRepository.findByUsername("testname")).thenReturn(Optional.of(user));
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.login(new User("testname", "testemail@dom.com", "wrongpass"));
            });
            assertEquals("Incorrect password: wrongpass", exception.getMessage());
        }

        @Test
        void testLoginUser_IncorrectUsername() {
            // Mock repository response
            when(userRepository.findByUsername("wrongname")).thenReturn(Optional.empty());
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.login(new User("wrongname", "testemail@dom.com", "fakepass"));
            });
            assertEquals("Incorrect Username: wrongname", exception.getMessage());
        }


    }

    @Nested
    class getWorkout {
        @Test
        void testGetWorkouts_Success() throws Exception {
            // Arrange
            String username = "testuser";
            User user = new User(username, "testemail@dom.com", "fakepass");
            List<Workout> workoutList = new ArrayList<>();
            workoutList.add(new Workout());
            workoutList.add(new Workout());
            user.setWorkoutsList(workoutList);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            // Act
            List<Workout> result = userService.getWorkouts(username);

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void testGetWorkouts_UserNotFound() {
            // Arrange
            String username = "nonexistentuser";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // Act & Assert
            NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
                userService.getWorkouts(username);
            });
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        void testGetWorkouts_EmptyWorkoutList() throws Exception {
            // Arrange
            String username = "testuser";
            User user = new User(username, "testemail@dom.com", "fakepass");
            user.setWorkoutsList(new ArrayList<>());

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            // Act
            List<Workout> result = userService.getWorkouts(username);

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void testGetWorkouts_NullUsername() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.getWorkouts(null);
            });
            assertEquals("Details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testGetWorkouts_EmptyUsername() {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.getWorkouts("");
            });
            assertEquals("Details cannot be empty or null", exception.getMessage());
        }
    }

    @Nested
    class addWorkout {
        @Test
        void testAddWorkout_Success() {
            // Arrange
            String username = "testuser";
            User user = new User(username, "testemail@dom.com", "fakepass");
            Workout workout = new Workout();
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Act
            User result = userService.addWorkout(username, workout);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getWorkoutsList().size());
            assertEquals(workout, result.getWorkoutsList().get(0));
            assertNotNull(workout.getDateCreated());
        }

        @Test
        void testAddWorkout_NullUsername() {
            // Arrange
            Workout workout = new Workout();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.addWorkout(null, workout);
            });
            assertEquals("Details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testAddWorkout_EmptyUsername() {
            // Arrange
            Workout workout = new Workout();

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.addWorkout("", workout);
            });
            assertEquals("Details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testAddWorkout_UserNotFound() {
            // Arrange
            String username = "nonexistentuser";
            Workout workout = new Workout();
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.addWorkout(username, workout);
            });
            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    class deleteWorkout {
        @Test
        void testDeleteWorkout_Success() {
            // Arrange
            String username = "testuser";
            String workoutId = "workout1";
            User user = new User(username, "testemail@dom.com", "fakepass");
            Workout workout = new Workout();
            workout.set_id(workoutId);
            user.addWorkout(workout);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Act
            User result = userService.deleteWorkout(username, workoutId);

            // Assert
            assertNotNull(result);
            assertTrue(result.getWorkoutsList().isEmpty());
        }

        @Test
        void testDeleteWorkout_UserNotFound() {
            // Arrange
            String username = "nonexistentuser";
            String workoutId = "workout1";

            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // Act
            User result = userService.deleteWorkout(username, workoutId);

            // Assert
            assertNull(result);
        }

        @Test
        void testDeleteWorkout_WorkoutNotFound() {
            // Arrange
            String username = "testuser";
            String workoutId = "nonexistentworkout";
            User user = new User(username, "testemail@dom.com", "fakepass");
            Workout workout = new Workout();
            workout.set_id("workout1");
            user.addWorkout(workout);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteWorkout(username, workoutId);
            });
            assertEquals("Workout not found", exception.getMessage());
        }

        @Test
        void testDeleteWorkout_NullUsername() {
            // Arrange
            String workoutId = "workout1";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteWorkout(null, workoutId);
            });
            assertEquals("Details cannot be empty or null", exception.getMessage());
        }

        @Test
        void testDeleteWorkout_EmptyUsername() {
            // Arrange
            String workoutId = "workout1";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteWorkout("", workoutId);
            });
            assertEquals("Details cannot be empty or null", exception.getMessage());
        }
    }

    @Nested
    class GetWorkoutsFromLast7Days {

        @Test
        void testGetWorkoutsFromLast7Days_Success() {
            // Arrange
            String username = "testuser";
            User user = new User(username, "testemail@dom.com", "fakepass");
            Workout workout1 = new Workout();
            workout1.setDateCreated(LocalDateTime.now().minusDays(3).toString());
            Workout workout2 = new Workout();
            workout2.setDateCreated(LocalDateTime.now().minusDays(5).toString());


            Exercise exercise = new Exercise();
            exercise.setExerciseName("exericse");
            exercise.setReps(2);
            exercise.setSets(2);
            exercise.setWeight(20);
            exercise.setTime(0);
            workout2.setExercises(List.of(exercise));
            user.setWorkoutsList(List.of(workout1, workout2));


            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            System.out.println(user.getWorkoutsList().get(1));

            // Act
            int result = userService.getWorkoutsFromLast7Days(username);

            // Assert
            assertEquals(80, result); // Result from the 2 * 2 * 20 from the exercise
        }

        @Test
        void testGetWorkoutsFromLast7Days_UserNotFound() {
            // Arrange
            String username = "nonexistentuser";

            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                userService.getWorkoutsFromLast7Days(username);
            });
            assertEquals("User not found", exception.getMessage());
        }

        @Test
        void testGetWorkoutsFromLast7Days_EmptyWorkoutList() {
            // Arrange
            String username = "testuser";
            User user = new User(username, "testemail@dom.com", "fakepass");
            user.setWorkoutsList(new ArrayList<>());

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            // Act
            int result = userService.getWorkoutsFromLast7Days(username);

            // Assert
            assertEquals(0, result);
        }
    }

    @Nested
    class CalculateTotalWeight {

        @Test
        void testCalculateTotalWeight_Success() {
            // Arrange
            Exercise exercise1 = new Exercise();
            exercise1.setExerciseName("exercise1");
            exercise1.setSets(3);
            exercise1.setReps(10);
            exercise1.setWeight(50);
            exercise1.setTime(0);

            Exercise exercise2 = new Exercise();
            exercise2.setExerciseName("exercise2");
            exercise2.setSets(2);
            exercise2.setReps(15);
            exercise2.setWeight(30);
            exercise2.setTime(0);

            int expected = (3*10*50) + (2*15*30);

            Workout workout = new Workout();
            workout.setExercises(List.of(exercise1, exercise2));

            List<Workout> workoutList = List.of(workout);

            // Act
            int result = userService.calculateTotalWeight(workoutList);


            // Assert
            assertEquals(expected, result);
        }

        @Test
        void testCalculateTotalWeight_EmptyWorkoutList() {
            // Arrange
            List<Workout> workoutList = new ArrayList<>();

            // Act
            int result = userService.calculateTotalWeight(workoutList);

            // Assert
            assertEquals(0, result);
        }

        @Test
        void testCalculateTotalWeight_EmptyExercises() {
            // Arrange
            Workout workout = new Workout();
            workout.setExercises(new ArrayList<>());

            List<Workout> workoutList = List.of(workout);

            // Act
            int result = userService.calculateTotalWeight(workoutList);

            // Assert
            assertEquals(0, result);
        }
    }

}
