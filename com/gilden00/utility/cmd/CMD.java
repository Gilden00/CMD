package com.gilden00.utility.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CMD {
	
	private Process process;
	private PrintWriter stdin;
	private BufferedReader stdout;
	
	/**
	 * Constructor for a CMD object
	 * Creates a new process for Windows Command Prompt
	 * Allows for the execution of cmd command
	 * @throws IOException
	 */
	public CMD() throws IOException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream(true);
		pb.command("cmd");
		
		process = pb.start();
		stdin = new PrintWriter(process.getOutputStream());
		stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		write("@echo off");
		
		// TODO implement trim of the cmd header and @echo off
	}
	
	/**
	 * Executes a command in the command prompt process.
	 * Some commands may return with extra empty lines compared to running it from the console.
	 * @param command The command to run
	 * @return The output from running the command
	 * @throws IOException 
	 */
	public String[] write(String command) throws IOException {
		// Calls the write method and sets ignoreEmptyLines to false. Returns the output.
		return write(command, false);
	}
	
	/**
	 * Executes a command in the command prompt process.
	 * Some commands may return with extra empty lines compared to running it from the console.
	 * @param command The command to run
	 * @param ignoreEmptyLines If set to true, output will not include empty lines.
	 * @return The output from running the command
	 * @throws IOException
	 */
	public String[] write(String command, boolean ignoreEmptyLines) throws IOException {
		// Execute the command
		stdin.println(command);
		// Queue up a marker so we know when the command has finished running
		// The command prompt will wait until the command has finished running before processing this line
		// The command is visible in cmd, but generates no output
		stdin.println("rem 'end'");
		// Flush the stream. This forces the buffer to empty and write the contents to the process
		stdin.flush();
		
		// Create an ArrayList to store the output
		ArrayList<String> output = new ArrayList<String>();
		// Create a String to use for reading the command output
		// The first line is trimmed off. This is just the command passed into this method
		String line = stdout.readLine();
		
		/* If the stream is ready to be read, read the next line
		 * Check if we are ignoring empty lines
		 * Add the line to the output ArrayList
		 * 
		 * Do this until the line equals the marker the added to indicate the end
		 */
		do {
			if(stdout.ready()) {
				
				line = stdout.readLine();
				
				if(!line.equals("rem 'end'")) {
					if(ignoreEmptyLines) {
						if(!line.equals("")) {
							output.add(line);
						}
					}else {
						output.add(line);
					}
				}
			}
		}while(!line.equals("rem 'end'"));
		
		// Convert the ArrayList to a String Array and return
		String[] str = new String[output.size()];
		return output.toArray(str);
	}
	
	public static void main(String[]args) throws IOException {
		
		CMD cmd = new CMD();
		
		String[] output = cmd.write("wmic product get", true);
		
		for(int i = 0; i < output.length; i++) {
			System.out.println(output[i]);
		}
		
	}
	
}
