[![Grabl](https://grabl.io/api/status/vaticle/typeql/badge.svg)](https://grabl.io/vaticle/typeql)
[![GitHub release](https://img.shields.io/github/release/vaticle/typeql.svg)](https://github.com/vaticle/typeql/releases/latest)
[![Discord](https://img.shields.io/discord/665254494820368395?color=7389D8&label=chat&logo=discord&logoColor=ffffff)](https://vaticle.com/discord)
[![Discussion Forum](https://img.shields.io/discourse/https/forum.vaticle.com/topics.svg)](https://forum.vaticle.com)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typedb-796de3.svg)](https://stackoverflow.com/questions/tagged/typedb)
[![Stack Overflow](https://img.shields.io/badge/stackoverflow-typeql-3dce8c.svg)](https://stackoverflow.com/questions/tagged/typeql)

TypeDB is a distributed knowledge graph: a logical database to organise large and complex networks of data as one body of knowledge.

| Get Started | Documentation | Discussion |
|:------------|:--------------|:-----------|
| Whether you are new to coding or an experienced developer, it’s easy to learn and use TypeDB. Get set up quickly with [quickstart tutorial](https://docs.vaticle.com/docs/general/quickstart). | Documentation for TypeDB’s development library and TypeQL language API, along with tutorials and guides, are available online. Visit our [documentation portal](https://docs.vaticle.com/). | When you’re stuck on a problem, collaborating helps. Ask your question on [StackOverflow](https://stackoverflow.com/questions/tagged/typeql+or+typedb) or discuss it on our [Discussion Forum](https://forum.vaticle.com/). |

# Meet TypeDB and TypeQL

TypeDB is a distributed knowledge graph: a logical database to organise large and complex networks of data as one body of knowledge. TypeDB provides the [knowledge engineering](https://en.wikipedia.org/wiki/Knowledge_engineering) tools for developers to easily leverage the power of [Knowledge Representation and Automated Reasoning](https://en.wikipedia.org/wiki/Knowledge_representation_and_reasoning) when building complex systems. Ultimately, TypeDB serves as the knowledge-base foundation for intelligent systems.

[TypeQL](https://github.com/vaticle/typeql) is TypeDB's reasoning and analytics query language. It provides an expressive knowledge schema language through an enhanced entity-relationship model, transactional queries that perform deductive reasoning in real-time, and analytical queries* with native distributed Pregel and MapReduce algorithms. TypeQL provides a strong abstraction over low-level data constructs and complex relationships. (* analytics queries are temporarily unavailable in 2.0.0)

TypeQL is distributed as an open-source technology, while TypeDB comes in two forms: TypeDB - open-source, and TypeDB Cluster - our enterprise distributed knowledge graph.

## Knowledge Schema

TypeDB provides an enhanced [entity-relationship](https://en.wikipedia.org/wiki/Entity–relationship_model) schema to model complex datasets. The schema allows users to model type hierarchies, hyper-entities, hyper-relationships, and rules. The schema can be updated and extended at any time in the database lifecycle. Hyper-entities are entities with multiple instances of a given attribute, and hyper-relationships are nested relationships, cardinality-restricted relationships, or relationships between any number of entities. This enables the creation of complex knowledge models very easily and allows them to evolve flexibly.

Under the hood, TypeDB has an expressive knowledge representation system based on [hypergraph](https://en.wikipedia.org/wiki/Hypergraph) data structures (that generalises an edge to be a set of vertices - non-binary). TypeQL is TypeDB’s reasoning (through OLTP) and analytics (through OLAP) declarative query language. 

## Logical Inference

TypeDB’s query language performs logical inference through [deductive reasoning](https://en.wikipedia.org/wiki/Deductive_reasoning) of entity types and relationships, to infer implicit facts, associations, and conclusions in real-time, during runtime of OLTP queries. The inference is performed through entity and relationship type reasoning, as well as rule-based reasoning. This allows the discovery of facts that would otherwise be too hard to find, the abstraction of complex relationships into its simpler conclusion, as well as translation of higher-level queries into the lower level and more complex data representation.

## Distributed Analytics (temporarily unavailable in 2.0.0)

TypeDB’s query language performs distributed [Pregel](https://kowshik.github.io/JPregel/pregel_paper.pdf) and [MapReduce](https://en.wikipedia.org/wiki/MapReduce) ([BSP](https://en.wikipedia.org/wiki/Bulk_synchronous_parallel)) algorithms abstracted as OLAP queries. These types of queries usually require custom development of distributed algorithms for every use case. However, TypeDB creates an abstraction of these distributed algorithms and incorporates them as part of the language API. This enables large scale computation of BSP algorithms through a declarative language without the need of implementing the algorithms.

## Higher-Level Language

With the expressivity of the schema, inference through OLTP, and distributed algorithms through OLAP, TypeDB provides a strong abstraction over low-level data constructs and complicated relationships through its query language. The language provides a higher-level schema, OLTP, and OLAP query language, which makes working with complex data a lot easier. When developers can achieve more by writing less code, the productivity rate increases by orders of magnitude.

## Importing TypeQL through Maven (for Java)

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
        <artifactId>typeql-grammar</artifactId>
        <version>{version}</version>
    </dependency>
</dependencies>
```

Replace `{version}` with the version number, in which you can find the latest on [TypeQL's Maven Repository](https://repo.vaticle.com/#browse/browse:maven:com%2Fvaticle%2Ftypeql%2Ftypeql-lang). Further documentation: http://docs.vaticle.com/docs/client-api/java#typeql

## Contributions

TypeDB & TypeQL has been built using various open-source Graph and Distributed Computing frameworks throughout its evolution. Today TypeDB & TypeQL is built using [RocksDB](https://rocksdb.org), [ANTLR](http://www.antlr.org), [SCIP](https://www.scipopt.org), [Bazel](https://bazel.build), [GRPC](https://grpc.io), and [ZeroMQ](https://zeromq.org), and [Caffeine](https://github.com/ben-manes/caffeine). In the past, TypeDB was enabled by various open-source technologies and communities that we are hugely thankful to: [Apache Cassandra](http://cassandra.apache.org), [Apache Hadoop](https://hadoop.apache.org), [Apache Spark](http://spark.apache.org), [Apache TinkerPop](http://tinkerpop.apache.org), and [JanusGraph](http://janusgraph.org). Thank you!

## Licensing

This product includes software developed by [Vaticle](https://vaticle.com/).  It's released under the GNU Affero GENERAL PUBLIC LICENSE, Version 3, 19 November 2007. For license information, please see [LICENSE](https://github.com/vaticle/typedb/blob/master/LICENSE). Vaticle also provides a commercial license for TypeDB Cluster - get in touch with our team at enterprise@vaticle.com.

Copyright (C) 2020 Vaticle
