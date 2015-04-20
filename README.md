#krail-jpa

This is a module for use with [Krail](https://github.com/davidsowerby/krail), a rapid development framework based around Vaadin 7.

This module provides JPA capability.

Most of the functionality is provided by [Apache Onami Persist](https://onami.apache.org/persist/), but with some additional Vaadin specific capability, and Krail specific generic DAOs.

In order to provide the DAOs, the onami-persist source is included and slightly modified. 

##Features

* Support for multiple persistence units
* Support for JPAContainer configuration and creation in a Guice / Onami environment
* Assisted persistence unit configuration, in Guice modules (in addition to persistence.xml)
* Generic DAOs, for single and multiple statements within a transaction
* Base DAOs for Krail I18N and Option

#Download
<a href='https://bintray.com/dsowerby/maven/krail-jpa/view?source=watch' alt='Get automatic notifications about new "krail-jpa" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>
##Gradle

```
repositories {
	jcenter()
}
```

```
'uk.q3c.krail:krail-jpa:0.8.7
```
##Maven

```
<repository>
	<id>jcenter</id>
	<url>http://jcenter.bintray.com</url>
</repository>

```

```
<dependency>
	<groupId>uk.q3c.krail</groupId>
	<artifactId>krail-jpa</artifactId>
	<version>0.8.7</version>
</dependency>
```
##Direct

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/krail-jpa/images/download.svg) ](https://bintray.com/dsowerby/maven/krail-jpa/_latestVersion)

