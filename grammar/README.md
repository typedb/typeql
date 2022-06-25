## Using TypeQL Grammar

> Note: All TypeDB Clients, as well as TypeDB Console, accept TypeQL syntax natively. If you are using TypeDB, you do not need additional libraries/tools to use TypeQL syntax natively.
> > However, if you would like to build a "Language Library" for TypeQL so you can construct TypeQL queries programmatically in your preferred language, you may use TypeQL Grammar library listed below.

---

### Java

If you would like to develop language TypeQL plugins or extension in Java, and require the TypeQL grammar library, you can import the following Maven package.

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

Replace `{version}` with the version number, in which you can find the latest of TypeQL Grammar on our [Maven Repository](https://repo.vaticle.com/#browse/browse:maven:com%2Fvaticle%2Ftypeql%2Ftypeql-grammar).

---

## Python

If you would like to develop language TypeQL plugins or extension in Python, and require the TypeQL grammar library, you can import the following PyPI package.

```
pip install typeql-grammar=={version}
```

Replace `{version}` with the version number, in which you can find the latest on [TypeQL's PyPi Page](https://pypi.org/project/typeql-grammar/).

---

## Licensing

The TypeQL Grammar libraries are distributed under the terms GNU Affero General Public License v3.0 ("AGPL 3.0") as published by the Free Software Foundation, but with the following special exception:

Any TypeQL language library that is based on material or materials in the Vaticle TypeQL repository, and that is used to communicate or interact (in each case) with a database created or managed or accessed (in each case) using a version of the TypeQL software that is made available by or on behalf of Vaticle Limited (UK Company Number 08766237) or any successor entity (but excluding any forked version of that software), may be distributed under one of the following licences:

- The Apache License version 2: https://www.apache.org/licenses/LICENSE-2.0.txt
- The MIT License: https://opensource.org/licenses/MIT
- The BSD License (2-Clause): https://opensource.org/licenses/BSD-2-Clause

As used above "successor entity" means any entity then owning copyrights in the TypeDB software that were previously owned by Vaticle Ltd.

Copyright (C) 2022 Vaticle
