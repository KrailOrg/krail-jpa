##Changes to Onami##

Code imported at version 1.0.1 of [Onami-persist](http://mvnrepository.com/artifact/org.apache.onami/org.apache.onami.persist)

The PersistenceUnitModule modified to:
 
- incorporate the DAOs into the binding with annotations, so that the DAOs get the right EntityManagerProvider injected.
- support binding arbitrary interface / implementation pairs, again with annotation within the context of the PrivateModule

PersistenceUnitModuleConfiguration
 
- now carries the binding pairs which can then be bound in the PrivateModule 


Some changes to the visibility of certain elements were necessary to enable some of the changes


