# Command-Location
This library provide easy access to user location using fuse location provider.You create your location request and pass it to this library funtion and you receive async callback with location. Even you can get location update in non activity context too without worrying about runtime permission.
  
# Motivation

Getting location update from google play service take lots of boilerplate code.

  *  Check Runtime permission, must have activity context
  *  Handle location callback in fuse location provide.
  *  Release location related resource on stopping location update.
  *  Check if location services are on or off
  *  Getting last known location is not easy either.
  *  Fallback to last known location if not getting location after certain duration.

This library solve all these problem with minimun piece of code

  * Receive location callback in sync logic( as in, Async location update can be expressed in series of sync method calls).
  * Don't have worry about runtime permission.
  * Don't have worry about location setting, in case provider not enable this open location setting screen.
  * Fallback time for last know location, in case app is not able to get current location. And you don't have handle it in seperate piece of code. you receive last know location in save callback so no more boilerplate code.
  * Handle location request setting.
  * Can access location in non activity context without worrying about runtime permission.
  

# Getting started

## Installing 
To use this library simply import it by placing the following line under dependencies in your app module's build.gradle file

This library is posted in jCenter

#### Gradle
```
implementation 'in.unicodelabs.location:command-location:1.0.0'
```

#### Maven
```
<dependency>
  <groupId>in.unicodelabs.location</groupId>
  <artifactId>command-location</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

# Usage

Create location request according to your needs

```
LocationRequest locationRequest = new LocationRequest()
        .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        .setInterval(2000)
        .setFastestInterval(1000);
```

now create command location builder and set location request to it

```
CommandLocation.Builder builder =  new CommandLocation.Builder(this);
        builder.setLocationRequest(locationRequest);
        builder.setRequestMode(CommandLocation.CommandMode.SINGLE);
        builder.setFallBackToLastLocationTime(3000);
        builder.setLocationResultCallback(new LocationResultCallback() {
            @Override
            public void onLocationReceived(Location location) {
                Toast.makeText(MainActivity.this,location.getLatitude()+","+location.getLongitude(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void noLocationReceived() {
                Toast.makeText(MainActivity.this,"no location received",Toast.LENGTH_SHORT).show();
            }
        });
        builder.start();
```

**Request Single loction update like this**
```text
builder.setRequestMode(CommandLocation.CommandMode.SINGLE);
```

**Or multiple location update like this**
```text
 builder.setRequestMode(CommandLocation.CommandMode.REGULAR_UPDATE);
```

**Set the fall of time for last know location like this**
```text
 builder.setFallBackToLastLocationTime(3000);
```

**Set LocationResultCallback to builder and receive call back on location update.**
 ```text
 builder.setLocationResultCallback(new LocationResultCallback() {
            @Override
            public void onLocationReceived(Location location) {
                //Logic with user current location
            }

            @Override
            public void noLocationReceived() {
                //logic in case app is not able to find user current location or last know location
                //Show some error message
            }
        });
```

**finally to start the location update call start().**
```text
builder.start();
```

# Author
  * **Saurabh K Sharma - [GIT](https://github.com/Sorbh)**
  
      I am very new to open source community. All suggestion and improvement are most welcomed. 
  
 
## Contributing

1. Fork it (<https://github.com/sorbh/KdLoadingView/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request


# License

```
Copyright 2018 Saurabh Kumar Sharma

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
