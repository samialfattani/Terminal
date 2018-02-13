# Terminal
This is a project allows you to connect to any Database using JDBC. You are enabled to use any SQL statement including DML, DDL, and DCL. It has been tested on MySQL, SQLite, PostgreSQL, ORACLE. This is very light weight program and won't take any noticed fingerprint. I have used JDBC Drivers for all DBMSs there. I have developed the UI using JavaFX technology.

# Usage
simply I'm using Gradle to build the project. use the following to start contributing.
```bash
$ git clone
$ gradle eclipse
```

# Try it out!
you can build and start the App directly using:
```bash
$ gradle run
```

# Build *.exe and Try it out!
Using [Gradle](https://gradle.org/) I have included Launch4J tool in order to build *.exe file so you can try it. use the following command (it is working on Windows only):
```bash
$ gradle createExe
\# Now you can find the executable file in '/exe/Terminal.exe'
```