Basic clickstream data model example
====================================

There is an example table layout in `src/main/layout/table_desc.dll`.  This layout describes a
made-up layout for a table that stores information about user purchases and page views.

There is also Avro record in `src/main/avro/MyRecords.avdl`.  This contains example Avro records for
a web-based retailer.

To run the demo, build the jars:

    mvn package

If you are using the Cassandra Bento Box, start the Bento Box by running

    source /path/to/bento/bin/kiji-env.sh
    bento start

Set up an environment variable with your Kiji URI:

    export KIJI=kiji-cassandra://localhost:2181/localhost/9042/clicks

Install your Kiji instance (a Kiji instance contains all of the tables for a given project):

    kiji install --kiji=${KIJI}

Create the Kiji table from the DDL:

    kiji-schema-shell --kiji=${KIJI} --file=src/main/layout/table_desc.ddl

Take a look at the table by starting the schema shell:

    kiji-schema-shell --kiji=${KIJI}
    schema> show tables;
    Table   Description
    =====   =============================
    users   Example user clickstream data
    schema> describe users;
    Table: users (Example user clickstream data)
    Row key: (raw bytes)

    Column family: info
            Description: information about the user

            Column info:name (name of user)
                    Default reader schema: "string"
                    1 reader schema(s) available.
                    1 writer schema(s) available.

            Column info:attributes (Information about the user)
                    Default reader schema class name: org.kiji.click.UserAttributes
                    1 reader schema(s) available.
                    1 writer schema(s) available.

    Column family: interactions
            Description: all user interactions with our application

            Column interactions:clicks (Time-series data of page views for this user)
                    Default reader schema class name: org.kiji.click.PageView
                    1 reader schema(s) available.
                    1 writer schema(s) available.

            Column interactions:searches (Time-series data of searches performed by this user)
                    Default reader schema class name: org.kiji.click.Search
                    1 reader schema(s) available.
                    1 writer schema(s) available.

    Column family: purchases
            Description: all of the items this user has purchased

            Column purchases:purchases (Time-series data of purchases)
                    Default reader schema class name: org.kiji.click.ItemPurchase
                    1 reader schema(s) available.
                    1 writer schema(s) available.

The file `DemoKiji.java` contains some example code that writes some data (a user name and an
example purchase) to a Kiji table and reads it back.
Run it with the following command:

    kiji jar target/click-1.0-SNAPSHOT.jar org.kiji.click.DemoKiji

You should see:

    Putting user Bob into table.
    Read username Bob from table.

You can also scan the data back by running:

    kiji scan $KIJI/users

And you should see:

    Scanning kiji table: kiji-cassandra://localhost:2181/localhost/9042/cas/users/
    entity-id=hbase=Bob [1398989943308] info:name
                                    Bob
    entity-id=hbase=Bob [1398989943326] interactions:purchases
                                    {"item_name": "Socks", "item_id": 10, "purchase_amount": 5.0, "transaction_id": 1234555}

