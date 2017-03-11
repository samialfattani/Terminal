package frawla.terminal.test;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class ExceptionsDemo 
{

    public static void main(String[] args) 
    {
        try 
        {
            int arr[]={1,2,3,4,5,6,7,8,9,10};
            for(int i=0; i<10; i++){
                if(i%200==0){
                    System.out.println("i =" + i);
                    throw new Exception();
                }            
            }
        } 
        catch (Exception e) {
            System.err.println("An exception was thrown");
        }finally{
        	System.err.println("fffffffff");
        }
        System.err.println("dddddddd");
    }
}