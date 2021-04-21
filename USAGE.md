# Usage

This guide provides basic guidance for interacting with API available in the Tackle Applicaiton Inventory REST endpoints.  

## `/application` endpoint

TBD  

## `/applications-dependency` endpoint

### Add a northbound dependency to an application
To add the application with id `<dependency_id>` as a northbound dependency for the application with id `<application_id>` do a `POST` call to the `/applications-dependency` resource with the payload:  

```json
{
    "from": {
        "id": <dependency_id>
    },
    "to": {
        "id": <application_id>
    }
}
```

### Add a southbound dependency to an application
To add the application with id `<dependency_id>` as a southbound dependency for the application with id `<application_id>` do a `POST` call to the `/applications-dependency` resource with the payload:

```json
{
    "from": {
        "id": <application_id>
    },
    "to": {
        "id": <dependency_id>
    }
}
```

### Retrieve northbound dependencies for an application
To retrieve the set of the northbound dependencies for an application with id `<application_id>` do a `GET` call to the `/applications-dependency` resource with the query parameter `to.id=<application_id>`.  

### Retrieve southbound dependencies for an application
To retrieve the set of the northbound dependencies for an application with id `<application_id>` do a `GET` call to the `/applications-dependency` resource with the query parameter `from.id=<application_id>`.  

### Delete a dependency
To delete the applications' dependency with id `<applications_dependency_id>` do a `DELETE` call to the `/applications-dependency/<applications_dependency_id>` endpoint.  

### Update a dependency
Updating a dependency doesn't make sense because it would mean changing at least one of the applications involved and that would represent a different dependency.  
For this reason the `PUT` HTTP verb on the `/applications-dependency` resource is not allowed.  

## `/review` endpoint

### Add a review for an application
To add a review for an application with id `<applications_id>`, do a `POST` call to the `/review` resource with the payload:

```json
{
    "proposedAction": "action",
    "effortEstimate": "effort",
    "businessCriticality": 5,
    "workPriority": 8,
    "comments": "comments",
    "application": {
        "id": <applications_id>
    }
}
```

### Retrieve a review
To retrieve the review with id `<review_id>` do a `GET` call to the `/review/<review_id>` endpoint.

### Update a review
To update the review with id `<review_id>` for an application with id `<applications_id>` do a `PUT` call to the `/review/<review_id>` endpoint with the payload:

```json
{
    "proposedAction": "update action",
    "effortEstimate": "update effort",
    "businessCriticality": 1,
    "workPriority": 10,
    "comments": "update comments",
    "application": {
        "id": <applications_id>
    }
}
```

### Delete a review
To delete the review with id `<review_id>` do a `DELETE` call to the `/review/<review_id>` endpoint.
