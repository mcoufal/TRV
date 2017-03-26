# TRV
This is my bachelor thesis project at Brno University of Technology - Faculty of Information Technology.

Idea of this project came from JBoss team @RedHat.

**Note:** *project is still in development phase and it is **not operational yet**!*

## What is it for?
Eclipse console output is often overflowed and it would be nice to have some gadget to see test run progress without messing with the focus of a tested instance. This project provides two essential parts - InRunJunit and TRView. Both are needed for propper functionality. The aim is to improve feedback when running GUI tests using JUnit.
### InRunJUnit
InRunJUnit is Eclipse plug-in, which creates JUnit listener and a server. Server is handling all available client connections and sending string representations of JUnit data from listener to all clients connected.
### TRView
TRView is simple SWT based application that allows connecting to a server. Server-Client architecture enables possibility to connect remotly. But what is crucial, it is diplaying tests progress and results to end user. Aim is to diplay information about all test cases are in a test run - which test case is currently running, what is the result of the specific testcase, how many errors or failures have been in a test run and stack traces of failed tests.
## How to use it?
1. install InRunJunit plug-in (using export, or update sites, or just try out within 'Eclipse IDE -> Run As -> Eclipse Application')
2. start the TRView app (build and export project or try out within 'Eclipse IDE -> Run As -> Java Application')
3. run tests
4. connect TRView to a server using '1234' port and IP address. ('127.0.0.1' for localhost)
5. Hit connect button to connect and receive results
