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

public class Block implements Serializable {
    boolean allocated;
    public ArrayList<Integer> subBlocks;

    public Block() {
        subBlocks = new ArrayList<>();
    }

    public Block(boolean allocated, ArrayList<Integer> subBlocks) {
        this.allocated = allocated;
        this.subBlocks = subBlocks;
    }

}
