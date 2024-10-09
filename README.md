# CyclicSeekbar
**CyclicSeekbar** provides an intuitive and customizable cyclic seekbar for Android apps. By integrating it into your project, you can offer users a circular slider to adjust values seamlessly within a defined range.

## Key Features:
1. **Easy Integration**: Simply add the library to your project and start using the cyclic seekbar immediately.
2. **Highly Customizable**: Modify its appearance, size, and behavior to fit your app’s design and functionality.
3. **Smooth Circular Control**: Provides a fluid, continuous control experience for users, perfect for volume, brightness, or other circular value adjustments.


## Step-by-Step Usage:

### 1. Dependency Addition

To use the cyclic seekbar, follow these steps to update your Gradle files.

#### Gradle Integration

##### Step A: Add Maven Repository
In your **project-level** `build.gradle` or `settings.gradle` file, add the following repository:

```
repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```

### Step B: Add Dependencies

Include the cyclic seekbar library in your **app-level** `build.gradle` file. Replace `x.x.x` with the latest version: [![](https://jitpack.io/v/hypersoftdev/CyclicSeekbar.svg)](https://jitpack.io/#hypersoftdev/CyclicSeekbar)

```
implementation 'com.github.hypersoftdev:CyclicSeekbar:x.x.x'
```

### 2. XML Integration

To integrate Cyclic SeekBar into your layout, use the following XML structure with customizable attributes:

```
<com.hypersoft.cyclicseekbar.CyclicSeekbar
    android:id="@+id/cyclic_seekbar"
    android:layout_width="100dp"
    android:layout_height="100dp"
    app:cAnimation="false"
    app:cAnimationBounciness="1"
    app:cAnimationSpeed="5"
    app:cBalloonValuesAnimation="scale"
    app:cBalloonValuesRelativePosition="0"
    app:cBalloonValuesSlightlyTransparent="true"
    app:cBalloonValuesTextSize="1dp"
    app:cBalloonValuesTimeToLive="0"
    app:cBorderWidth="0dp"
    app:cClickBehaviour="nothing"
    app:cDefaultState="0"
    app:cEnabled="true"
    app:cFreeRotation="true"
    app:cIndicatorWidth="0dp"
    app:cKnobDrawable="@drawable/ic_drawable"  <!-- Use your own drawable -->
    app:cKnobDrawableRotates="true"
    app:cNumberOfStates="50"
    app:cSelectedStateMarkerColor="@color/black"
    app:cSelectedStateMarkerContinuous="true"
    app:cShowBalloonValues="false"
    app:cStateMarkersAccentColor="@color/green"
    app:cStateMarkersAccentPeriodicity="1"
    app:cStateMarkersColor="@android:color/transparent"
    app:cStateMarkersRelativeLength="0"
    app:cStateMarkersWidth="1dp"
    app:cSwipe="circular"
    app:layout_constraintTop_toTopOf="parent"
    tools:cStateMarkersAccentColor="#B4B6C0"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"/>
```


### 3. Implementation

#### Kotlin Example:

```
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupCyclicSeekBar()
    }

    private fun setupCyclicSeekBar() {
        val cyclicSeekBar = findViewById<CyclicSeekBar>(R.id.cyclicSeekBar)
        cyclicSeekBar.setOnStateChanged(object : CyclicSeekbar.OnStateChanged {
            override fun onStateChanged(progress: Int) {
                Toast.makeText(this@MainActivity, "Progress: $progress", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
```

## Screen Demo

![Demo](https://github.com/hypersoftdev/CyclicSeekbar/blob/master/screens/screen1.gif?raw=true)

# Acknowledgements

This work would not have been possible without the invaluable contributions of **Muhammad Asif**. His expertise, dedication, and unwavering support have been instrumental in bringing this project to fruition.

![Profile](https://github.com/hypersoftdev/CyclicSeekbar/blob/master/screens/profile_image.jpg?raw=true)

We are deeply grateful for **Muhammad Asif** involvement and his belief in the importance of this work. His contributions have made a significant impact, and we are honored to have had the opportunity to collaborate with him.

# LICENSE

Copyright 2023 Hypersoft Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
