package org.complitex.keconnection.importing;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "KeConnectionImportModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.keconnection.importing";
}
