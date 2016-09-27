package com.ppi.api.service;

import com.ppi.api.model.*;
import com.ppi.api.security.DataFilter;
import com.ppi.api.security.Secured;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import java.util.Set;

/**
 * UserService
 *
 * @author jcarreira@gmail.com
 * @version 1.0
 */
@Component
@Singleton
@Path("/participants")
@Produces("application/json")
@Consumes("application/json")
@DataFilter(Participant.class)
public class ParticipantService extends BaseService<Participant> {
    @Autowired
    PortfolioInstrumentService portfolioInstrumentService;

    public ParticipantService() {
        super(Participant.MAP_NAME);
    }

    @Override
    public void doCreate(Participant entity) {
        super.doCreate(entity);
        Set<Portfolio> portfolios = entity.getPortfolios();
        for (Portfolio portfolio : portfolios) {
            Set<PortfolioInstrument> instruments = portfolio.getInstruments();
            for (PortfolioInstrument instrument : instruments) {
                portfolioInstrumentService.doCreate(instrument);
            }
        }
    }

    @POST
    @Path("{participantId}/addPortfolio")
    @Secured({Role.COMPANY_ADMIN, Role.NESTUP_ADMIN})
    public Portfolio addPortfolio(@Context ContainerRequestContext context, @PathParam("participantId") String participantId, Portfolio entity) {
        if (entity != null) {
            Participant participant = getOne(context, participantId);
            if (participant != null) {
                participant.addPortfolio(entity);
                doUpdate(context,participantId,participant);
                entity.setHazelcastInstance(hazelcastInstance);
                return entity;
            }
        }
        return null;
    }
}
