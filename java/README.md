# TypeQL Language Library for Java

TypeQL language library for Java allows you to construct TypeQL queries programmatically, as opposed to manual string concatenations. For example, take the following native TypeQL query.

```typeql
match $x isa person, has name "alice", has age 32;
``` 

The native TypeQL query above can be constructed programmatically in Java using this library, in the following way.

```java
TypeQL.match(cVar("x").isa("person").has("name", "alice").has("age", 32));
```

You can learn more about TypeQL Language Library for Java from [typedb.com/docs](https://typedb.com/docs/query/overview). You can find TypeDB and TypeQL repositories at [typedb/typedb](https://github.com/typedb/typedb) and [typedb/typeql](https://github.com/typedb/typeql).

## Importing TypeQL Language Library through Maven

```xml
<repositories>
    <repository>
        <id>repo.typedb.com</id>
        <url>https://repo.typedb.com/public/public-release/maven/</url>
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

Replace `{version}` with the version number, in which you can find the latest on [TypeQL's Maven Repository](https://cloudsmith.io/~typedb/repos/public-release/packages/?q=name%3A%27%5Etypeql-lang%24%27). Further documentation: http://typedb.com/docs/client-api/java#typeql

## Building TypeQL from Source

> Note: You don't need to compile TypeQL from source if you just want to use it in your code. See the _"Importing TypeQL"_ section above.

1. Make sure you have the following dependencies installed on your machine:
    - Java 11
    - [Bazel](https://docs.bazel.build/versions/master/install.html)

2. Build the JAR:

   a) to build the native/raw JAR:
   ```
   bazel build//java:typeql
   ```
   The Java library JAR will be produced at: `bazel-bin/libclient-java.jar`

   b) to build the JAR for a Maven application:
   ```
   bazel build //java:assemble-maven
   ```
   The Maven JAR and POM will be produced at: 
   ```
   bazel-bin/java/com.vaticle.typeql:lang.jar
   bazel-bin/java/pom.xml
   ```