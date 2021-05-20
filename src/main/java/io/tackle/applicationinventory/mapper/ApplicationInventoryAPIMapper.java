package io.tackle.applicationinventory.mapper;

import io.tackle.applicationinventory.entities.ApplicationImport;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;


public class ApplicationInventoryAPIMapper extends ApplicationMapper{
    @Override
    public Response map(ApplicationImport importApp)
    {
        System.out.println("Call to Mapper");

        return Response.ok().build();
    }
}
