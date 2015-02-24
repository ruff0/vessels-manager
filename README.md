
**Table of Contents** 

- [Vessels Manager](#vessels-manager)
  - [Services specification](#services-specification)
    - [List all vessels](#list-all-vessels)
    - [Create a new vessel](#create-a-new-vessel)
    - [Edit an existing vessel](#edit-an-existing-vessel)
    - [Delete a vessel](#delete-a-vessel)
  - [Wireframes](#wireframes)
    - [List all vessels](#list-all-vessels-1)
    - [Create new Vessel](#create-new-vessel)
    - [Edit a Vessel](#edit-a-vessel)
    - [Delete a Vessel](#delete-a-vessel)



# Vessels Manager

This applications is a basic CRUD to manage vessels records. As a CRUD application his features are:

* List all the vessels
* Create a new vessel
* Edit an existing vessel
* Delete a vessel

The vessel definition in terms of attribute is:

* Name
* Width in meters
* Length in meters
* Draft in meters
* Last coordinates it was seen at

Technologies that use the Manager:

* Scala
* Play Framework 2
* AngularJS
* Mongo with the Reactive Mongo library

Practices applied to the project:

* TDD
* git-flow

## Services specification

The services follows the REST architecture.

### List all vessels
```
GET /vessels retrieves all the vessels

Optional parameters:
- page | set the page number to retrieve
- max | set the maximum of records per page
- filter | filter the result using vessel's name as criteria

Expected response example:

{
	"page": 1,
	"next_page": null,
	"total_records": 2,
	"result": [
		{
			"_id": 1,
			"name": "Vessel 1",
			"width": 25,
			"length": 226,
			"draft": 8,
			"lastCoordinate": {
				"latitude": 53.323473,
				"longitude": -6.173601
			}
		},{
            "_id": 2,
            "name": "Vessel 2",
            "width": 25,
            "length": 226,
            "draft": 8,
            "lastCoordinate": {
                    "latitude": 53.643649,
                    "longitude": -3.148441
            }
        }
	]
}
```
### Create a new vessel
```
POST /vessels receives a JSON Object with vessel's attributes and persists it

Example body request:

{
	"name": "Vessel 3",
	"width": 25,
	"length": 226,
	"draft": 8,
	"lastCoordinate": {
        	"latitude": 53.643649,
            "longitude": -3.148441
        }
}

Possible responses:

201 The vessel has been created
422 There are missing or invalid data
```

### Edit an existing vessel
```
PUT /vessels/:id receives a vessel's JSON Object and updates the proper one which has the indicated id

Example body request:

{
    "name": "Vessel 3",
    "width": 25,
    "length": 226,
    "draft": 8,
    "lastCoordinate": {
            "latitude": 53.643649,
            "longitude": -6.543478
        }
}

Possible responses:

200 The vessel has been updated
    {
        "_id": 3
        "name": "Vessel 3",
        "width": 25,
        "length": 226,
        "draft": 8,
        "lastCoordinate": {
                "latitude": 53.643649,
                "longitude": -6.543478
            }
    }

422 There are missing or invalid data
404 The vessel that you are looking for not exists
```

### Delete a vessel
```
DELETE /vessels/:id deletes the indicated vessel

Posible responses:
204 The vessel has been deleted
404 The vessel that you are looking for not exists
```

## Wireframes
### List all vessels
![List](https://dl.dropboxusercontent.com/u/228377/manager/list-vessel.png)

### Create new Vessel
![Create](https://dl.dropboxusercontent.com/u/228377/manager/create-vessel.png)

### Edit a Vessel
![Edit](https://dl.dropboxusercontent.com/u/228377/manager/edit-vessel.png)

### Delete a Vessel
![Delete](https://dl.dropboxusercontent.com/u/228377/manager/delete-vessel.png)

