package com.example.dao;


import com.example.HibernateFactory;

public class PlayerDao extends EntityDao {
    public PlayerDao(HibernateFactory hibernateFactory, Class clazz) {
        super(hibernateFactory, clazz);
    }
}