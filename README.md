# Calendar Application

## Overview

This desktop calendar application follows the Model-View-Controller design pattern. The
application can be used via a GUI or a CLI through the terminal. It allows users to create and 
manage multiple calendars, add and edit events, and seamlessly switch between different calendars. 
Additionally, the application supports importing events from a CSV file and 
exporting events to a CSV file.

## Requirements
- System should be enabled with Java 11 or higher

## Instructions
when invoked in the Cmd or in terminal by the below command the program opens the script file,
executes it and then shuts down.
*     java -jar Program.jar --mode headless path-of-script-file

when invoked in the Cmd or in terminal by the below command the program is in an
interactive text mode,allowing the user to type the script and execute it one line at a time.
*     java -jar Program.jar --mode interactive

when invoked by the below command the program opens graphical user interface. The graphical user
interface also opens if you simply double-click on the jar file.
*     java -jar Program.jar

To exit out of the program when running in interactive mode,
use the `exit` commands.

## Features
* Create multiple calendars
* Change the current calendar
* Edit a calendar
* Copy events from one calendar to another
* Create an event
* Create a repeating event
* Edit an event
* Edit multiple events with the same subject
* Edit multiple events that occur after a certain date and time
* Get the calendar status on a particular date and time
* View all events occurring in a given time range
* View all events occurring on a given date
* Export events to a CSV file that can be imported into Google Calendar.
* Import events from a CSV file

All features are working.

## Notes
* When the `exit` command is executed in headless mode, the program terminates execution.
* The program continues until the end of the file if there is no exit command in the file in headless mode.

The following fields can be edited for each event. 
- `location`
- `description`
- `subject`
- `startDateTime` 
- `endDateTime`
- `isPrivate`

The following fields can be edited for repeating events. 
- `repeatDays`
- `repeatEndDateTime`
- `repeatNumber`

The following fields can be edited for a calendar:
- `name`
- `timezone`

Values for the following fields will require double quotes if the value consists of multiple words. For all other properties, quotes are not necessary.
  * event subject
  * event description
  * event location
  * calendar name

Creating events
* All day events span one day when initially created. An all day event is considered as an event that goes from midnight to midnight.
* An event ending at time x does not conflict with an event that starts at time x. They are considered back-to-back events.

Repeating Events
* `repeatNumber`, `repeatDays` and `repeatEndDateTime` are specific to repeating events and cannot be set or changed for a single event.

* When the 'repeatDays' property is used, the repeat days will be calculated starting from the specified date.

* When specifying repeat days, they must be explicitly mentioned in the command.
  - If the start date specified in a create command does not fall within the defined repeat days, it will not be included.
  - Similarly, if the 'edit from date' in an edit command is not within the newly defined repeat days, it will not be included.

Copying events
* When copying multiple events from one calendar to another, conflicting events are skipped. Only non-conflicting events are copied over.

