### Release Notes for krail-jpa 0.8.7

Included onami-persist in the source, so that modifications can be made to it.  DAOs are now created using the same Private Modules as Onami, thus enabling the use of DAOs with Binding Annotations

#### Change log

-   [1](https://github.com/davidsowerby/krail-jpa/issues/1): Persistence for UserOption
-   [2](https://github.com/davidsowerby/krail-jpa/issues/2): Persistence for I18N
-   [5](https://github.com/davidsowerby/krail-jpa/issues/5): DAO implementations for multiple persistent units


#### Dependency changes

   compile dependency version changed to: krail:0.9.2

#### Detail

*Updated version information*


---
*Fix [5](https://github.com/davidsowerby/krail-jpa/issues/5) Support for multiple persistence units and DAOs*
Apache Onami persist already provides support for multiple persistence units, but makes it difficult to provide generic DAOs linked to the annotations which identify the different PUs.  This is because the binding to annotations is done in a PrivateModule.  The Onami notes.md file provides a brief description of the changes made to the original code.
The implementations provided by this change enable the use of multiple PUs, and provide generic DAOs to support them.  The base of an entity specific DAO is also provided


---
*Fix [1](https://github.com/davidsowerby/krail-jpa/issues/1) Implementation of DAOs and persistence for Option*
onami-persist uses Guice Private Modules to enable the use of multiple persistence units.  However, many of its classes are inaccessible for modification, and this prevented binding DAOs through the same mechanism (which would be required of course to ensure that DAOs were pointing at the correct source).  This would not be an issue for Entity-specific DAOs, but precluded the use of generic DOAs.  Onami-persist has therefore been imported as source and modified slightly.  (see Onami notes.md for some detail).
Option is now persistable
