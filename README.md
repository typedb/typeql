[![Grabl](https://grabl.io/api/status/vaticle/typeql-lang-java/badge.svg)](https://grabl.io/vaticle/typeql-lang-java)
[![GitHub release](https://img.shields.io/github/release/vaticle/typeql-lang-java.svg)](https://github.com/vaticle/typeql-lang-java/releases/latest)
[![Discord](https://img.shields.io/discord/665254494820368395?color=7389D8&label=chat&logo=discord&logoColor=ffffff)](https://vaticle.com/discord)
[![Discussion Forum](https://img.shields.io/discourse/https/forum.vaticle.com/topics.svg)](https://forum.vaticle.com)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typedb-796de3.svg)](https://stackoverflow.com/questions/tagged/typedb)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typeql-3dce8c.svg)](https://stackoverflow.com/questions/tagged/typeql)

# TypeQL Language Library for Java

TypeQL language library for Java allows you to construct TypeQL queries programmatically, as opposed to manual string concatenations. For example, take the following native TypeQL query.

```typeql
match $x isa person, has name "alice", has age 32;
``` 

The native TypeQL query above can be constructed programmatically in Java using this library, in the following way.

```java
TypeQL.match(var("x").isa("person").has("name", "alice").has("age", 32));
```

You can learn more about TypeQL Language Library for Java from [docs.vaticle.com](https://docs.vaticle.com/docs/query/overview). You can find TypeDB and TypeQL repositories at [vaticle/typedb](https://github.com/vaticle/typedb) and [vaticle/typeql](https://github.com/vaticle/typeql).

## Importing TypeQL Language Library through Maven

```xml
<repositories>
    <repository>
        <id>repo.vaticle.com</id>
        <url>https://repo.vaticle.com/repository/maven/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.vaticle.typeql</groupId>
        <artifactId>typeql-lang</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>
```

Replace `{version}` with the version number, in which you can find the latest on [TypeQL's Maven Repository](https://repo.vaticle.com/#browse/browse:maven:com%2Fvaticle%2Ftypeql%2Ftypeql-lang). Further documentation: http://docs.vaticle.com/docs/client-api/java#typeql

## Building TypeQL from Source

> Note: You don't need to compile TypeQL from source if you just want to use it in your code. See the _"Importing TypeQL"_ section above.

1. Make sure you have the following dependencies installed on your machine:
    - Java 8
    - [Bazel](https://docs.bazel.build/versions/master/install.html)

2. Build the JAR:

   a) to build the native/raw JAR:
   ```
   bazel build//:typeql
   ```
   The Java library JAR will be produced at: `bazel-bin/libclient-java.jar`

   b) to build the JAR for a Maven application:
   ```
   bazel build //:assemble-maven
   ```
   The Maven JAR and POM will be produced at: 
   ```
   bazel-bin/java/com.vaticle.typeql:lang.jar
   bazel-bin/java/pom.xml
   ```

## Contributions

TypeDB & TypeQL has been built using various open-source Graph and Distributed Computing frameworks throughout its evolution. Today TypeDB & TypeQL is built using [RocksDB](https://rocksdb.org), [ANTLR](http://www.antlr.org), [SCIP](https://www.scipopt.org), [Bazel](https://bazel.build), [GRPC](https://grpc.io), and [ZeroMQ](https://zeromq.org), and [Caffeine](https://github.com/ben-manes/caffeine). In the past, TypeDB was enabled by various open-source technologies and communities that we are hugely thankful to: [Apache Cassandra](http://cassandra.apache.org), [Apache Hadoop](https://hadoop.apache.org), [Apache Spark](http://spark.apache.org), [Apache TinkerPop](http://tinkerpop.apache.org), and [JanusGraph](http://janusgraph.org). Thank you!

## Licensing

This software is developed by [Vaticle](https://vaticle.com/).  It's released under the GNU Affero GENERAL PUBLIC LICENSE, Version 3, 29 June 2007. For license information, please see [LICENSE](https://github.com/vaticle/typedb/blob/master/LICENSE).

Copyright (C) 2020 Vaticle
