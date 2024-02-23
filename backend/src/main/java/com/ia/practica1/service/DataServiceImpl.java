/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ia.practica1.service;

/**
 *
 * @author alexm
 */
import com.ia.practica1.model.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.ia.practica1.repository.DataRepository;

@Service
public class DataServiceImpl implements DataService{

    @Autowired(required = false)
    private DataRepository dataRepository;

    @Override
    public Data saveData(Data student) {
        return dataRepository.save(student);
    }

    @Override
    public List<Data> getAllData() {
        return dataRepository.findAll();
    }

}