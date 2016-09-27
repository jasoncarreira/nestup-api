package com.ppi.api.service;

import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.ppi.api.model.PortfolioInstrument;
import com.ppi.api.model.Role;
import com.ppi.api.model.User;
import com.ppi.api.security.DataFilter;
import com.ppi.api.security.Secured;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.Collection;
import java.util.HashSet;

import static javafx.scene.input.KeyCode.T;

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
@DataFilter(PortfolioInstrument.class)
public class PortfolioInstrumentService extends BaseService<PortfolioInstrument> {
    public PortfolioInstrumentService() {
        super(PortfolioInstrument.MAP_NAME);
    }



    @GET
    @Path("byPortfolio/{portfolioId}")
    @Secured({Role.NESTUP_ADMIN, Role.AUTHENTICATED_USER, Role.COMPANY_ADMIN})
    public Collection<PortfolioInstrument> getAllForPortfolio(@Context ContainerRequestContext context, @PathParam("portfolioId") String portfolioId) {
        PredicateBuilder predicateBuilder = getPredicate(context);
        EntryObject e = new PredicateBuilder().getEntryObject();
        Predicate portfolioPredicate = e.get( "portfolioId" ).equal( portfolioId );
        Predicate predicate = (predicateBuilder == null) ? portfolioPredicate : predicateBuilder.and(portfolioPredicate) ;
        Collection<PortfolioInstrument> values = getMap().values(predicate);
        values.forEach((org) -> org.setHazelcastInstance(this.hazelcastInstance));
        return values;
    }
}
