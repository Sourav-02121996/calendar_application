# Java Swing GUI Application

## Overview

This Java Swing-based desktop calendar application is built using the Model-View-Controller (MVC) 
architecture. It allows users to create and manage multiple calendars, add and edit events, and 
seamlessly switch between different calendars. Additionally, the application supports importing 
events from a CSV file and exporting existing events to CSV for external use.

## Features
* Create multiple calendars
* Change the current calendar
* Create an event
* Create a repeating event
* Edit an event
* Edit multiple events with the same subject
* Edit multiple events that occur after a certain date and time
* Export the events to a CSV file that can be imported into Google Calendar.
* Import the events into the GUI.

## Requirements
- System should be enabled with Java 11 or higher

## How to Run

when invoked in the Cmd or in terminal by the below command the program opens the script file, 
executes it and then shuts down. 
*     java -jar assignment6.jar --mode headless path-of-script-file

when invoked in the Cmd or in terminal by the below command the program is in an 
interactive text mode,allowing the user to type the script and execute it one line at a time.
*     java -jar assignment6.jar --mode interactive

when invoked by the below command the program opens graphical user interface. The graphical user 
interface also opens if you simply double-click on the jar file.
*     java -jar assignment6.jar