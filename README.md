#krail-jpa

This is a module for use with [Krail](https://github.com/davidsowerby/krail), a rapid development framework based around Vaadin 7.

This module provides JPA capability.

Most of the functionality is provided by [Apache Onami Persist](https://onami.apache.org/persist/), but with some additional Vaadin specific capability.

##Features

* Support for multiple persistence units
* Support for JPAContainer configuration and creation in a Guice / Onami environment
* Assisted persistence unit configuration, in Guice modules (in addition to persistence.xml)

## Download

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail-jpa/images/download.svg) ](https://bintray.com/dsowerby/maven/krail-jpa/_latestVersion)

### From Central Repository

(not available in Maven Central, only JCenter)

#### Gradle:

```
repositories {
    jcenter()
}

'uk.q3c.krail:krail-jpa:0.8.0'
```
#### Maven :
```
<repository>
  <id>jcenter</id>
  <url>http://jcenter.bintray.com</url>
</repository>


<dependency>
   <groupId>uk.q3c.krail</groupId>
   <artifactId>krail-jpa</artifactId>
   <version>0.8.0</version>
</dependency>

```

