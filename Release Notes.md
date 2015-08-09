### Release Notes for krail-jpa 0.9.0

Simplification of integration with Krail core has caused a substantial number of changes.  The biggest apparent change is probably the use of annotations alone to identify sources.

#### Change log

-   [7](https://github.com/davidsowerby/krail-jpa/issues/7): java.lang.NoSuchMethodError: javax.persistence.EntityManager.createQuery
-   [12](https://github.com/davidsowerby/krail-jpa/issues/12): OptionDao_LongInt not updating correctly
-   [14](https://github.com/davidsowerby/krail-jpa/issues/14): Separate persistence for Pattern
-   [16](https://github.com/davidsowerby/krail-jpa/issues/16): refactor uk.q3c.krail.core.user.opt.jpa


#### Dependency changes

   compile dependency version changed to: krail:0.9.6

#### Detail

*Pre-release - update version descriptions and version properties*


---
*Option persistence*

Adapted for changes made under [krail 454](https://github.com/davidsowerby/krail/issues/454)
Improved persistence tests for Option


---
*Pre-merge of PatternDao and BundleWriter/Reader*


---
*See [krail 432](https://github.com/davidsowerby/krail/issues/432) Support for Export Keys*

 BaseJpaDao tableName() renamed entityName(), and corrected, as JPQL uses the entity name


---
*Corrected ```BaseJpaDao.tableName()``` to use name from @Entity and not @Table*


---
*Fix [16](https://github.com/davidsowerby/krail-jpa/issues/16) refactor*

Refactored all the packages to make them more consistent


---
*Fix [7](https://github.com/davidsowerby/krail-jpa/issues/7) Casting to EntityManagerImpl*

Not sure of original cause, but this is no longer a problem.  Workaround removed wherever it had been used


---
*Fix [14](https://github.com/davidsowerby/krail-jpa/issues/14) Pattern persistence separated from Option*

 Changed to principle of sub-classing ```DatabaseBundleReaderBase``` and ```DatabaseBundleWriterBase```, so that the sub-classes can be added to the bundle sources in ```18NModule```


---
*Fix [12](https://github.com/davidsowerby/krail-jpa/issues/12) OptionJpaDao updating correctly*

write method was not checking whether OptionCacheKey already exists.  Tests extended


---
*Separated OptionContainer from Pattern*

Data now loads correctly but issue with Jpa method of storing entity (davidsowerby/krail-jpa#12)


---
*OptionView shows active and selected options sources*

Renaming to DefaultOptionSource
OptionKey is no longer immutable, but there is a setter only for the default value.  This is useful for setting a common default for groups of options (see ```SourcePanel``` for an example)


---
*UnitOfWork restarted (when necessary) by EntityManagerProviderOnamiWrapper.*


---
*JpaView test fails intermittently with UnitOfWork not started*


---
*InMemoryContainer added.  ContainerType move to krail core from krail-jpa*


---
*VaadinContainerProvider returns DefaultJpaContainerProvider with correct EntityManagerProvider injected*


---
*Flexible DAO selection [krail 434](https://github.com/(davidsowerby/krail/issues/434) and davidsowerby/krail#435)*

Supporting the change to selection method.
Standardised use of Optional for the Option framework
Fixed the conversion of Option values to String (so only one column is used for the OptionEntity value)


---
*All DAOs now are annotated (for krail core the annotation is @CoreDao).  This makes it possible to configure any future persistence module to provide persistence for Krail core.    All persistence modules should now implement KrailPersistenceModule*

  The persistence elements of Option and I18N have been taken out of their respective Guice modules, and placed in a separate InMemoryModule - the latter now represents the equivalent of a persistence module and is therefore readily interchangeable.

  A lot of tests were changed to incorporate TestPersistenceModule (which is just InMemoryModule with some pre-configuration)


---
*Refactoring*

JpaInstanceConfiguration ```addPrivateBinding()``` changed to ```bind()```
```StandardEntity``` changed to ```EntityBase_LongInt```


---
*Persistence and JPA  improvements*

Some small changes in Krail core to make Dao structure simpler
Major changes in krail-jpa to provide a single, simplified, transaction-managed generic Dao (```BaseJpaDao```).  krail-jpa also has a simplified API for instance configuration.  This includes removing BlockDao and StatementDao which were not very useful in practice.
Generic Dao can be instantiated for any persistence unit, using annotation identification.
The test-app  adds tests for Dao providers to ensure that bindings are operating correctly


---
*JpaService / DefaultJpaService wrap the PersistenceService - but not yet in use.*

The PersistenceFilter also needs to be amended for this change


---
