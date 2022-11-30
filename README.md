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

### How to set up and run Kafka
1. Download kafka from https://kafka.apache.org/downloads
2. tar -xzf kafka_2.13-3.3.1.tgz
      1. #### extract it:
              tar -xzf kafka_2.13-3.3.1.tgz
      2. Start the ZooKeeper service
          #### Windows:
              .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

          #### Mac:
             bin/zookeeper-server-start.sh config/zookeeper.properties
     3. Start the Kafka broker service
          #### Windows:
                .\bin\kafka-server-start.bat .\config\server.properties
    
          #### Mac:
                bin/kafka-server-start.sh config/server.properties