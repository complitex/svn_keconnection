package org.complitex.keconnection.heatmeter;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = "HeatmeterModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.keconnection.heatmeter";
}
