package com.mdp.producer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ExtractCSV{
    
    private FileInputStream fis;
    
    public ExtractCSV(FileInputStream fis_in){
        fis = fis_in;
    }
    
    public String[][] extract(){
        ArrayList<String[]> data = new ArrayList<String[]>();
        String line;
        
        try (
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr);
             ) {
            
            for (int i = 0; i < 4; i++) {
                br.readLine();
            }
            while((line = br.readLine()) != null){//read rest of rows
                data.add(line.split(",,"));
            }
            
        } catch(IOException e){
            e.printStackTrace();
        }
        String[][] dataArr = new String[data.size()][data.get(0).length];
        dataArr = data.toArray(dataArr);
        return dataArr;
    }
    
    /*
        extracts rows and seperates the row into an array of strings where 
        each element contains comma seperated values of each table. 
        For example the row "0,0,,0,0,,0,0,,0,0,,0,0,,0,0,,0,0,,0,0,,0,0,,0,0" 
        seperates to ["0,0" , "0,0", ...] where each pair of 0's belongs to a 
        different table corresponding to the extracted headers since each table
        in the simulation data is seperated by a blank column 
        (represented by two consecutive commas)
    */
    
}