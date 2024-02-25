/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ia.practica1.model;

import java.util.List;

/**
 *
 * @author alexm
 */
public class DataList {

    private int faces = 0;
    private String imageDetectedFaces;
    private List<LabelData> labels;
    private Sensitive sensitive;

    public DataList(int faces, String imageDetectedFaces, List<LabelData> labels, Sensitive sensitive) {
        this.faces = faces;
        this.imageDetectedFaces = imageDetectedFaces;
        this.labels = labels;
        this.sensitive = sensitive;
    }

    public List<LabelData> getLabels() {
        return labels;
    }

    public String getImageDetectedFaces() {
        return imageDetectedFaces;
    }

    public int getFacesNumber() {
        return faces;
    }

    public Sensitive getSensitive() {
        return sensitive;
    }
}
