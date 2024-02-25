/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ia.practica1.model;

/**
 *
 * @author alexm
 */
public class LabelData {

    private String description;
    private float score;

    public LabelData(String description, float score) {
        this.description = description;
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public float getScore() {
        return score;
    }
}
