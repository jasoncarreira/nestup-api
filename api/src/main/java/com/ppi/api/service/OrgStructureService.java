package com.ppi.api.service;

import com.hazelcast.core.*;
import com.hazelcast.map.listener.*;
import com.ppi.api.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

@Component
public class OrgStructureService implements MapListener, EntryAddedListener<String, Organization>, EntryRemovedListener<String, Organization>,
        EntryUpdatedListener<String, Organization>, MapClearedListener {

    public static final String ORG_STRUCTURE_MAP = "org-structure";

    private String listenerRegistration;

    @Autowired
    HazelcastInstance hazelcastInstance;

    @Autowired
    OrganizationService organizationService;

    @PostConstruct
    public void init() {
        IMap<String, Organization> organizationMap = organizationService.getMap();
        listenerRegistration = organizationMap.addLocalEntryListener(this);
        MultiMap<String, String> orgStructureMap = getOrgStructureMap();
        if (orgStructureMap.keySet().size() == 0) {
            Collection<Organization> all = organizationService.getAll(null);
            Set<String> visited = new HashSet<>();
            for (Organization next : all) {
                if (!visited.contains(next.getId())) {
                    while (next != null && next.getParentId() != null && !visited.contains(next.getParentId())) {
                        next = next.getParentOrganization();
                    }
                    recurseReferences(visited, next);
                }
            }
        }
    }

    private void recurseReferences(Set<String> visited, Organization org) {
        addAllReferences(org);
        visited.add(org.getId());
        Set<Organization> childOrganizations = org.getChildOrganizations();
        for (Organization child : childOrganizations) {
            recurseReferences(visited, child);
        }
    }

    @PreDestroy
    public void cleanup() {
        getOrganizationMap().removeEntryListener(listenerRegistration);
    }

    private MultiMap<String, String> getOrgStructureMap() {
        return hazelcastInstance.getMultiMap(ORG_STRUCTURE_MAP);
    }

    private IMap<String, Organization> getOrganizationMap() {
        return organizationService.getMap();
    }

    public Set<String> getAllReachableOrgs(String organizationId) {
        return new HashSet<>(getOrgStructureMap().get(organizationId));
    }

    /**
     * Invoked upon addition of an entry.
     *
     * @param event the event invoked when an entry is added
     */
    @Override
    public void entryAdded(EntryEvent<String, Organization> event) {
        Organization organization = event.getValue();
        addAllReferences(organization);
    }

    /**
     * Invoked upon removal of an entry.
     *
     * @param event the event invoked when an entry is removed
     */
    @Override
    public void entryRemoved(EntryEvent<String, Organization> event) {
        Organization organization = event.getValue();
        removeAllReferences(organization);
    }

    /**
     * Invoked upon update of an entry.
     *
     * @param event the event invoked when an entry is updated
     */
    @Override
    public void entryUpdated(EntryEvent<String, Organization> event) {
        Organization oldValue = event.getOldValue();
        Organization newValue = event.getMergingValue();
        if (!Objects.equals(oldValue.getParentId(), newValue.getParentId())) {
            removeAllReferences(oldValue);
            addAllReferences(newValue);
        }
    }

    /**
     * Invoked when all entries are removed by {@link IMap#clear()}.
     *
     * @param event the map event invoked when all entries are removed by {@link IMap#clear()}
     */
    @Override
    public void mapCleared(MapEvent event) {
        getOrgStructureMap().clear();
    }

    private void addAllReferences(Organization organization) {
        MultiMap<String, String> orgStructureMap = getOrgStructureMap();
        String parentId = organization.getParentId();
        if (parentId != null) {
            Set<Map.Entry<String, String>> entries = orgStructureMap.entrySet();
            for (Map.Entry<String, String> next : entries) {
                if (next.getValue().equals(parentId)) {
                    orgStructureMap.put(next.getKey(),organization.getId());
                }
            }
        }
        orgStructureMap.put(organization.getId(), organization.getId());
    }



    private void removeAllReferences(Organization organization) {
        MultiMap<String, String> orgStructureMap = getOrgStructureMap();
        Set<String> keys = orgStructureMap.keySet();
        for (String key : keys) {
            orgStructureMap.remove(key, organization.getId());
        }
    }
}
