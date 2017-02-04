## android unique id

save unique id to SharedPreferences and External File System.

## Usage

you should use single instance in your app

```java
    Unique unique = new Unique(getApplicationContext());
    String id = unique.getUniqueId();

```