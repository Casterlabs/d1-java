# d1-java

## Usage
```java
D1 d1 = new D1.builder()
//    .withLogLevel(LogLevel.ALL) // Useful for debugging
    .withAccountId("...")
    .withDatabaseId("...")
    .withApiToken("...")
    .withApiUrl("https://...")    // If for some reason you have access to some private API, omit to use the public Cloudflare API.
    .build();

FastLogger.logStatic(
    d1
        .query("SELECT * FROM myTable")
        .first()
    // {"field":123123}
);

FastLogger.logStatic(
    d1
        .query("SELECT * FROM myTable")
        .first("field")
    // 123123
);

FastLogger.logStatic(
    d1
        .query("SELECT * FROM myTable")
        .all()
        .asArray()
    // [{"field":123123}, {"field":2456235}, {"field":574754457}]
);
```

Want the result as a POJO? Use `as()`.

```java
d1
    .query("SELECT * FROM myTable")
    .first()
	.as(MyClass.class);

// Or, if you want an array...
d1
    .query("SELECT * FROM myTable")
    .all()
	.as(MyClass[].class);

// Or, if you want a collection...
d1
    .query("SELECT * FROM myTable")
    .all()
	.as(new TypeToken<List<MyClass>>() {});
```

## Installation (Maven)
```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>co.casterlabs</groupId>
            <artifactId>d1-java</artifactId>
            <version>LATEST_VERSION_OR_COMMITHASH_HERE</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
```

## Libraries Used

- [Rson](https://github.com/Casterlabs/Rakurai/tree/main/Json) - Our own in-house JSON library.
- [FastLoggingFramework](https://github.com/e3ndr/FastLoggingFramework) - Exactly what it says on the tin.
- [OkHttp](https://square.github.io/okhttp/) - A http Java library.
- [Lombok](https://projectlombok.org) - Useful code generation for Java.
- [IntelliJ Annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html) - Useful code documentation.

