package com.mac.macysfilesapp.utils;

/**
 * Created by admin on 5/5/2016.
 */
public class MyFile {

    private String name;
    private int size;

    public MyFile() {

    }

    public MyFile(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

}
