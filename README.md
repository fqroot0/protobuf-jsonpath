# protobuf-jsonpath
A Java library for reading protobuf message with [JsonPath](https://github.com/json-path/JsonPath).

# Building from Source
```bash
git clone https://github.com/fqroot0/protobuf-jsonpath.git
cd protobuf-jsonpath
mvn clean package -DskipTests
```

# Getting started
```java
byte[] bytes = ...;
Descriptor descriptor = AddressBook.getDescriptor();

DynamicMessage dynamicMessage = DynamicMessage.parseFrom(descriptor, bytes);

Configuration conf = Configuration.defaultConfiguration()
        .jsonProvider(new PbProvider());
PbParseContextImpl pbParseContext = new PbParseContextImpl(conf);

DocumentContext ctx = pbParseContext.parse(dynamicMessage);

Object obj = ctx.read("$.people[1].name", String.class);
obj = ctx.read("$..number", List.class);
obj = ctx.read("$.people[0:2]");
obj = ctx.read("$..people[?(@.id==0)].name");
```