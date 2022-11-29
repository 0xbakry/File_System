/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author omarb
 */
import java.io.Serializable;
import java.util.ArrayList;

public class Directory implements Serializable  {
    
    public ArrayList<File> files;
    public String name;
    public ArrayList<Directory> subDirectories;

    public Directory() {
        files = new ArrayList<>();
        subDirectories = new ArrayList<>();
        this.name = name;
    }
    public Directory(String name)
    {
        files = new ArrayList<>();
        subDirectories = new ArrayList<>();
        this.name = name;
    }

}
