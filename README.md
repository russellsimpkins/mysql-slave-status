MySQL Slave Status
==================

This is a simple application that queries slave databases to determine how
many seconds behind the master they are. It reads a properties file
to get configuration information. It reads a json file to get the mysql
slaves to connect to. It then writes a json file that can be used for
nagios monitoring or dashboards.

Its a simple app I first wrote in PHP. Its my first foray back into java
and Spring, so its a little rough around the edges, but it works and 
only needs minor tweaks if it were to be used in an enterprise 
environment, but maybe not. If you were to switch environments you may 
decide to modify the properties files and that may or may not require 
changes to the Application.java file.