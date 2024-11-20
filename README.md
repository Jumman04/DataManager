# DataManager Library

A simple, efficient, and flexible data management library that allows you to store, retrieve, and manipulate data using JSON serialization and deserialization. This library is designed to easily save objects to disk, load them back, and provide high flexibility with batch processing, data change listeners, and type-safe operations.

## Features

- **JSON Serialization/Deserialization**: Easily convert objects to and from JSON format.
- **File-based Data Storage**: Store objects, strings, integers, booleans, lists, and more in local storage.
- **Batch Data Handling**: Efficiently save and load lists in batches to optimize memory usage.
- **On Data Change Listener**: Listen to changes in data to update your app in real time.
- **Type-Safe Operations**: Work with strongly-typed objects and use generics for flexibility.

## Installation

To include this library in your project, simply clone this repository and build the project, or you can manually add the source code to your project.

If you're using **Maven** or **Gradle**, follow these steps:

### Maven

```xml
<dependency>
    <groupId>com.yourdomain</groupId>
    <artifactId>data-manager</artifactId>
    <version>2.6</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.yourdomain:data-manager:2.6'
```

## Usage

### 1. Initializing the DataManager

```java
File filesDir = new File(context.getFilesDir(), "DataManager");
DataManager dataManager = new DataManagerImpl(filesDir);
```

### 2. Storing Data

Store simple data types or complex objects:

```java
dataManager.putString("user_name", "John Doe");
dataManager.putInt("user_age", 30);
```

Store a list of objects:

```java
List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
dataManager.putList("fruits", fruits);
```

### 3. Retrieving Data

Retrieve data with a specified key:

```java
String userName = dataManager.getString("user_name", "Default Name");
int userAge = dataManager.getInt("user_age", 0);
```

Retrieve a list of data:

```java
List<String> fruits = dataManager.getList("fruits", String.class);
```

### 4. Data Change Listener

You can register a listener to detect when data changes:

```java
dataManager.registerOnDataChangeListener(new OnDataChangeListener() {
    @Override
    public void onDataChanged(String key) {
        // Handle data change for the specific key
    }
});
```

## Methods Overview

- **getString(String key, String defValue)**: Get a stored string, or return the default value if not found.
- **getInt(String key, int defValue)**: Get a stored integer, or return the default value.
- **putString(String key, String value)**: Store a string value.
- **putInt(String key, int value)**: Store an integer value.
- **getObject(String key, Type type)**: Get a stored object of a specified type.
- **putObject(String key, Object value)**: Store a generic object.

For more methods, refer to the [documentation](https://jumman04.github.io/DataManager/doc/index.html).

## Contributing

We welcome contributions from the community! If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.

### Steps to contribute:

1. Fork the repository.
2. Create a new branch for your feature or bugfix.
3. Make the necessary changes and add tests.
4. Commit your changes and push to your fork.
5. Open a pull request to the main repository.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For any issues, please open an issue on GitHub, and we will get back to you as soon as possible.

---

Created with ❤️ by [Jummania](https://github.com/yourusername)
