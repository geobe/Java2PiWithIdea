## Java To Pi Cross Development With Gradle and Idea
IntelliJ IDEA cross development targeting RaspberryPi (or other Linux Systems) for Java and other JVM languages 
( Groovy, Kotlin, ...) using Gradle. 
This works well for IntelliJ IDEA (community and ultimate), tested on Windows 10 and Linux.
### Preparing the Environment
#### Prerequisites on Pi Site
Scripts on development computer need to run commands on target Pi system:
* Existing ssh login on Pi with private key authentication
  (see e.g. [here](https://www.cyberciti.biz/faq/how-to-set-up-ssh-keys-on-linux-unix/))  
  **Login with ssh at least once from the command line** to make sure that the target host key
  is accepted. 

Pi programs often access GPIO hardware or expose privileged ports
 which needs root privileges:
* Pi user can run sudo commands without entering a password 
(see e.g. [here](https://www.cyberciti.biz/faq/linux-unix-running-sudo-command-without-a-password/))

#### Prerequisites on Windows development system
The provided build scripts use several standard unix tools that have to be made 
available on Windows. Scripts use `ssh`, `scp`, `sed` and `unzip`.

The easiest way found to make these available is installing 
[Git tools](https://git-scm.com/download/win) 
and adding the Git `usr\bin` subdirectory to your path.
In a typical installation this is `C:\Program Files\Git\usr\bin`. 
(See e.g. [here](https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/))
 
#### Prerequisites on linux development system
Scripts should work out of the box

### Installing the Gradle Tasks
We are using a quite basic way to integrate cross development 
tasks into the Gradle build script `build.gradle`:
* Tasks and helper methods are coded in crossdev.gradle
* Site specific parameters are set in gradle.properties

The easiest way to use the cross development tasks is to apply `crossdev.gradle` from this 
project repository into your project. This is done by including the following line into
your `build.gradle` configuration file.

`
apply from: 'https://raw.githubusercontent.com/geobe/Java2PiWithIdea/master/crossdev.gradle'
`

Alternatively, you can also clone this repository,
test, if the simple RaspiHello program is running on your target and substitute 
the example specific code with your development.

### Make `distZip` gradle task available
Be sure that the gradle standard task `distZip` is available to your builds. 
It is available, if your plugins closure contains the application plugin, e.g.:  
``` groovy
 plugins {
     id 'application'
     id 'java'
 }
 ```  
 It is missing if only the java plugin is configured like this, so change it:  
 ``` groovy
 plugins {
      id 'java'
 }
  ```  
  The `distZip` task needs to know your main class to generate a shell script or 
  `.bat` file to start your program on the target. So make sure your `build.gradle` 
  defines a main class like in the example:  
  `mainClassName = 'de.geobe.java2pi.hello.HelloPigpio'
  ` 


### Configuration and Use
#### Project Specific Values in `gradle.properties`
You will have to set at least `remoteHome` and `keyfilepath` to your local requirements
``` shell script
# user@ip or user@url of remote machine*
remoteHome = pi@192.168.112.102
# path to ssh key file for target login 
# remember to use / as file separators
# on linux typically ~/.ssh/id_rsa
keyfilepath= d:/usr/geb/.ssh/id_rsa
# distribution path on target machine, will be created if not existing
targetpath = xdev/runjava
# output directory of build process
buildOutput = build/distributions
# set true for running with sudo on target machine, 
# any other value will run as normal user
runsudo = true
```
#### Cross Development Tasks in `crossdev.gradle`
The following tasks are provided:
1. **testRemoteLogin**  
   Login to target machine and show some identifying output on standard output.   
   Intended to check if target can be reached with given configuration. Remember to login
   using ssh at least once from the command line!
1. **distAll**   
   Distribute built java program and all needed libraries to the target: 
   * Create target path on target machine, if needed
   * Unzip the distribution zip file in the buildOutput directory
   * Remote copy all the unzipped files from buildOutput to targetpath on target machine
   * Edit the generated startup shell script to run java program as sudo on target
   * Make startup shell script executable
1. **distAllAndRunRemote**  
   Like distAll, but also run program on target. 
   The number shown on standard output is the process id (pid) of the remote program.
   You can use it to `sudo kill -9 pid` kill the program if it hangs.
   If developing a web application, it is very helpful to make it have a 
   shutdown menu (at least during development) because remote runs         
   (xxxRunRemote tasks) cannot be stopped from the development machine.
1. **distBuild**  
   Distribute only the jar file of latest build to the target, assuming that everything 
   else was already distributed before and has not changed 
   (i.e. no changed resources or added library jars). This task will only work if
   distAll had already been used before.
1. **distBuildAndRunRemote**  
   Distribute the latest build and run it, similar to distAllAndRunRemote.
