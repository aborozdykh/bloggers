package com.alexcorp.bloggers.statistics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class CommandExecutor {

    public static String REBUILD;
    public static String RESTART;
    public static String UPDATE_RESOURCES;
    public static String CLEAN_CACHE;

    public static int runCommand(String command, StringBuilder output){
        System.out.println("Run: " + command);
        try{
            Process process = Runtime.getRuntime().exec(command);
            while (process.isAlive()) {
                Thread.sleep(200);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while(reader.ready()){
                output.append(reader.readLine()).append("\n");
            }
            if(!output.toString().equals("")){
                System.out.println(output.toString());
                return -1;
            }

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while(reader.ready()){
                output.append(reader.readLine()).append("\n");
            }

            System.out.println(output.toString());

            int exitVal = process.waitFor();

            return exitVal;

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();

            return -1;
        }
    }

    @Value("${commands.rebuild}")
    public void setRebuild(String name){
        CommandExecutor.REBUILD = name;
    }

    @Value("${commands.restart}")
    public void setRestart(String name){
        CommandExecutor.RESTART = name;
    }

    @Value("${commands.update-res}")
    public void setUpdateRes(String name){
        CommandExecutor.UPDATE_RESOURCES = name;
    }

    @Value("${commands.clean-cache}")
    public void setCleanCache(String name){
        CommandExecutor.CLEAN_CACHE = name;
    }
}
