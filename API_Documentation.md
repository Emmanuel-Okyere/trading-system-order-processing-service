## Table of Contents
- [Registration](#registration)
- [Authentication - Login to get Token](#login)
- [Portfolio](#create-portfolio)
- [User's Portfolio](#get-portfolios)
- [Order](#create-order)


<a name="registration"></a>
## Registration

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
## Order

The register API will accept user credentials:
product,quantity, price,side,Portfolio and type  and saves it to the database.
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

| Property Name | type    | required | Description                                                 |
|---------------|---------|----------|-------------------------------------------------------------|
| product       | String  | true     | The name of the ticker                                      |
| quantity      | Integer | true     | the quantity a user wants                                   |
| price         | Double  | true     | the price the user wants to buy or sell for                 |
| side          | String  | true     | whether the user is selling or buying                       |
| type          | String  | true     | The type of trade the user want to create (Market or Limit) |
| portfolioId   | Integer | true     | The portfolio Id                                            |

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

<a name="create-portfolio"></a>

## Portfolio- Create

This Api endpoint accepts user's name for a portfolio and create one.
**Note** On Login, default portfolio is created if the user does not have a portfolio. The default one can not be deleted.

### Request Information

| Type | URL               |
|------|-------------------|
| POST | /api/v1/portfolio |

### Header

| Type         | Property name    |
|--------------|------------------|
| Allow        | POST, OPTIONS    |
| Content-Type | application/json |
| Vary         | Accept           |


### JSON Body

| Property Name | type   | required | Description               |
|---------------|--------|----------|---------------------------|
| ticker        | String | true     | the name of the portfolio |

### Error Responses

| Code | Message                           |
|------|-----------------------------------|
| 400  | name: This field is required.     |


### Successful Response Example

```
{
    "data": {
        "ticker": "NTFX",
        "quantity": 0,
        "id": 10
    },
    "message": "portfolio creation success",
    "status": "00"
}
```

<a name="get-portfolios"></a>

## Users Portfolio

### GET Successful Response Example
This gets the portfolios created by a particular user.
**Note** Only authenticated users can access this endpoint.

### Request Information

| Type | URL               |
|------|-------------------|
| GET  | /api/v1/portfolio |

### Header

| Type         | Property name    |
|--------------|------------------|
| Allow        | GET, OPTIONS     |
| Content-Type | application/json |
| Vary         | Accept           |


### Successful response Example

```
{
    "data": [
        {
            "ticker": "DEFAULT",
            "quantity": 290,
            "id": 1
        },
        {
            "ticker": "MSFT",
            "quantity": 177,
            "id": 4
        },
        {
            "ticker": "GOOGL",
            "quantity": 0,
            "id": 6
        },
        {
            "ticker": "NTFX",
            "quantity": 0,
            "id": 10
        }
    ],
    "message": "portfolios getting success",
    "status": "00"
}
```


<a name="get-order-by-id"></a>

## Get orders by Portfolio ID

This API endpoint is used for getting an order by id from the user's portfolio.
**Note** Only authenticated users can access this endpoint

### Request Information

| Type | URL                                    |
|------|----------------------------------------|
| GET  | /api/v1/portfolio/{portfolioID}/orders |

### Header

| Type          | Property name    |
|---------------|------------------|
| Allow         | GET              |
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
    "data": [
        {
            "product": "MSFT",
            "quantity": 30,
            "price": 1.0,
            "orderStatus": "close",
            "type": "MARKET",
            "side": "BUY",
            "orderId": "54dc98f4-6441-4712-8a04-3125c6a73289",
            "createdAt": "2022-12-09T11:00:07.519+00:00",
            "updatedAt": "2022-12-09T11:00:55.302+00:00",
            "portfolioId": null,
            "id": 2
        },
        {
            "product": "MSFT",
            "quantity": 30,
            "price": 1.0,
            "orderStatus": "close",
            "type": "MARKET",
            "side": "BUY",
            "orderId": "732c0d56-2cd4-4398-9d4e-004d3b306eb2",
            "createdAt": "2022-12-08T16:31:15.592+00:00",
            "updatedAt": "2022-12-12T08:59:17.608+00:00",
            "portfolioId": null,
            "id": 1
        },
        {
            "product": "MSFT",
            "quantity": 30,
            "price": 1.0,
            "orderStatus": "close",
            "type": "MARKET",
            "side": "BUY",
            "orderId": "3d53c503-f9c9-4367-89f0-2ccc69abd7d3",
            "createdAt": "2022-12-09T11:24:32.628+00:00",
            "updatedAt": "2022-12-12T09:07:33.059+00:00",
            "portfolioId": null,
            "id": 3
        },
        {
            "product": "MSFT",
            "quantity": 200,
            "price": 2.0,
            "orderStatus": "close",
            "type": "MARKET",
            "side": "BUY",
            "orderId": "237bf2e2-ee96-4877-9a71-1b726a6c7424",
            "createdAt": "2022-12-09T14:38:12.447+00:00",
            "updatedAt": "2022-12-12T09:14:14.094+00:00",
            "portfolioId": null,
            "id": 10
        }
    ],
    "message": "order fetch successful",
    "status": "00"
}
```
