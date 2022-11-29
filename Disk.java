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
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;

public class Disk implements Serializable {

    public Directory directory;
    int numberOfBlocks, mode = 0, emptySpace,
            allocatedSpace ,indexOfcomma=0;
    Block[] blocks;
    String capability = "00", UserName = "" , safePath="", str = "", name, pass;
    ArrayList<User> users_with_cap = new ArrayList<User>();

    public Disk(int numberOfBlocks, int mode) {
        directory= new Directory("root");
        this.numberOfBlocks = numberOfBlocks;
        this.emptySpace = numberOfBlocks;
        this.allocatedSpace = 0;
        this.mode = mode;
        blocks = new Block[numberOfBlocks+1];
        for(int i = 0 ; i < numberOfBlocks+1  ;++i) blocks[i] = new Block();
        for(int i = 0 ; i < numberOfBlocks  ;++i) blocks[i].allocated = false;
        blocks[numberOfBlocks].allocated = true;
    }

    public Boolean runCommand(String command, User currentUser){

        if (command.equalsIgnoreCase("TellUser")) {
            System.out.println("The name of current user is: " + currentUser.getName());
            return true;
        }
        Parser parser = new Parser();
        Boolean isValid = parser.parse(command, currentUser);
        if (!isValid){
            System.out.println("ERROR 404:(");
            return false;
        }
        else {

            if (parser.cmd.equalsIgnoreCase("CreateFile")){
                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
                if(fileIsExist(dirs, parser.fileName, 1, directory)){
                    System.out.println("File already exist");
                    return false;
                }
                Block []allocatedBlocks = allocateContiguous(parser.size);
                if(allocatedBlocks.length != 0 ) {
                    return createFile(dirs, parser.fileName, parser.size, 1, directory , allocatedBlocks);
                }
                else{
                    System.out.println("No enough space :(");
                    return false;
                }
            }
            else if (parser.cmd.equalsIgnoreCase("CreateFolder")){
                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
                return createFolder(dirs, parser.folderName , 1 , directory);
            }
            else if (parser.cmd.equalsIgnoreCase("DeleteFile")){
                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
                return deleteFile(dirs, parser.fileName , 1 , directory);
            }
            else if (parser.cmd.equalsIgnoreCase("DeleteFolder")){
                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
                return deleteFolder(dirs, parser.folderName , 1 , directory);
            }
            else if (parser.cmd.equalsIgnoreCase("DisplayDiskStatus")){
                displayDiskStatus();
            }
            else if (parser.cmd.equalsIgnoreCase("DisplayDiskStructure")){
                System.out.println("root");
                displayDiskStructure(directory , 4 , 1);
            }
            
            else if (parser.cmd.equalsIgnoreCase("Grant")){

                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
                capability = parser.capability; UserName = parser.UserName; safePath = parser.safePath;
                return Grant(dirs, parser.folderName , 1 , directory);
            }
            else if (parser.cmd.equalsIgnoreCase("CFolder")){
                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
               
                if (check_delete_Capability(currentUser, parser.folderName, parser.Cpath)) {
                    return deleteFolder(dirs, parser.folderName, 1, directory);
                } else {
                    System.out.println("Not Allowed!");
                        return false;
                        }
            }
              else if (parser.cmd.equalsIgnoreCase("MFolder")){
                String []dirs = parser.path.split(" ");
                if(!dirs[0].equalsIgnoreCase("root"))
                {
                    System.out.println("Wrong path!");
                    return false;
                }
                if (check_create_Capability(currentUser, parser.folderName, parser.Cpath)) {
                    return createFolder(dirs, parser.folderName, 1, directory);
                } else {
                    System.out.println("Not Allowed!");
                        return false;
                        }
            }
            
        }
        return true;
    }

    
    public Boolean check_delete_Capability(User currentUser,String folderame, String dir) {

        String path = "", cap = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader("E:\\Materiales\\OS\\Operating-System-2-master\\File System Allocation\\capability.txt"));

            while ((str = in.readLine()) != null) {
                String temp[] = str.split(",");
                for (int k = 0; k < temp.length; k += 3) {
                    path = temp[k];
                    name = temp[k + 1];
                    cap = temp[k + 2];
                    User newuser = new User(name, path, cap);
                    users_with_cap.add(newuser);
                }

            }

