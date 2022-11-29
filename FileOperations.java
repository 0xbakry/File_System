/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author omarb
 */

import java.io.*;

public class FileOperations implements Serializable{
    private static final long serialVersionUID = 1L;
    String path = "C:\\Users\\omarb\\Documents\\NetBeansProjects\\FileSystem\\DiskStructure.txt";
    public FileOperations() {

    }
    public void saveFile(Disk disk) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(disk);

        fileOutputStream.close();
        objectOutputStream.close();
    }
    public Disk loadFile() throws IOException, ClassNotFoundException {

        InputStream file = new FileInputStream(path);
        InputStream buffer = new BufferedInputStream(file);
        ObjectInput input = new ObjectInputStream(buffer);

        Disk disk = (Disk) input.readObject();

        file.close();
        buffer.close();
        input.close();

        return  disk;
    }
    public void clearFile() throws IOException {
        FileWriter file = new FileWriter(path, false);
        PrintWriter obj = new PrintWriter(file, false);
        obj.flush();
        obj.close();
        file.close();
    }
}
