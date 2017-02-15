# ALTERnatives

ALTERnatives is a set of Java classes managing the loading of native JNI 
libraries into the JVM. The binaries can have dependencies and no specific 
settings are required to start the program. Maven support is included for easy
use.

Copyright [Sébastien Aupetit](mailto:sebtic@projectsforge.org?subject=HMMTK4j)

# Maven repository

Current artifacts are available through a simple repository. To use the artifacts in a Maven project, you need to add the following to your pom.xml :
```xml
<repositories>
  <repository>
   <id>projectsforge-repository</id>
   <name>Projecsforge.org Public Maven repository</name>
   <url>https://static.projectsforge.org/maven/</url>
   <snapshots>
    <updatePolicy>always</updatePolicy>
   </snapshots>
  </repository>
 </repositories>
```

# Warning

This code is no more actively maintained and is provided AS-IS.


