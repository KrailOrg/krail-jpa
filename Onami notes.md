##Changes to Onami##

Code imported at version 1.0.1 of Onami-persist

The PersistenceUnitModule modified to:
 
- incorporate the DAOs into the binding with annotations, so that the DAOs get the right EntityManagerProvider injected.
- support binding arbitrary interface / implementation pairs, again with annotation within the context of the PrivateModule

PersistenceConfiguration
 
- now carries the binding pairs which can then be bound in the PrivateModule 

Some changes to the visibility of certain elements were necessary to enable some of the changes


