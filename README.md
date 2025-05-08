# DataManager Library

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
[![](https://jitpack.io/v/Jumman04/DataManager.svg)](https://jitpack.io/#Jumman04/DataManager)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A simple, efficient, and flexible data management library for Java and Android.
Easily store, retrieve, and manipulate data using JSON serialization with support for type safety,
pagination, and file-based persistence.

## Features

- **JSON Serialization/Deserialization**: Easily convert objects to and from JSON format.
- **File-based Data Storage**: Store objects, strings, integers, booleans, lists, and more in local
  storage.
- **Batch Data Handling**: Efficiently save and load lists in batches to optimize memory usage.
- **On Data Change Listener**: Listen to changes in data to update your app in real time.
- **Type-Safe Operations**: Work with strongly-typed objects and use generics for flexibility.
- **Customizable Converter**: **NEW in version 2.8!** You can choose from the following built-in
  converters:
    - **GsonConverter**: Uses the Google Gson library for serialization and deserialization.

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
implementation 'com.github.Jumman04:DataManager:3.1'
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
// Retrieve the full list of fruits (not recommended for very large datasets)
List<String> fruitsFull = dataManager.getFullList("fruits", String.class);

// Retrieve a paginated list of fruits (page 1)
PaginatedData<String> fruitsPage = dataManager.getPagedList("fruits", String.class, 1);

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

| Return Type   | Method Name                                                                    | Description                                                                 |
|---------------|--------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| `String`      | `getRawString(String key)`                                                     | Retrieves the raw JSON string associated with the key.                      |
| `<T>`         | `getObject(String key, Type type)`                                             | Retrieves an object of the specified type.                                  |
| `<T> List<T>` | `getFullList(String key, Type type)`                                           | Retrieves the full list of objects (⚠️ not recommended for large datasets). |
| `<T>`         | `PaginatedData<T> getPagedList(String key, Type type, int page)`               | Retrieves a paginated subset of the list.                                   |
| `String`      | `getString(String key)`                                                        | Retrieves a String value or `null` if not found.                            |
| `String`      | `getString(String key, String defValue)`                                       | Retrieves a String value with a fallback default.                           |
| `int`         | `getInt(String key)`                                                           | Retrieves an int value or 0 if not found.                                   |
| `int`         | `getInt(String key, int defValue)`                                             | Retrieves an int value with a fallback default.                             |
| `long`        | `getLong(String key)`                                                          | Retrieves a long value or 0L if not found.                                  |
| `long`        | `getLong(String key, long defValue)`                                           | Retrieves a long value with a fallback default.                             |
| `float`       | `getFloat(String key)`                                                         | Retrieves a float value or 0.0f if not found.                               |
| `float`       | `getFloat(String key, float defValue)`                                         | Retrieves a float value with a fallback default.                            |
| `boolean`     | `getBoolean(String key)`                                                       | Retrieves a boolean value or `false` if not found.                          |
| `boolean`     | `getBoolean(String key, boolean defValue)`                                     | Retrieves a boolean value with a fallback default.                          |
| `<T>`         | `fromJson(String value, Type typeOfT)`                                         | Converts a JSON string to an object of the specified type.                  |
| `<T>`         | `fromReader(Reader json, Type typeOfT)`                                        | Converts JSON from a `Reader` to an object of the specified type.           |
| `String`      | `toJson(Object object)`                                                        | Converts an object to a JSON string.                                        |
| `Type`        | `getParameterized(Type rawType, Type... typeArguments)`                        | Constructs a parameterized generic type.                                    |
| `void`        | `saveString(String key, String value)`                                         | Saves a String value.                                                       |
| `void`        | `saveInt(String key, int value)`                                               | Saves an int value.                                                         |
| `void`        | `saveLong(String key, long value)`                                             | Saves a long value.                                                         |
| `void`        | `saveFloat(String key, float value)`                                           | Saves a float value.                                                        |
| `void`        | `saveBoolean(String key, boolean value)`                                       | Saves a boolean value.                                                      |
| `void`        | `saveObject(String key, Object value)`                                         | Saves an object.                                                            |
| `<E>`         | `saveList(String key, List<E> value)`                                          | Saves a full list.                                                          |
| `<E>`         | `saveList(String key, List<E> value, int maxArraySize)`                        | Saves a list with a maximum size (for paging).                              |
| `void`        | `appendToList(String key, Object element)`                                     | Appends an element to the end of the list.                                  |
| `void`        | `appendToList(String key, Object element, boolean removeDuplicate)`            | Appends an element, optionally removing duplicates.                         |
| `void`        | `appendToList(String key, int index, Object element)`                          | Inserts an element at a specific index.                                     |
| `void`        | `appendToList(String key, int index, Object element, boolean removeDuplicate)` | Inserts at index with optional duplicate removal.                           |
| `void`        | `removeFromList(String key, int index)`                                        | Removes an element at a specific index from the list.                       |
| `void`        | `remove(String key)`                                                           | Removes the item associated with the key.                                   |
| `void`        | `clear()`                                                                      | Clears all stored data.                                                     |
| `boolean`     | `contains(String key)`                                                         | Checks if a key exists in storage.                                          |
| `void`        | `addDataObserver(DataObserver observer)`                                       | Registers a data observer for change notifications.                         |
| `void`        | `removeDataObserver(DataObserver observer)`                                    | Unregisters a data observer.                                                |

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
