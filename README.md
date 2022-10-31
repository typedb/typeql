[![Factory](https://factory.vaticle.com/api/status/vaticle/typeql/badge.svg)](https://factory.vaticle.com/vaticle/typeql)
[![GitHub release](https://img.shields.io/github/release/vaticle/typeql.svg)](https://github.com/vaticle/typeql/releases/latest)
[![Discord](https://img.shields.io/discord/665254494820368395?color=7389D8&label=chat&logo=discord&logoColor=ffffff)](https://vaticle.com/discord)
[![Discussion Forum](https://img.shields.io/discourse/https/forum.vaticle.com/topics.svg)](https://forum.vaticle.com)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typedb-796de3.svg)](https://stackoverflow.com/questions/tagged/typedb)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typeql-3dce8c.svg)](https://stackoverflow.com/questions/tagged/typeql)

# Meet TypeQL (and [TypeDB](https://github.com/vaticle/typedb))

TypeDB is a strongly-typed database with a rich and logical type system. TypeDB empowers you to tackle complex problems, and TypeQL is its query language.

## A higher level of expressivity

TypeQL allows you to model your domain based on logical and object-oriented principles. Composed of entity, relationship, and attribute types, as well as type hierarchies, roles, and rules, TypeQL allows you to think higher-level as opposed to join-tables, columns, documents, vertices, edges, and properties.

### Entity-Relationship Model

TypeQL allows you to model your domain using the well-known Entity-Relationship model. It is composed of entity types, relation types, and attribute types, with the introduction of role types. TypeQL allows you to leverage the full expressivity of the ER model, and describe your schema through first normal form.

```typeql
define

person sub entity,
  owns name,
  plays employment:employee;
company sub entity,
  owns name,
  plays employment:employer;
employment sub relation,
  relates employee,
  relates employer;
name sub attribute,
  value string;
```

### Type Hierarchies

TypeQL allows you to easily model type inheritance into your domain model. Following logical and object-oriented principle, TypeQL allows data types to inherit the behaviours and properties of their supertypes. Complex data structures become reusable, and data interpretation becomes richer through polymorphism.

```typeql
define

person sub entity,
  owns first-name,
  owns last-name;

student sub person;
undergrad sub student;
postgrad sub student;

teacher sub person;
supervisor sub teacher;
professor sub teacher;
```


### N-ary Relations

In the real world, relations aren't just binary connections between two things. In rich systems, we often need to capture three or more things related with each other at once. Representing them as separate binary relationships would lose information. TypeQL can naturally represent arbitrary number of things as one relation.

```typeql
match
 
$person isa person, has name "Leonardo";
$character isa character, has name "Jack";
$movie isa movie;
(actor: $person, character: $character, movie: $movie) isa cast;
get $movie;
 
answers>>
 
$movie isa movie, has name "Titanic";
```


### Nested Relations

Relations are concepts we use to describe the association between two or more things. Sometimes, those things can be relations themselves. TypeQL can represent these structures naturally, as it enables relations to be nested in another relation, allowing you to express the model of your system in the most natural form.

```typeql
match
 
$alice isa person, has name "Alice";
$bob isa person, has name "Bob";
$mar ($alice, $bob) isa marriage;
$city isa city;
($mar, $city) isa located;
 
answers>>
 
$city isa city, has name "London";
```


## A higher degree of safety

Types provide a way to describe the logical structures of your data, allowing TypeDB to validate that your code inserts and queries data correctly. Query validation goes beyond static type checking, and includes logical validations of meaningless queries. With strict type-checking errors, you have a dataset that you can trust.

### Logical Data Validation

Inserted data gets validated beyond static type checking of attribute value types. Entities are validated to only have the correct attributes, and relations are validated to only relate things that are logically allowed. TypeDB performs richer validation of inserted entities and relations by evaluating the polymorphic types of the things involved.

```typeql
insert

$charlie isa person, has name "Charlie";
$dataCo isa company, has name "DataCo";
(husband: $charlie, wife: $dataCo) isa marriage; # invalid relation

commit>>

ERROR: invalid data detected during type validation
```


### Logical Query Validation

Read queries executed on TypeDB go through a type resolution process. This process not only optimises the query's execution, but also acts as a static type checker to reject meaningless and unsatisfiable queries, as they are likely a user error.

```typeql
match

$alice isa person, has name "Alice";
$bob isa person, has name "Bob";
($alice, $bob) isa marriage;
$dataCo isa company, has name "DataCo";
($bob, $dataCo) isa marriage; # invalid relation

answers>>

ERROR: unsatisfiable query detected during type resolution
```

## Evolved with logical inference

TypeDB encodes your data for logical interpretation by a reasoning engine. It enables type-inference and rule-inference that creates logical abstractions of data. This allows the discovery of facts and patterns that would otherwise be too hard to find; and complex queries become much simpler.

### Rules

TypeQL allows you to define rules in your schema. This extends the expressivity of your model as it enables the system to derive new conclusions when a certain logical form in your dataset is satisfied. Like functions in programming, rules can chain onto one another, creating abstractions of behaviour at the data level.

```typeql
define

rule transitive-location:
when {
  (located: $x, locating: $y);
  (located: $y, locating: $z);
} then {
  (located: $x, locating: $z);
};
```

### Inference

TypeDB's inference facility translates one query into all of its possible interpretations. This happens through two mechanisms: type-based and rule-based inference. Not only does this derive new conclusions and uncovers relationships that would otherwise be hidden, but it also enables the abstraction of complex patterns into simple queries.

```typeql
match

$person isa person;
$uk isa country, has name "UK";
($person, $uk) isa location;
get $person;

answers>>

$person isa teacher, has name "Alice";
$person isa postgrad, has name "Bob";
```

## TypeQL Grammar and Language Libraries

> Note: All TypeDB Clients, as well as TypeDB Console, accept TypeQL syntax natively. If you are using TypeDB, you do not need additional libraries/tools to use TypeQL syntax natively.
> However, if you would like to construct TypeQL queries programmatically, you can do so with "Language Libraries" listed below.

- [TypeQL Grammar](https://github.com/vaticle/typeql/blob/master/grammar/README.md)
- [TypeQL Language Library for Java](https://github.com/vaticle/typeql/blob/master/java)
- [TypeQL Language Library for Rust (under development)](https://github.com/vaticle/typeql/blob/master/rust)
- [TypeQL Language Library for Python (under development)](https://github.com/typedb-osi/typeql-lang-python)

## Contributions

TypeDB & TypeQL has been built using various open-source Graph and Distributed Computing frameworks throughout its evolution. Today TypeDB & TypeQL is built using [RocksDB](https://rocksdb.org), [ANTLR](http://www.antlr.org), [SCIP](https://www.scipopt.org), [Bazel](https://bazel.build), [GRPC](https://grpc.io), and [ZeroMQ](https://zeromq.org), and [Caffeine](https://github.com/ben-manes/caffeine). In the past, TypeDB was enabled by various open-source technologies and communities that we are hugely thankful to: [Apache Cassandra](http://cassandra.apache.org), [Apache Hadoop](https://hadoop.apache.org), [Apache Spark](http://spark.apache.org), [Apache TinkerPop](http://tinkerpop.apache.org), and [JanusGraph](http://janusgraph.org). Thank you!

---

## Licensing

The TypeQL language libraries, such as TypeQL Rust and Java, are distributed under Apache License, Version 2.0, January 2004. The full license can be founder at: [LICENSE](https://github.com/vaticle/typeql/blob/master/LICENSE).

However, the TypeQL Grammar libraries, located under the `/grammar` package in this repository, are distributed under the terms GNU Affero General Public License v3.0 ("AGPL 3.0") as published by the Free Software Foundation, but with a special exception. Please refer to [TypeQL Grammar Licensing](https://github.com/vaticle/typeql/blob/master/grammar/README.md#licensing) for further details.

Copyright (C) 2022 Vaticle
