# Generic XML Writer

A type-safe XML file writer for Java 21+ that allows efficient export of objects to XML files using functional mappings without reflection.

## Features

*   **Type Safety:** Full support for Java Generics to avoid runtime errors.
*   **Functional Mapping:** Define XML elements using method references or lambdas (e.g., `Person::getName`).
*   **Hierarchical Structures:** Support for deeply nested XML structures and child elements.
*   **Collections:** Specialized `XmlCollectionField` for handling lists and sets.

## Prerequisites

*   **Java 21** or higher

## Quick Start

### 1. Define Data Model
Preferably use Java Records for a compact representation:

```java
public record User(UUID id, String userName, int age, List<User> friends) {}
```

### 2. Configure Writer
Define the mapping between your object and the XML structure:

```java
import org.benschwi.XmlCollectionField;

XmlFileWriter<User> writer = new XmlFileWriter<>(
        new XmlField<>("id", User::id),
        new XmlField<>("name", User::userName),
        new XmlCollectionField<User, User>("friends", "friend", User::friends,
                new XmlField<>("name", User::userName),
                new XmlField<>("age", User::age))
);
```

### 3. Write XML File
Export a list of objects to a file:

```java

writer.writeAndCreateXMLFile(
    "C:\\desktop\\userFolder",
    "fileName", // the name of the file
    "UserEntries", // the root element
    "comment", // a comment at the top of the file (optional)
    8192, // the buffer size (optional)
    List.of(
            new User(UUID.randomUUID(), "John", 30, List.of()),
            new User(UUID.randomUUID(), "Jane", 25, List.of()),
            new User(UUID.randomUUID(), "Doe", 40, List.of())
    )
);
```

## Components to use

*   `XmlFileWriter<A>`: The main component for creating XML files.
*   `XmlField<A, B>`: Represents a single XML tag with mapping logic.
*   `XmlCollectionField<A, B>`: Specialization for list elements.
*   `XmlNode<A>`: Base interface for all XML components.

