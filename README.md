# TRV
This is my bachelor thesis project at Brno University of Technology - Faculty of Information Technology.

Idea of this project came from JBoss team @RedHat.

**Note:** _Project is still in development phase - some features may not work!_

## What is it for?
Eclipse console output is often overflowed and it would be nice to have some gadget to see test run progress without messing with the focus of a tested instance. This project provides two essential parts - InRunJunit and TRView. Both are needed for propper functionality. The aim is to improve feedback when running GUI tests using JUnit.
### InRunJUnit
InRunJUnit is Eclipse plug-in, which creates JUnit listener and a server. Server is handling all available client connections and sending string representations of JUnit data from listener to all clients connected.
### TRView
TRView is simple SWT based application that allows connecting to a server. Server-Client architecture enables possibility to connect remotly. But what is crucial, it is diplaying tests progress and results to end user. Aim is to diplay information about all test cases are in a test run - which test case is currently running, what is the result of the specific testcase, how many errors or failures have been in a test run and stack traces of failed tests.
## How to use it?
1. install InRunJunit plug-in
 * clone this repository
 * start Eclipse IDE
 * go to '`Help`' -> '`Install New Software...`'
 * add Update site with '`Add...`' -> '`Local...`' and set it to _<PATH_TO_CLONED_REPO>/TRV/mcoufal.InRunJUnit.updateSite_
 * select feature, confirm license and complete instalation
 * restart eclipse
2. start the TRView app
 * go to _<PATH_TO_CLONED_REPO>/TRV/TRView_
 * run jar file ('`java -jar TRView.jar`')
3. run tests with JUnit
4. connect TRView to a server using '7357' port and IP address. ('127.0.0.1' for localhost)
5. Hit '`TRView`' -> '`Connect...`' and receive results!
