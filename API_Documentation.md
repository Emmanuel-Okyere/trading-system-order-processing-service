## Table of Contents
- [Registration](#registration)
- [Authentication - Login to get Token](#login)
- [Registration](#create-order)

- 
<a name="registration"></a>
- ## Registration

The register API will accept user credentials:
username,email and password and saves it to the database.

### Request Information

| Type | URL                   |
|------|-----------------------|
| POST | /api/v1/auth/register |

### Header

| Type         | Property name    |
|--------------|------------------|
| Allow        | POST, OPTIONS    |
| Content-Type | application/json |
| Vary         | Accept           |

### JSON Body

| Property Name | type   | required | Description                    |
|---------------|--------|----------|--------------------------------|
| name          | String | true     | The name of the user           |
| email_address | String | true     | email address of the user      |
| password      | String | true     | Password for the user account  |
| role          | List   | false    | Role(s) of the user logging in |

### Error Responses

| Code | Message                        |
|------|--------------------------------|
| 400  | "email already taken"          |
| 400  | "This field may not be blank." |


### Successful Response Example

```
{
    "message": "user created successfully",
    "status": "00",
    "data": {
        "id": 5,
        "name": "Group7 ",
        "email": "group7trials1@gmail.com",
        "balance": 0.0
    },
}
```
<a name="login"></a>

## Authentication - Login

This Api endpoint accepts user's email and password
and authenticates the user and return a token.
The token can be used by the user to authenticate their
identity.

**Note** if you are already authenticated and trying to send a new login request, you won't be allowed.

### Request Information

| Type | URL                |
|------|--------------------|
| POST | /api/v1/auth/login |

### Header

| Type         | Property name    |
|--------------|------------------|
| Allow        | POST, OPTIONS    |
| Content-Type | application/json |
| Vary         | Accept           |


### JSON Body

| Property Name | type   | required | Description             |
|---------------|--------|----------|-------------------------|
| email_address | String | true     | email address for login |
| password      | String | true     | password for login      |

### Error Responses

| Code | Message                                |
|------|----------------------------------------|
| 400  | email_address: This field is required. |
| 400  | password: This field is required.      |
| 401  | Bad credentials                        |


### Successful Response Example

```
{
    "status": "00",
    "message": "login successful",
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJneWF0ZW5nQGdtYWlsLmNvbSIsImV4cCI6MTY3MDA3NjEzNiwiaWF0IjoxNjY5OTg5NzM2fQ.OxhNHz--y3ZB-5jIk8j33U3xI7B4v9aAn1ACrcpzApx3noAJXXZpr4Jbujl11ZqCdxGtLjvHynvyiIv7-BP2Zw"
    "data": {
        "id": 1,
        "name": "Emmanuel Gyateng ",
        "email": "gyateng@gmail.com",
        "balance": 0.0,
        "status": "00",
        "roles": [
            "ADMIN"
        ]
    }
    }
```

<a name="create-order"></a>
- ## Registration

The register API will accept user credentials:
product,quantity, price,side and type  and saves it to the database.
**Note** Only authenticated users can access this endpoint
### Request Information

| Type | URL                |
|------|--------------------|
| POST | /api/v1/auth/order |
| GET  | /api/v1/auth/order |

### Header

| Type         | Property name      |
|--------------|--------------------|
| Allow        | POST, GET, OPTIONS |
| Content-Type | application/json   |
| Vary         | Accept             |

### JSON Body

| Property Name | type    | required | Description                                      |
|---------------|---------|----------|--------------------------------------------------|
| product       | String  | true     | The name of the ticker                           |
| quantity      | Integer | true     | the quantity a user wants                        |
| price         | Double  | true     | the price the user wants to buy or sell for      |
| side          | String  | true     | whether the user is selling or buying            |
| type          | String  | true     | The type of marl=key that the user want to trade |

### Error Responses

| Code | Message                        |
|------|--------------------------------|
| 400  | "This field may not be blank." |


### Successful Response Example

```
{
    "message": "order created successfully",
    "status": "00",
    "data": {
        "status": "00",
        "message": "order created successfully",
        "id": 4,
        "orderId": "a34a3e68-d9ff-4bcf-8bca-8ae67a12529b",
        "quantity": 30,
        "product": "GOOGL",
        "price": 1.0,
        "type": "MARKET"
    }
}
```
### GET Successful Response Example
This gets the order made by a particular user.
**Note** Only authenticated users can access this endpoint
```
{
    "status": "00",
    "message": "order fetch successful",
    "data": [
        {
            "product": "GOOGL",
            "quantity": 30,
            "price": 1.0,
            "type": "MARKET",
            "side": "BUY",
            "orderId": "7369c9de-e8df-4f32-b45f-7168fb345888",
            "createdAt": "2022-12-02T10:20:13.056+00:00",
            "updatedAt": "2022-12-02T10:20:13.056+00:00",
            "id": 1
        },
        {
            "product": "GOOGL",
            "quantity": 30,
            "price": 1.0,
            "type": "MARKET",
            "side": "BUY",
            "orderId": "c4424608-aaf7-4abd-97e4-18bd399766a6",
            "createdAt": "2022-12-02T11:02:15.848+00:00",
            "updatedAt": "2022-12-02T11:02:15.848+00:00",
            "id": 2
        },
        {
            "product": "GOOGL",
            "quantity": 30,
            "price": 1.0,
            "type": "MARKET",
            "side": "BUY",
            "orderId": "a34a3e68-d9ff-4bcf-8bca-8ae67a12529b",
            "createdAt": "2022-12-02T14:13:09.391+00:00",
            "updatedAt": "2022-12-02T14:13:09.391+00:00",
            "id": 4
        },
        {
            "product": "GOOGL",
            "quantity": 30,
            "price": 1.0,
            "type": "MARKET",
            "side": "BUY",
            "orderId": "612923f8-25fb-4dab-b05e-04fbb5f3f645",
            "createdAt": "2022-12-02T14:17:03.929+00:00",
            "updatedAt": "2022-12-02T14:17:03.929+00:00",
            "id": 5
        }
    ]
}
```

<a name="get-order-by-id"></a>

## Get orders by Id

This API endpoint is used for getting an order by id from the  system.
**Note** Only authenticated users can access this endpoint

### Request Information

| Type | URL                          |
|------|------------------------------|
| POST | /api/v1/auth/order/{orderID} |

### Header

| Type          | Property name    |
|---------------|------------------|
| Allow         | POST             |
| Content-Type  | application/json |
| Authorization | Authenticated    |

### Error Responses

| Code | Message      |
|------|--------------|
| 401  | UNAUTHORIZED |
| 403  | FORBIDDEN    |
| 404  | NOT FOUND    |

### Successful Response Example

```
{
    "product": "GOOGL",
    "quantity": 30,
    "price": 0.0,
    "orderType": "MARKET",
    "side": "BUY",
    "commulativeQuantity": 0,
    "cumulatitivePrice": 0.0,
    "orderID": "7369c9de-e8df-4f32-b45f-7168fb345888",
    "executions": [],
    "createdAt": "2022-12-02T10:20:13.056+00:00",
    "updatedAt": "2022-12-02T14:25:40.856+00:00"
}
```