            in.close();
        } catch (IOException e) {
        };

        for (int i = 0; i < users_with_cap.size(); i++) 
        {
            if (users_with_cap.get(i).getDir().equalsIgnoreCase(dir)) {
                if (currentUser.getName().equalsIgnoreCase(users_with_cap.get(i).getName())) {
                    if (users_with_cap.get(i).getCap().charAt(1) == '1') {
                        System.out.println("Allowed!");
                        return true;
                    }

                }
            }
        }

        return false;
    }
    
    public Boolean check_create_Capability(User currentUser,String folderame, String dir) {

        String path = "", cap = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader("E:\\Materiales\\OS\\Operating-System-2-master\\File System Allocation\\capability.txt"));

            while ((str = in.readLine()) != null) {
                String temp[] = str.split(",");
                for (int k = 0; k < temp.length; k += 3) {
                    path = temp[k];
                    name = temp[k + 1];
                    cap = temp[k + 2];
                    User newuser = new User(name, path, cap);
                    users_with_cap.add(newuser);
                }

            }

            in.close();
        } catch (IOException e) {
        };

        for (int i = 0; i < users_with_cap.size(); i++) 
        {
            if (dir.contains(users_with_cap.get(i).getDir())) {
                if (currentUser.getName().equalsIgnoreCase(users_with_cap.get(i).getName())) {
                    if (users_with_cap.get(i).getCap().charAt(0) == '1') {
                        System.out.println("Allowed!");
                        return true;
                    }

                }
            }
        }

        return false;
    }
    
    public Boolean Grant(String[] path, String folderName , int index , Directory current) {
         for (int i = index; i<path.length ; ++i)
            System.out.println("path: " + path[i]);

        Directory target;
        for (int i = index; i<path.length ; ++i)
        {
            if (checkName(current ,path[i]))
            {
                current = getDirectory(current , path[i]);
            }
            else
            {
                System.out.println("Wrong path :(");
                return false;
            }
        }
        if(checkName(current , folderName))
        {
            target = getDirectory(current  , folderName);
        }
        else
        {
            System.out.println("Folder not found");
            return false;
        }
        try {
            Writer output = new BufferedWriter(new FileWriter("E:\\Materiales\\OS\\Operating-System-2-master\\File System Allocation\\capability.txt", true));
            output.append( safePath+ ',' + UserName + ',' + capability + ',');
            output.close();
        } catch (IOException io) {
        };
        System.out.println("You've changed the premissions.");
        return true;
        
       }
    
    
    public Boolean createFile(String[] path, String fileName, int size, int index, Directory current , Block[] blocks) {
        if(index < path.length)
        {
            if(checkName(current ,path[index]))
            {
                createFile(path, fileName, size , index+1 , getDirectory(current , path[index]) , blocks);
            }
            else
            {
                current.subDirectories.add(new Directory(path[index]));
                createFile(path , fileName , size , index+1 , getDirectory(current , path[index]) , blocks);
            }
        }
        else if (index == path.length)
        {
            for(int i = 0 ; i<current.files.size();++i){
                if(current.files.get(i).name.equalsIgnoreCase(fileName)){
                    return false;
                }
            }
            String completePath = "";
            for(int i=0 ; i<path.length ; i++) completePath += path[i] + " ";
            File newFile;
            if (mode == 1){
                newFile =new File(fileName, completePath ,blocks);
            }
            else {
                newFile =new File(fileName, completePath ,blocks);
            }

            current.files.add(newFile);
            emptySpace -= size;
            allocatedSpace += size;
        }
        return true;
    }

    public boolean checkName(Directory dir, String name)
    {
        for(int i=0 ; i<dir.subDirectories.size() ; i++)
        {
            if(dir.subDirectories.get(i).name.equalsIgnoreCase(name)) return true;
        }
        return false;
    }
    public Directory getDirectory(Directory dir, String name)
    {
        for(int i=0 ; i<dir.subDirectories.size() ; i++)
        {
            if(dir.subDirectories.get(i).name.equalsIgnoreCase(name)) return dir.subDirectories.get(i);
        }
        return null;
    }
    public Boolean createFolder(String[] path, String folderName , int index , Directory current) {
        if(index < path.length)
        {
            if(checkName(current ,path[index]))
            {
                createFolder(path, folderName,index+1 , getDirectory(current , path[index]));
            }
            else
            {
                current.subDirectories.add(new Directory(path[index]));
                createFolder(path , folderName ,index+1 , getDirectory(current , path[index]));
            }
        }
        else if (index == path.length)
        {
            if (checkName(current ,path[index-1])){
                System.out.println("Folder already exist");
                return false;
            }
            current.subDirectories.add(new Directory(folderName));
        }
        return true;
    }

    public Boolean deleteFile(String[] path, String fileName , int index , Directory current) 
    {
       Directory newCurrent = null;

     
        for (int i = 0; i<path.length ; ++i)
        {
            newCurrent = getDirectory(current , path[i]);
        }
         
        if(mode == 1)
        {
             boolean check = false;
            for (int i = 0; i< newCurrent.files.size() ;++i)
            {

                if (newCurrent.files.get(i).name.equalsIgnoreCase(fileName))
                {
                    Block curBlocks [] = newCurrent.files.get(i).getAllocatedBlocks();
                    for (int j = 0 ; j < curBlocks.length ; ++j)
                    {
                        curBlocks[j].allocated = false;
                    }
                    emptySpace += curBlocks.length;
                    allocatedSpace -= curBlocks.length;
                    newCurrent.files.remove(newCurrent.files.get(i));
                    check = true;
                    break;
                }
            }
            if (!check){
                System.out.println("File Not Found :(");
                return false;
            }
        }
        
        else if(mode == 2){
            for (int i = 0; i< newCurrent.files.size() ;++i){
                if (newCurrent.files.get(i).name.equalsIgnoreCase(fileName)){
                    Block curBlocks [] = newCurrent.files.get(i).getAllocatedBlocks();
                    for (int j = 0 ; j < curBlocks.length ; ++j){
                        curBlocks[j].allocated = false;
                    }
                    if(curBlocks[0].subBlocks.size() > 0)
                    {
                        curBlocks[0].subBlocks.clear();
                    }
                    emptySpace += curBlocks.length;
                    allocatedSpace -= curBlocks.length;
                    newCurrent.files.remove(newCurrent.files.get(i));
                     
                }
                else if (i == newCurrent.files.size() -1){
                    System.out.println("File Not Found :(");
                    return false;
                }
            }
        }
        return true;
    }

    public Boolean deleteFolder(String[] path, String folderName , int index , Directory current) {

        Directory target;
        for (int i = index; i<path.length ; ++i){
            if (checkName(current ,path[i])){
                current = getDirectory(current , path[i]);
            }
            else{
                System.out.println("Wrong path :(");
                return false;
            }
        }
        if(checkName(current , folderName))
        {
            target = getDirectory(current  , folderName);
        }
        else
        {
            System.out.println("Folder not found");
            return false;
        }
        if(mode == 1){
            for (int i = 0; i< target.files.size() ;++i){
                Block curBlocks [] = target.files.get(i).getAllocatedBlocks();
                for (int j = 0 ; j < curBlocks.length ; ++j){
                    curBlocks[j].allocated = false;
                }
                emptySpace += curBlocks.length;
                allocatedSpace -= curBlocks.length;
                target.files.remove(target.files.get(i));
            }
            current.subDirectories.remove(target);
        }
        else if(mode == 2){
            for (int i = 0; i< target.files.size() ;++i){
                Block curBlocks [] = target.files.get(i).getAllocatedBlocks();
                for (int j = 0 ; j < curBlocks.length ; ++j){
                    curBlocks[j].allocated = false;
                }
                curBlocks[0].subBlocks.clear();
                emptySpace += curBlocks.length;
                allocatedSpace -= curBlocks.length;
                target.files.remove(target.files.get(i));
            }
            current.subDirectories.remove(target);
        }
        return true;
    }

    public void displayDiskStatus(){
        System.out.println("1-Empty space : " + emptySpace);
        System.out.println("2-Allocated space : " + allocatedSpace);
        System.out.println("\n3-Empty Blocks in the Disk : ");
        for (int i = 0 ; i < numberOfBlocks ; ++i){
            if (!blocks[i].allocated){
                System.out.print(i + " ");
            }
            if (i % 30 == 0 && i!=0 && !blocks[i].allocated){
                System.out.println("");
            }
        }
        System.out.println("\n\n4-Allocated  Blocks in the Disk : ");
        for (int i = 0 ; i < numberOfBlocks ; ++i){
            if (blocks[i].allocated){
                System.out.print(i + " ");
            }
            if (i % 30 == 0 && i!=0 && blocks[i].allocated){
                System.out.println("");
            }
        }
    }

    public void displayDiskStructure(Directory current , int spaces , int level){

        if(current.subDirectories.size() == 0 && current.files.size() == 0) return;

        else if(current.subDirectories.size() ==0)
        {
            for (int i=0 ; i<current.files.size() ; i++)
            {
                for (int j=0 ; j<spaces*level ; j++) System.out.print(" ");
                System.out.println(current.files.get(i).name);
            }
            return;
        }
        else
        {
            for (int i=0 ; i<current.files.size() ; i++)
            {
                for (int j=0 ; j<spaces*level ; j++) System.out.print(" ");
                System.out.println(current.files.get(i).name);
            }
            for (int i=0 ; i<current.subDirectories.size() ; i++)
            {
                for (int j=0 ; j<spaces*level ; j++) System.out.print(" ");
                System.out.println(current.subDirectories.get(i).name);
                displayDiskStructure(current.subDirectories.get(i) , spaces ,level+1);
            }
        }
    }
    public Block[] allocateContiguous(int size)
    {
        int space = 0;
        int start = 0;
        int bestFit = Integer.MAX_VALUE;
        //Get the best fit place
        for(int i=0 ; i<numberOfBlocks+1 ; i++)
        {
            if(!blocks[i].allocated) space+=1;
            else
            {
                if(space >= size && space<bestFit)
                {
                    bestFit = space;
                    start = i - space;
                }
                space = 0;
            }
        }
        System.out.println(start);
        System.out.println(start + size);
        if(space <= numberOfBlocks)
        {
            int index = 0;
            Block []result = new Block[size];
            for(int i=start ; i<start+size ; i++)
            {
                blocks[i].allocated = true;
                result[index++] = blocks[i];
            }
            return result;
        }
        else
        {
            Block []result = new Block[0];
            return result;
        }
    }
    public Block[] allocateIndexed(int size)
    {
        int count = 0;
        int start = -1;
        int currentStart = 0;
        for(int i=0; i<numberOfBlocks ;++i){
            if(!blocks[i].allocated){
                count++;
            }
            else{
                count = 0;
                currentStart = i +1;
            }
            if (size  == count+1){
                start = currentStart;
                break;
            }
        }
        if(start != -1){
            Block [] arr = new Block[size+1];
            arr[0] = blocks[start];
            blocks [start].allocated = true;
            for(int i=0; i<size ;++i){
                blocks[start].subBlocks.add(i+start+1);
            }
            count  = 1;
            for(int i=start+1; i<size ;++i){
                blocks [i].allocated = true;
                arr[count++] = blocks [i];
            }
            return arr;
        }
        else{
            Block [] arr = new Block[0];
            return arr;
        }
    }

    public Boolean fileIsExist(String[] path, String fileName, int index, Directory current) {
        if(index < path.length)
        {
            if(checkName(current ,path[index]))
            {
                fileIsExist(path, fileName, index+1 , getDirectory(current , path[index]));
            }
            else
            {
                return false;
            }
        }
        else if (index == path.length) {
            for (int i = 0; i < current.files.size(); ++i) {
                if (current.files.get(i).name.matches(fileName)) {
                    return true;
                }
            }
        }
        return false;
    }


}


               // System.out.println("name: " + users_with_cap.get(k).getName() + " path "+  users_with_cap.get(k).getDir() + " cap " +users_with_cap.get(k).getCap());
