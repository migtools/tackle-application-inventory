package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.entities.ApplicationImport;

import javax.ws.rs.core.Response;

public abstract class ApplicationMapper {
    public abstract Response map(ApplicationImport importApp);
}
