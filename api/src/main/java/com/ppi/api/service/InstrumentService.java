package com.ppi.api.service;

import com.ppi.api.model.Instrument;
import com.ppi.api.security.DataFilter;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * UserService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Component
@Singleton
@Path("/portfolio-instruments")
@Produces("application/json")
@Consumes("application/json")
@DataFilter(Instrument.class)
public class InstrumentService extends BaseService<Instrument> {
    public InstrumentService() {
        super(Instrument.MAP_NAME);
    }
}
