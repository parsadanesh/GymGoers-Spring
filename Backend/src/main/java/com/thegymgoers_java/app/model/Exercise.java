package com.thegymgoers_java.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import org.springframework.data.mongodb.core.mapping.Field;

public class Exercise {


    @JsonProperty("exerciseName")
    @NotEmpty(message = "Exercise needs a name")
    private String exerciseName;

    @JsonProperty("sets")
//    @Positive
    private int sets;

    @JsonProperty("reps")
    private int reps;

    @JsonProperty("weight")
    private int weight;

    @JsonProperty("time")
    private int time;

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getWeight() {
        return weight;
    }

    public int getTime() {
        return time;
    }

    public void setExerciseName(@NotEmpty(message = "Exercise needs a name") String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
