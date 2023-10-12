[![TypeDB Studio](./banner.png)](https://typedb.com/docs/typeql/2.x/overview)

[![Factory](https://factory.vaticle.com/api/status/vaticle/typeql/badge.svg)](https://factory.vaticle.com/vaticle/typeql)
[![GitHub release](https://img.shields.io/github/release/vaticle/typeql.svg)](https://github.com/vaticle/typeql/releases/latest)
[![Discord](https://img.shields.io/discord/665254494820368395?color=7389D8&label=chat&logo=discord&logoColor=ffffff)](https://vaticle.com/discord)
[![Discussion Forum](https://img.shields.io/discourse/https/forum.vaticle.com/topics.svg)](https://forum.vaticle.com)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typedb-796de3.svg)](https://stackoverflow.com/questions/tagged/typedb)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typeql-3dce8c.svg)](https://stackoverflow.com/questions/tagged/typeql)

TypeQL is the query language of [TypeDB](https://github.com/vaticle/typedb).
It features a near-natural, declarative, and highly composable syntax for defining,
manipulating, querying, and reasoning over data in a TypeDB database.

* [Core design principles](#core-design-principles)
* [Query examples](#query-examples)
* [TypeQL grammar](#typeql-grammar)
* [Resources](#resources)
* [Contributions](#contributions)
* [Licensing](#licensing)

## Core design principles

### Conceptual and intuitive

TypeQL’s design is based directly on the polymorphic conceptual data model of TypeDB databases,
including syntax for modeling with `entity`, `relation`, and `attribute` types,
while also closely mirroring the structure of natural language.
Like natural language, TypeQL queries are comprised of sequences of statements, delineated with `;`.
Many statements in TypeQL queries read just like sentences,
and follow a “subject-verb-object” order, like `$some_employee has name "John Doe";`.
Moreover, all collection statements are composable,
and the composition can be given in any order —
TypeDB’s inference engine will infer all necessary types and inform the user if type constraints cannot be satisfied.

### Fully variablizable language

Variables in TypeQL are pre-fixed with `$`.
Just as any part in a sentence might be unknown to us, any part of a TypeQL statement can be variablized.
For example, both `$some_employee has name $full_name;` and `$some_employee has $some_attribute "John Doe";`
are valid in TypeQL syntax.
The ability to variablize types is underpinned by a form of [parametric polymorphism](https://typedb.com/features#polymorphic-queries),
which leads to particularly powerful queries and database operations that can be performed with TypeDB.

### Extensible and built for consistency

TypeQL’s statement-based syntax can be naturally interleaved with many other declarative constructs,
including variable arithmetic or regular expressions.
All these additional construct are integrated into database’s type system,
which ensures consistency of any query sent to the database.
For a more in-depth overview of the range of statements
that are available in TypeQL check out our [TypeQL in 20 queries guide](https://typedb.com/features)!

## Query examples

### Entity-Relation-Attribute

TypeQL features the type system of the [Polymorphic Entity-Relation-Attribute](https://development.typedb.com/philosophy)
(PERA) model:
entities are independent concepts,
relations depend on role interfaces played by either entities or relations,
and attributes are properties with a value that can interface with (namely, be owned by) entities or relations.
Entities, relations, and attributes are all considered first-class citizens and can be subtyped,
allowing for expressive modeling without the need for normalization or reification.

```typeql
define

id sub attribute, value string;
email sub id;
path sub id;
name sub id;

user sub entity,
    owns email @unique,
    plays permission:subject,
    plays request:requestee;
file sub entity,
    owns path,
    plays permission:object;
action sub entity,
    owns name,
    plays permission:action;

permission sub relation,
    relates subject,
    relates object,
    relates action,
    plays request:target;
request sub relation,
    relates target,
    relates requestee;
```

### Polymorphic querying

Use subtyping to query a common supertype and automatically retrieve matching data.
Variablize queries to return types, roles, and data.
New types added to the schema are automatically included in the results of pre-existing queries against their supertype,
so no refactoring is necessary.

```typeql
match $user isa user,
    has full-name $name,
    has email $email;
# This returns all users of any type

match $user isa employee,
    has full-name $name,
    has email $email,
    has employee-id $id;
# This returns only users who are employees

match $user-type sub user;
$user isa $user-type,
    has full-name $name,
    has email $email;
# This returns all users and their type
```

### Near Natural and fully declarative

TypeQL's near-natural syntax and fully declarative properties make queries easily understandable,
reducing the learning curve and easing maintenance.
This allows you to define query patterns without considering execution strategy.
TypeDB's query planner always optimizes queries, so you don't have to worry about the logical implementation.

```typeql
match
$kevin isa user, has email "kevin@vaticle.com";

insert
$chloe isa full-time-employee,
    has full-name "Chloé Dupond",
    has email "chloe@vaticle.com",
    has employee-id 185,
    has weekly-hours 35;
$hire (employee: $chloe, ceo: $kevin) isa hiring,
    has date 2023-09-27;
```

### Composable patterns

TypeDB's TypeQL query language uses pattern matching to find data.
Patterns in TypeQL are fully composable.
Every complex pattern can be broken down into a conjunction of atomic constraints,
which can be concatenated in any order.
Any pattern composed of valid constraints is guaranteed to be valid itself, no matter how complex.

```typeql
match 
$user isa user;

match
$user isa user;
$user has email "john@vaticle.com";

match
$user isa user;
$user has email "john@vaticle.com";
(team: $team, member: $user) isa team-membership;

match
$user isa user;
$user has email "john@vaticle.com";
(team: $team, member: $user) isa team-membership;
$team has name "Engineering";
```

## TypeQL grammar

> Note: All TypeDB Clients, as well as TypeDB Console, accept TypeQL syntax natively. 
> If you are using TypeDB, you do not need additional libraries/tools to use TypeQL syntax natively.
> However, if you would like to construct TypeQL queries programmatically, you can do so with "Language Libraries" listed below.

- [TypeQL Grammar](https://github.com/vaticle/typeql/blob/master/grammar/README.md)
- [TypeQL Language Library for Java](https://github.com/vaticle/typeql/blob/master/java)
- [TypeQL Language Library for Rust (under development)](https://github.com/vaticle/typeql/blob/master/rust)
- [TypeQL Language Library for Python (under development)](https://github.com/typedb-osi/typeql-lang-python)

## Resources

### Developer resources

- Documentation: https://typedb.com/docs
- Discussion Forum: https://forum.typedb.com/
- Discord Chat Server: https://typedb.com/discord
- Community Projects: https://github.com/typedb-osi

### Useful links

If you want to begin your journey with TypeDB, you can explore the following resources:

* More on TypeDB's [features](https://typedb.com/features)
* In-depth dive into TypeDB's [philosophy](https://typedb.com/philosophy)
* Our [TypeDB quickstart](https://typedb.com/docs/typedb/2.x/quickstart-guide)

## Contributions

TypeDB and TypeQL are built using various open-source frameworks and technologies throughout its evolution. 
Today TypeDB and TypeQL use
[Speedb](https://www.speedb.io/),
[pest](https://pest.rs/),
[SCIP](https://www.scipopt.org),
[Bazel](https://bazel.build),
[gRPC](https://grpc.io),
[ZeroMQ](https://zeromq.org), 
and [Caffeine](https://github.com/ben-manes/caffeine). 

Thank you!

In the past, TypeDB was enabled by various open-source products and communities that we are hugely thankful to:
[RocksDB](https://rocksdb.org),
[ANTLR](https://www.antlr.org),
[Apache Cassandra](http://cassandra.apache.org), 
[Apache Hadoop](https://hadoop.apache.org), 
[Apache Spark](http://spark.apache.org), 
[Apache TinkerPop](http://tinkerpop.apache.org), 
and [JanusGraph](http://janusgraph.org). 

## Licensing

The TypeQL language libraries, such as TypeQL Rust and Java, are distributed under Apache License, Version 2.0, January 2004.
The full license can be founder at: [LICENSE](https://github.com/vaticle/typeql/blob/master/LICENSE).

However, the TypeQL Grammar libraries, located under the `/grammar` package in this repository, 
are distributed under the terms GNU Affero General Public License v3.0 ("AGPL 3.0"), but with a special exception. 
Please refer to [TypeQL Grammar Licensing](https://github.com/vaticle/typeql/blob/master/grammar/README.md#licensing) for further details.

Copyright (C) 2023 Vaticle.
