server port 8083

## API Endpoints
### Authentication

| METHOD | URL                    | DESCRIPTION         |
|--------|------------------------|---------------------|
| POST   | /api/v1/auth/login/    | user login          |
| POST   | /api/v1/auth/register/ | create user account |
| GET    | /auth/profile/{id}     | get user profile    |

### Order Processing
| METHOD | URL                     | DESCRIPTION           |
|--------|-------------------------|-----------------------|
| POST   | /api/v1/order           | create order          |
| GET    | /api/v1/order/          | get order             |
| GET    | /api/v1/order/{orderId} | get order info        |
| PUT    | /api/v1/order/{orderId} | update order info *** |
| DELETE | /api/v1/order/{orderId} | delete order          |

### To Run the Athentications, run these on your database first
INSERT INTO role (name) VALUES ('ADMIN')

INSERT INTO role (name) VALUES ('USER')