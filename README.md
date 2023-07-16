# Donnez-Lui Gloire
An SDA app that I developed a while ago. 
Bitbucket deleted the repo and all the related CI setup hence moving this to GitHub

## How to run locally
### Pre-reqs
- Install jdk 11. Make sure that version is set as environment variable
```
/usr/libexec/java_home -V 
export JAVA_HOME=`/usr/libexec/java_home -v 11.X
```

### Build
- Run build command
```
./gradlew clean build
```
- See `build.gradle` for how how the build is working and what type of artifact is generated
