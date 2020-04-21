package com.khoa.kmnote2.model;

import java.io.Serializable;

public class Tag implements Serializable {
    public String name = "";
    public int amount;

    public Tag(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }
}
