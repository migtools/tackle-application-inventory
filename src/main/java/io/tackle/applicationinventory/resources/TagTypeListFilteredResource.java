package io.tackle.applicationinventory.resources;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.tackle.commons.resources.ListFilteredResource;
import io.tackle.commons.resources.query.Query;
import io.tackle.commons.resources.query.QueryBuilder;
import io.tackle.applicationinventory.entities.Tag;
import io.tackle.applicationinventory.entities.TagType;
import org.jboss.resteasy.links.LinkResource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

@Path("tag-type")
public class TagTypeListFilteredResource implements ListFilteredResource<TagType> {

    @Override
    public Class<TagType> getPanacheEntityType() {
        return TagType.class;
    }

    @GET
    @Path("")
    @Produces({"application/json"})
    @LinkResource(
            entityClassName = "io.tackle.applicationinventory.entities.TagType",
            rel = "list"
    )
    public Response list(@QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) List var1,
                         @QueryParam(QUERY_PARAM_PAGE) @DefaultValue(DEFAULT_VALUE_PAGE) int var2,
                         @QueryParam(QUERY_PARAM_SIZE) @DefaultValue(DEFAULT_VALUE_SIZE) int var3,
                         @Context UriInfo var4) throws Exception {
        return ListFilteredResource.super.list(var1, var2, var3, var4, false);
    }

    @Path("")
    @GET
    @Produces({"application/hal+json"})
    public Response listHal(@QueryParam(QUERY_PARAM_SORT) @DefaultValue(DEFAULT_VALUE_SORT) List var1,
                            @QueryParam(QUERY_PARAM_PAGE) @DefaultValue(DEFAULT_VALUE_PAGE) int var2,
                            @QueryParam(QUERY_PARAM_SIZE) @DefaultValue(DEFAULT_VALUE_SIZE) int var3,
                            @Context UriInfo var4) throws Exception {
        return ListFilteredResource.super.list(var1, var2, var3, var4, true);
    }

    @Override
    public List list(Page page, Sort sort, Query query) throws Exception {
        // when filter by tag name is requested
        final String tagsNameFilterName = "tags.name";
        final Map<String, List<String>> rawQueryParams = query.getRawQueryParams();
        if (rawQueryParams != null && rawQueryParams.containsKey(tagsNameFilterName)) {
            // it means the filter applies to select the "root" resource (i.e. tag-type)
            // and also to filter the set of tags returned within each with tag-type to ensure only the tags that match
            // the tag name filter are returned. To achieve this:

            // 1. select the tags by tag name query parameters
            final MultivaluedMap<String, String> tagsQueryParameters = new MultivaluedHashMap<>();
            final List<String> tagNameFilterValues = rawQueryParams.get(tagsNameFilterName);
            tagNameFilterValues.forEach(tagNameFilterValue -> tagsQueryParameters.add("name", tagNameFilterValue));
            final Query tagsQuery = QueryBuilder.withPanacheEntity(Tag.class).andMultivaluedMap(tagsQueryParameters).build();
            List<Tag> tags = Tag.find(tagsQuery.getQuery(), tagsQuery.getQueryParameters()).list();

            // 2. select the related tag-type's IDs
            final MultivaluedMap<String, String> tagTypesQueryParameters = new MultivaluedHashMap<>();
            tags.stream()
                    .map(tag -> tag.tagType.id)
                    .collect(Collectors.toUnmodifiableSet())
                    .forEach(id -> tagTypesQueryParameters.add("id", Long.toString(id)));
            // also adding the remaining filter parameters to guarantee they match
            rawQueryParams.entrySet().stream()
                    .filter(entry -> !entry.getKey().equals(tagsNameFilterName))
                    .forEach(entry -> tagTypesQueryParameters.addAll(entry.getKey(), entry.getValue()));

            // 3. load the TagType entities for the response
            final Query tagTypesQuery = QueryBuilder.withPanacheEntity(TagType.class).andMultivaluedMap(tagTypesQueryParameters).build();
            final List<TagType> tagTypes = TagType.find(tagTypesQuery.getQuery(), sort, tagTypesQuery.getQueryParameters()).page(page).list();

            // 4. replace the Tag entities loaded from DB with the one loaded in step #1 filtering by tag name
            final Map<Long, List<Tag>> tagsByTagTypeId = new HashMap<>();
            tags.forEach(tag -> tagsByTagTypeId.computeIfAbsent(tag.tagType.id, k -> new ArrayList<>()).add(tag));
            return tagTypes.stream()
                    // remove the TagType entities without an associated valid Tag entity
                    .filter(tagType -> tagsByTagTypeId.get(tagType.id) != null)
                    .peek(tagType -> {
                        // clear the list of tags loaded from DB because they're not filtered by tag name
                        tagType.tags.clear();
                        // and replace them with tag coming from step #1 above
                        tagType.tags.addAll(tagsByTagTypeId.getOrDefault(tagType.id, Collections.emptyList()));
                    })
                    .collect(Collectors.toList());
        }
        // otherwise the default implementation can be used
        else {
            return ListFilteredResource.super.list(page, sort, query);
        }
    }
}
