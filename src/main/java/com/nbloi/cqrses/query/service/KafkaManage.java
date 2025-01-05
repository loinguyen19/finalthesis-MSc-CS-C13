package com.nbloi.cqrses.query.service;

import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class KafkaManage {

    public static void stopKafka() throws IOException, InterruptedException {
        executeCommand("sudo systemctl stop kafka");
     String commandLine = "wsl" + "," + "-e" + "," + "./bin/kafka-server-stop.sh" + "," + "config/server.properties";
     executeCommand(commandLine);
    }

    public static void startKafka() throws IOException, InterruptedException {
        executeCommand("sudo systemctl start kafka");
        String commandLine = "wsl" + "," + "-e" + "," + "./bin/kafka-server-start.sh" + "," + "config/server.properties";
        executeCommand(commandLine);
    }

    public static void executeCommand(String commandLine) {
        try {
            // Specify the directory where you want to run the command
            // TODO: replace with your Kafka directory containing the bin file
            File directory = new File("/mnt/c/Users/LNG/Downloads/external-servers/cqrs-es/kafka_2.13-3.9.0");

            // Create a ProcessBuilder with the command you want to execute
//            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", "sudo systemctl stop kafka");
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.directory(directory); // Set the working directory
//            processBuilder.command("cmd.exe", "/c", "your-directory"); // For Windows
//             processBuilder.command("sh", "-c", ".your-directory"); // For Linux/Mac
            processBuilder.command(commandLine); // For Linux for Windows (WSL)

            // Start the process
            Process process = processBuilder.start();


            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to finish and check the exit code
            int exitCode = process.waitFor();
            System.out.println("Command exited with code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
