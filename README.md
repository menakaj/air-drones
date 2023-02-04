# AirDrones
Drone based delivery service

This system allows to register your drone, add items to the drone and dispatch it to a location. 

## Assumptions
1. Once delivered the items will be removed from the system.
2. Location is set by the system internally
3. The drone's battery level will not degrade when it's idle. But discharges if it's on move.

## Usage

### Build the application

Run

`mvn clean install`

### Running the application

Run,

`java -jar target/drone-delivery-1.0.0.jar`

### Register drone

Registers a drone in the system. Drones must be one of the following types.
`LIGHT_WEIGHT, HEAVY_WEIGHT, CRUISE_WEIGHT, MIDDLE_WEIGHT`

Request

```
curl -X POST http://localhost:8080/dispatcher/v1/drones/register \
-H 'Content-type: application/json' \
-d '{
"model": "HEAVY_WEIGHT",
"batteryCapacity": 75,
"serialNumber": "NjJkZTNlN2ItMDg14LWJiZTYtNjJkZT"
}'
```

Response

```json
{
    "id": 1,
    "serialNumber": "NjJkZTNlN2ItMDg14LWJiZTYtNjJkZT",
    "model": "HEAVY_WEIGHT",
    "state": "IDLE",
    "batteryCapacity": 75.0,
    "items": [],
    "maxWeight": 500.0,
    "available": true,
    "_links": {
        "self": {
            "href": "http://localhost:8080/dispatcher/v1/drones/1"
        },
        "items": {
            "href": "http://localhost:8080/dispatcher/v1/drones/1/items"
        }
    }
}
```

### List available drones

Request

```
curl -X GET http://localhost:8080/dispatcher/v1/drones?available=true
```

Response

```json
{
    "_embedded": {
        "droneList": [
            {
                "id": 2,
                "serialNumber": "ZAAZSJFU_DESF4",
                "model": "MIDDLE_WEIGHT",
                "state": "IDLE",
                "batteryCapacity": 100.0,
                "items": [],
                "maxWeight": 300.0,
                "available": true,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/dispatcher/v1/drones/2"
                    },
                    "items": {
                        "href": "http://localhost:8080/dispatcher/v1/drones/2/items"
                    }
                }
            },
            {
                "id": 3,
                "serialNumber": "SSDFFFU_DHJ234",
                "model": "LIGHT_WEIGHT",
                "state": "IDLE",
                "batteryCapacity": 80.3,
                "items": [],
                "maxWeight": 200.0,
                "available": true,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/dispatcher/v1/drones/3"
                    },
                    "items": {
                        "href": "http://localhost:8080/dispatcher/v1/drones/3/items"
                    }
                }
            },
            {
                "id": 4,
                "serialNumber": "NjJkZTNlN2ItMDg14LWJiZTYtNjJkZT",
                "model": "LIGHT_WEIGHT",
                "state": "IDLE",
                "batteryCapacity": 63.0,
                "items": [],
                "maxWeight": 200.0,
                "available": true,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/dispatcher/v1/drones/4"
                    },
                    "items": {
                        "href": "http://localhost:8080/dispatcher/v1/drones/4/items"
                    }
                }
            }
        ]
    },
    "_links": {
        "self": {
            "href": "http://localhost:8080/dispatcher/v1/drones?available=true"
        }
    }
}
```

### Add items to a drone

Request:

```
curl -X POST http://localhost:8080/dispatcher/v1/drones/4/items \
-H 'Content-Type: application/json' \
-d '[{
    "name": "Sugarwww",
    "code": "EF_999",
    "weight": 75,
    "image": "http://abc.jpg"
},
{
    "name": "Sugaqqqr",
    "code": "EF_999",
    "weight": 37,
    "image": "http://abc.jpg"
}]'
```

Response
```json
{
    "id": 4,
    "serialNumber": "NjJkZTNlN2ItMDg14LWJiZTYtNjJkZT",
    "model": "HEAVY_WEIGHT",
    "state": "LOADED",
    "batteryCapacity": 75.0,
    "items": [
        {
            "id": 4,
            "name": "Sugaqqqr",
            "code": "EF_999",
            "weight": 37.0,
            "image": "http://abc.jpg"
        },
        {
            "id": 3,
            "name": "Sugarwww",
            "code": "EF_999",
            "weight": 75.0,
            "image": "http://abc.jpg"
        }
    ],
    "maxWeight": 500.0,
    "available": true,
    "_links": {
        "self": {
            "href": "http://localhost:8080/dispatcher/v1/drones/4"
        },
        "items": {
            "href": "http://localhost:8080/dispatcher/v1/drones/4/items"
        }
    }
}
```
### Dispatch drone

Request:
```
curl -X POST http://localhost:8080/dispatcher/v1/drones/4/dispatch
```

Response:
```json
{
    "id": 4,
    "serialNumber": "NjJkZTNlN2ItMDg14LWJiZTYtNjJkZT",
    "model": "HEAVY_WEIGHT",
    "state": "DELIVERING",
    "batteryCapacity": 64.0,
    "items": [
        {
            "id": 3,
            "name": "Sugarwww",
            "code": "EF_999",
            "weight": 75.0,
            "image": "http://abc.jpg"
        },
        {
            "id": 4,
            "name": "Sugaqqqr",
            "code": "EF_999",
            "weight": 37.0,
            "image": "http://abc.jpg"
        }
    ],
    "maxWeight": 500.0,
    "available": false
}
```

### Check drone's loaded items
Request

```
curl -X GET http://localhost:8080/dispatcher/v1/drones/4/items
```
Response 

```json
[
    {
        "id": 5,
        "name": "Sugarwww",
        "code": "EF_999",
        "weight": 75.0,
        "image": "http://abc.jpg"
    },
    {
        "id": 6,
        "name": "Sugaqqqr",
        "code": "EF_999",
        "weight": 37.0,
        "image": "http://abc.jpg"
    }
]
```
### Check drone's battery level


Request

```
curl -X GET http://localhost:8080/dispatcher/v1/drones/4/battery
```
Response 

```json
{"battery": "75.0%"}
```