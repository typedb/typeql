## Using TypeQL Grammar

> Note: All TypeDB Clients, as well as TypeDB Console, accept TypeQL syntax natively. If you are using TypeDB, you do not need additional libraries/tools to use TypeQL syntax natively.
> However, if you would like to build a "Language Library" for TypeQL so you can construct TypeQL queries programmatically in your preferred language, you may use TypeQL Grammar library listed below.

---

### Java

If you would like to develop language TypeQL plugins or extension in Java, and require the TypeQL grammar library, you can import the following Maven package.

```xml

<repositories>
    <repository>
        <id>repo.typedb.com</id>
        <url>https://repo.typedb.com/public/public-release/maven/</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.typeql</groupId>
    <artifactId>typeql-grammar</artifactId>
    <version>{version}</version>
</dependency>
</dependencies>
```

Replace `{version}` with the version number, in which you can find the latest of TypeQL Grammar on our [Maven Repository](https://cloudsmith.io/~typedb/repos/public-release/packages/?q=name%3A%27%5Etypeql-grammar%24%27).

---

## Python

If you would like to develop language TypeQL plugins or extension in Python, and require the TypeQL grammar library, you can import the following PyPI package.

```
pip install typeql-grammar=={version}
```

Replace `{version}` with the version number, in which you can find the latest on [TypeQL's PyPi Page](https://pypi.org/project/typeql-grammar/).

---

## Licensing

The TypeQL grammar and language libraries are licensed under the Mozilla Public License version 2.0: 
https://www.mozilla.org/en-US/MPL/2.0/
