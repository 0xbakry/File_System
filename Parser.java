/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author omarb
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    ArrayList<String> cap_list = new ArrayList<String> (List.of("00", "01", "10", "11"));
    String path = "", fileName, folderName, cmd, capability = "00", UserName = "", safePath = "", Cpath="";
    int size, indexOfcomma = 0;
    boolean checkCreate = false;
    ArrayList<String> commands = new ArrayList<String>(List.of("CreateFile", "CreateFolder", "DeleteFile",
            "DeleteFolder", "DisplayDiskStatus", "DisplayDiskStructure", "CUser", "Login", "Grant", "CFolder", "MFolder"));

    ArrayList<User> users = new ArrayList<User>();
    String str = "", name, pass;

    public boolean checkName(Directory dir, String name) {
        for (int i = 0; i < dir.subDirectories.size(); i++) {
            if (dir.subDirectories.get(i).name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean parse(String command, User current) {
        String[] spaces = command.split(" ");
        try {
            BufferedReader in = new BufferedReader(new FileReader("E:\\Materiales\\OS\\Operating-System-2-master\\File System Allocation\\user.txt"));
            int i = 0;
            while ((str = in.readLine()) != null) {
                indexOfcomma = str.indexOf(',');
                name = str.substring(0, indexOfcomma);
                pass = str.substring(indexOfcomma + 1);
                User newuser = new User(name, pass);
                users.add(newuser);
            }
            in.close();
        } catch (IOException e) {
        };

        if (commands.contains(spaces[0])) {
            // Global for all
            cmd = spaces[0];
            if (cmd.equalsIgnoreCase("CreateFile") && spaces.length == 3) {
                String[] paths = spaces[1].split("/");
                if (paths[paths.length - 1].contains(".")) {
                    fileName = paths[paths.length - 1];
                    folderName = paths[paths.length - 2];
                } else {
                    System.out.println("wrong extension");
                    return false;
                }
                // To check if the index is a valid integer
                try {
                    size = Integer.parseInt(spaces[2]);
                } catch (Exception e) {
                    return false;
                }
                // Check cmd to take the path if it not exist to create it
                checkCreate = true;
                // take the path to check it , and if it not exist create it

                for (int i = 0; i < paths.length - 1; i++) {
                    path += paths[i] + " ";
                }

                return true;
            } else if (cmd.equalsIgnoreCase("Grant") && spaces.length == 4) {

                if (current.role != 1) {
                    System.out.println("You're not the admin!!");
                    return false;
                }
                boolean status = false;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getName().equalsIgnoreCase(spaces[1])) {
                        status = true;
                    }
                }
                if (!status) {
                    System.out.println("User Doesn't Exist");
                    return false;
                } else {
                    String[] paths = spaces[2].split("/");
                    if (spaces.length == 4) 
                    {
                        for (int i = 0; i < paths.length - 1; i++) {
                            path += paths[i] + " ";
                        }
                        folderName = paths[paths.length - 1];
                        for(int i =0; i< cap_list.size(); i++)
                        {   
                            if (spaces[3].equalsIgnoreCase(cap_list.get(i))) {
                                capability = cap_list.get(i);
                                UserName = spaces[1];
                                safePath = spaces[2];
                                return true;
                            }

                        }
                        System.out.println("Wrong premissions!!");
                        return false;

                    } 
                    else 
                       return false;

                }

            } else if (cmd.equalsIgnoreCase("DeleteFile") && spaces.length == 2) {
                String[] paths = spaces[1].split("/");
                if (paths[paths.length - 1].contains(".")) //// MAy be not the right ext
                {
                    fileName = paths[paths.length - 1];
                    folderName = paths[paths.length - 2];
                } else {
                    System.out.println("wrong extinction");
                    return false;
                }
                for (int i = 0; i < paths.length - 1; i++) {
                    path += paths[i] + " ";
                }
                System.out.println(path);

                return true;
            } else if ((cmd.equalsIgnoreCase("DisplayDiskStatus") || spaces[0].equalsIgnoreCase("DisplayDiskStructure") && spaces.length == 1)
                    && spaces.length == 1) {
                return true;
            } else if ((cmd.equalsIgnoreCase("CreateFolder") || spaces[0].equalsIgnoreCase("DeleteFolder") && spaces.length == 2)) {
                String[] paths = spaces[1].split("/");
                if (spaces.length == 2) {
                    for (int i = 0; i < paths.length - 1; i++) {
                        path += paths[i] + " ";
                    }
                    folderName = paths[paths.length - 1];
                    return true;
                } else {
                    return false;
                }
            }
             else if ((cmd.equalsIgnoreCase("CFolder") || spaces[0].equalsIgnoreCase("MFolder") && spaces.length == 2)) {
                 Cpath=spaces[1];
                String[] paths = spaces[1].split("/");
                if (spaces.length == 2) {
                    for (int i = 0; i < paths.length - 1; i++) {
                        path += paths[i] + " ";
                    }
                    folderName = paths[paths.length - 1];
                    return true;
                } else {
                    return false;
                }
            } else if (cmd.equalsIgnoreCase("CUser")) {
                if (current.role == 1) {
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getName().equalsIgnoreCase(spaces[1])) {
                            System.out.println("The username is taken!");
                            return false;
                        }
                    }
                    User newuser = new User(spaces[1], spaces[2]);
                    try {
                        Writer output = new BufferedWriter(new FileWriter("E:\\Materiales\\OS\\Operating-System-2-master\\File System Allocation\\user.txt", true));
                        output.append("\n" + spaces[1] + "," + spaces[2]);
                        output.close();
                    } catch (IOException io) {
                    };
                    System.out.println("You've created your user.");
                    return true;
                } else {
                    System.out.println("You are not the admin!");
                    return false;
                }
            } else if (cmd.equalsIgnoreCase("Login")) {
                boolean work = true;

                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getName().equalsIgnoreCase(spaces[1]) && users.get(i).getPass().equals(spaces[2])) {
                        System.out.println("Welcome " + spaces[1] + "!");
                        if (users.get(i).getName().equalsIgnoreCase("admin")) {
                            current.role = 1;
                        } else {
                            current.role = 0;
                        }
                        current.change(users.get(i));
                        return true;
                    } else {
                        work = false;
                    }
                }
                if (!work) {
                    System.out.println("Wrong username or password!");
                    return false;
                }

            }

        }
        return false;
    }
}
