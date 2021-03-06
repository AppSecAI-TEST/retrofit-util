### Global Configuration
```
        new Configuration.Builder()
                .baseUrl("test")
                .connectTimeOut(1000L)
                .readTimeOut(2000L)
                .config();
```

### Api calling using Webconnect
```
        WebParam webParam = WebConnect.with(this,"url")
                .requestParam(new LinkedHashMap<String, Object>())
                .callback(new WebHandler.OnWebCallback() {
                    @Override
                    public <T> void onSuccess(@Nullable T object, int taskId, Response response) {

                    }

                    @Override
                    public <T> void onError(@Nullable T object, String error, int taskId, Response response) {

                    }
                })
                .build();
        WebConnect.connect(Services.class,webParam).friends().subscribe(new Callback<>(webParam));
```
### Interface for service
```
public interface Services {

    @GET("friends")
    Observable<Response<Object>> friends();
}
```

Download
--------
Add the JitPack repository to your root build.gradle:

```groovy
	allprojects {
		repositories {
			maven { url "https://jitpack.io" }
		}
	}
```
Add the Gradle dependency:
```groovy
	dependencies {
		compile 'com.github.amitclickapps:retrofit-util:1.0.2'
	}
```