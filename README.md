## android unique id

save unique id to SharedPreferences and External File System.

## Usage

```gradle
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

then

```gradle
dependencies {
	        compile 'com.github.xesam:android-unique:0.1'
	}
```

you should use single instance in your app

```java
    Unique unique = new Unique(getApplicationContext());
    String id = unique.getUniqueId();

```