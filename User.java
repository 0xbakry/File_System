/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author omarb
 */
public class User {
    
    private String  name ,pass, ability="", dir;
    public int role= 0;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public void setCap(String cap) {
        this.ability = cap;
    }

    public String getCap() {
        return ability;
    }
    public String getDir() {
        return dir;
    }

    public User(){};
    public User(String name, String pass)
    {
        this.name = name;
        this.pass = pass;
    }
     public User(String name, String dir, String cap)
    {
        this.name = name;
        this.dir = dir;
        this.ability = cap;
    }
    public void change(User u)
    {
        name = u.name;
        pass = u.pass;
       
    }
 
}

