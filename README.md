server port 8083

## API Endpoints
### Authentication

| METHOD | URL                | DESCRIPTION         |
|--------|--------------------|---------------------|
| POST   | /auth/login/       | user login          |
| POST   | /auth/register/    | create user account |
| GET    | /auth/profile/{id} | get user profile    |

### Order Processing
| METHOD | URL              | DESCRIPTION           |
|--------|------------------|-----------------------|
| POST   | /order           | create order          |
| GET    | /order/{orderId} | get order info        |
| PUT    | /order/{orderId} | update order info *** |
| DELETE | /order/{orderId} | delete order          |