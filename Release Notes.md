### Release Notes for krail-jpa 0.9.2

Build file renamed and a single minor fix

#### Change log

-   [18](https://github.com/davidsowerby/krail-jpa/issues/18):  query is potentially vulnerable SQL/JPQL injection
-   [19](https://github.com/davidsowerby/krail-jpa/issues/19): Use the same OptionDao test as Krail core
-   [20](https://github.com/davidsowerby/krail-jpa/issues/20): UnitOfWork not started errors


#### Dependency changes

   compile dependency version changed to: krail:0.9.8

#### Detail

*Release notes and version.properties generated*


---
*Version files updated*


---
*#Fix 22 Cast solves compile error*


---
*[krail-master 36](https://github.com/davidsowerby/krail-master/issues/36) rename build file*

reverted to 'build.gradle' to enable Travis


---
*[krail 499](https://github.com/davidsowerby/krail/issues/499) krail-testUtil merged back into core*


---
*[krail 509](https://github.com/davidsowerby/krail/issues/509) Reduced duplication of Option handling*

JpaOptionDao is now JpaOptionDao delegate.  This removes duplicated logic, and also enables OptionDao tests to be inherited.


---
*See [krail 503](https://github.com/davidsowerby/krail/issues/503) OptionDao updated to encompass OptionList conversion*

OptionDaoTest inherits from core to ensure consistent tests


---
*Fix [20](https://github.com/davidsowerby/krail-jpa/issues/20) Missing @Transactionals*

Causing UnitOfWork to report that it wasn't started


---
*Fix [19](https://github.com/davidsowerby/krail-jpa/issues/19) OptionDaoTest sub-class of core*

Tests for InMemoryOptionDao and JpaOptionDao now identical.
Refactoring changes in the core, see [krail 460](https://github.com/davidsowerby/krail/issues/460)


---
*Fix [508](https://github.com/davidsowerby/krail-jpa/issues/508) restructured packages*


---
*[krail 507](https://github.com/davidsowerby/krail/issues/507) OptionCacheKey is generic*


---
*Fix [18](https://github.com/davidsowerby/krail-jpa/issues/18) SQLInjection vulnerability cleared*

Pattern and Option DAOs re-written to a common key-value base class.  Using composite primary key in place of surrogate key.


---
*[krail 340](https://github.com/davidsowerby/krail/issues/340) FindBugs analysis complete*

SQL injection still to fix under [18](https://github.com/davidsowerby/krail-jpa/issues/18)


---
*Changes to Service related to [krail 244](https://github.com/davidsowerby/krail/issues/244)*


---
*using MockService based on AbstractService, Executor tests green*


---
