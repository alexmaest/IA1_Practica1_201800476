/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ia.practica1.model;

/**
 *
 * @author alexm
 */
public class Sensitive {

    private String adult;
    private String spoof;
    private String medical;
    private String violence;
    private String racy;

    public Sensitive(String adult, String spoof, String medical, String violence, String racy) {
        this.adult = adult;
        this.spoof = spoof;
        this.medical = medical;
        this.violence = violence;
        this.racy = racy;
    }

    public String getAdult() {
        return adult;
    }

    public String getSpoof() {
        return spoof;
    }

    public String getMedical() {
        return medical;
    }

    public String getViolence() {
        return violence;
    }

    public String getRacy() {
        return racy;
    }
}
