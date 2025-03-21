# DataManager Library

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
[![](https://jitpack.io/v/Jumman04/DataManager.svg)](https://jitpack.io/#Jumman04/DataManager)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A simple, efficient, and flexible data management library that allows you to store, retrieve, and
manipulate data using JSON serialization and deserialization.

## Features

- **JSON Serialization/Deserialization**: Easily convert objects to and from JSON format.
- **File-based Data Storage**: Store objects, strings, integers, booleans, lists, and more in local
  storage.
- **Batch Data Handling**: Efficiently save and load lists in batches to optimize memory usage.
- **On Data Change Listener**: Listen to changes in data to update your app in real time.
- **Type-Safe Operations**: Work with strongly-typed objects and use generics for flexibility.
- **Customizable Converter**: **NEW in version 2.8!** You can choose from the following built-in
  converters:
    - **FastJsonConverter**
    - **GsonConverter**
    - **JacksonConverter**
    - **JsonIterConverter**
    - **LoganSquareConverter**
    - **MoshiConverter**
    - **KotlinxSerializationConverter**

  You can also create your own converter by implementing the `DataManager.Converter` interface,
  allowing for even greater flexibility in handling data.

---

## Installation

To include this library in your project, simply clone this repository and build the project, or you
can manually add the source code to your project.

Follow these steps to integrate the **DataManager** library into your project:

### Step 1: Add the JitPack Repository

Add the JitPack repository to your root `settings.gradle` file:

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the Dependency

Add the following dependency to your module-level `build.gradle` file:

```groovy
implementation 'com.github.Jumman04:DataManager:2.8'
```

---

## Usage

### 1. Initializing the DataManager

You can initialize the DataManager with a custom converter:

```java
DataManager dataManager = DataManagerFactory.create(getFilesDir(), new GsonConverter());
```

### 2. Storing Data

Store simple data types or complex objects:

```java
dataManager.putString("user_name","John Doe");
dataManager.

putInt("user_age",30);
```

Store a list of objects:

```java
List<String> fruits = Arrays.asList("Apple", "Banana", "Cherry");
dataManager.

putList("fruits",fruits);
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
dataManager.addDataObserver(new DataObserver() {
    @Override
    public void onDataChange (String key){
        // Handle data change for the specific key
    }

    @Override
    public void onError (Throwable error){
        // Handle error if necessary
        System.err.println("Error occurred for key '" + key + "': " + error.getMessage());
    }
});
```

### 5. Creating a Custom Converter

To create your own converter, implement the `DataManager.Converter` interface as follows:

```java
public class MyCustomConverter implements DataManager.Converter {
    @Override
    public <T> String toJson(T data) {
        return // Implement serialization logic
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return // Implement deserialization logic
    }

    @Override
    public <T> T fromReader(Reader json, Type typeOfT) {
        return // Implement deserialization logic from a Reader
    }
}
```

You can then use your custom converter when initializing the `DataManager`:

```java
DataManager dataManager = DataManagerFactory.create(getFilesDir(), new MyCustomConverter());
```

## Methods Overview

| Return Type   | Method Name                                                         | Description                                                |
|---------------|---------------------------------------------------------------------|------------------------------------------------------------|
| `<T>`         | `fromJson(String value, Type typeOfT)`                              | Converts JSON to an object of the specified type.          |
| `<T>`         | `fromReader(Reader json, Type typeOfT)`                             | Converts a JSON stream to an object of the specified type. |
| `<T>`         | `getObject(String key, Type type)`                                  | Retrieves a stored object.                                 |
| `<T>`         | `getParameterized(String key, Type rawType, Type... typeArguments)` | Retrieves a parameterized object.                          |
| `<T> List<T>` | `getList(String key, Type type)`                                    | Retrieves a list of objects.                               |
| `boolean`     | `getBoolean(String key, boolean defValue)`                          | Retrieves a boolean value.                                 |
| `float`       | `getFloat(String key, float defValue)`                              | Retrieves a float value.                                   |
| `int`         | `getInt(String key, int defValue)`                                  | Retrieves an int value.                                    |
| `long`        | `getLong(String key, long defValue)`                                | Retrieves a long value.                                    |
| `String`      | `getString(String key, String defValue)`                            | Retrieves a String value.                                  |
| `String`      | `toJson(Object object)`                                             | Converts an object to a JSON string.                       |
| `void`        | `addDataObserver(DataManager.DataObserver observer)`                | Registers a data change listener.                          |
| `void`        | `remove(String key)`                                                | Removes the stored value.                                  |
| `void`        | `saveBoolean(String key, boolean value)`                            | Stores a boolean value.                                    |
| `void`        | `saveFloat(String key, float value)`                                | Stores a float value.                                      |
| `void`        | `saveInt(String key, int value)`                                    | Stores an int value.                                       |
| `void`        | `saveLong(String key, long value)`                                  | Stores a long value.                                       |
| `void`        | `saveList(String key, List<T> value)`                               | Stores a list of objects.                                  |
| `void`        | `saveList(String key, List<T> value, int maxArraySize)`             | Stores a list of objects with a size limit.                |
| `void`        | `saveObject(String key, Object value)`                              | Stores an object.                                          |
| `void`        | `saveString(String key, String value)`                              | Stores a String value.                                     |

For more methods, refer to
the [documentation](https://jumman04.github.io/DataManager/doc/index.html).

## Contributing

We welcome contributions from the community! If you'd like to contribute, please fork the
repository, make your changes, and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For any issues, please open an issue on GitHub, and we will get back to you as soon as possible.

---

Created with ❤️ by [Jummania](https://github.com/yourusername)
