// The package declaration specifies that this class belongs to the org.example.Pattern package.
package org.example.task1.Pattern;

// Importing the necessary classes from Spoon library for processing and reflecting on Java code.
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
// Abstract class representing a generic pattern detection mechanism.
public abstract class AbstractPattern {
    // Inner class 'elementSchema' is used to represent the structure of a detected code element.
    public class elementSchema {
        // Fields to store information about the detected element.
        public String message;    // A message related to the detection (e.g., a warning or error message).
        public String className;  // The name of the class where the detection occurred.
        public String methodName; // The name of the method where the detection occurred.
        public String path;       // The path of the file where the detection occurred.
        public int startLine;     // The starting line number of the detected code element.
        public int endLine;       // The ending line number of the detected code element.
        public String code;       // The code snippet or line of code related to the detection.

        // Constructor that initializes the fields when message, start line, end line, and code are given.
        elementSchema(String message, int startLine, int endLine, String code){
            this.message = message;
            this.startLine = startLine;
            this.endLine = endLine;
            this.code = code;
        }

        // Constructor that initializes all the fields, including className and methodName.
        elementSchema(String message, String className, String methodName, int startLine, int endLine, String code){
            this.className = className;
            this.methodName = methodName;
            this.message = message;
            this.startLine = startLine;
            this.endLine = endLine;
            this.code = code;
        }

        // Constructor that initializes the fields with path, start line, end line, and code.
        elementSchema(String message, String path, int startLine, int endLine, String code){
            this.path = path;
            this.message = message;
            this.startLine = startLine;
            this.endLine = endLine;
            this.code = code;
        }
    }

    // A list to store all detected elements.
    ArrayList<elementSchema> elements = new ArrayList<>();

    // Getter method that returns the list of detected elements.
    public ArrayList<elementSchema> getDetection(){
        return elements;
    }

    // Abstract method for processing patterns in the code (to be implemented by subclasses).
    public abstract void process();



    // Abstract method for printing the detection results (to be implemented by subclasses).
    public void print() {
        // Iterate through the list of detected empty catch blocks.
        for (elementSchema et : getDetection()) {
            // Format and print the message for each detected issue.
            System.out.println(String.format(
                    "Detected: %s at Line[%d:%d] in class %s",
                    et.message, et.startLine, et.endLine, et.path
            ));
        }
    }
}
