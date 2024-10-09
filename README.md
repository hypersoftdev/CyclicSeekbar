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

### 2. Attribute Summary

<table>
  <tr><th colspan="3">Attribute Summary</th></tr>
  <tr><th>Attribute</th><th>Format</th><th>Description</th></tr>
  <tr><td>cAnimation</td>
      <td>boolean</td>
      <td>Enable / disable indicator's animation.</td></tr>
  <tr><td>cAnimationBounciness</td>
      <td>int</td>
      <td>Parameter "bounciness" applied to the spring physical model for the indicator's animation.</td></tr>
  <tr><td>cAnimationSpeed</td>
      <td>int</td>
      <td>Parameter "speed" applied to the spring physical model for the indicator's animation.</td></tr>
  <tr><td>cBalloonValuesAnimation</td>
      <td>string</td>
      <td>Animation. Choose among *fade*, *pop* or *scale*.</td></tr>
  <tr><td>cBalloonValuesRelativePosition</td>
      <td>int</td>
      <td>Relative position of the balloons. 0 = center, 1 = edge. Values >1 are allowed.</td></tr>
  <tr><td>cBalloonValuesSlightlyTransparent</td>
      <td>boolean</td>
      <td>When true, the balloons will be 75% visible.</td></tr>
  <tr><td>cBalloonValuesTimeToLive</td>
      <td>int</td>
      <td>How long the popup balloons display. 0 = permanent.</td></tr>
  <tr><td>cBorderWidth</td>
      <td>dimension</td>
      <td>Width of the external circle. 0 = disable.</td></tr>
  <tr><td>cClickBehaviour</td>
      <td>string</td>
      <td>What is expected when the seekbar is clicked. Options: next value, previous value, reset to default value, let the user select with a popup menu, or define a custom listener. Default: next.<br>
          Warning: the popup menu is available only with Compat.<br>
          Warning: the custom listener (a Runnable) should be defined runtime, with setUserBehaviour().</td></tr>
  <tr><td>cDefaultState</td>
      <td>int</td>
      <td>The starting state of the seekbar.</td></tr>
  <tr><td>cEnabled</td>
      <td>boolean</td>
      <td>Enable / disable seekbar.</td></tr>
  <tr><td>cFreeRotation</td>
      <td>boolean</td>
      <td>Enable free rotation. When false, after reaching maximum or minimum, the indicator will stop; when true, the value will continue in a round-robin fashion. Default: true.</td></tr>
  <tr><td>cIndicatorWidth</td>
      <td>dimension</td>
      <td>Width of the line indicator. 0 = disable.</td></tr>
  <tr><td>cKnobDrawable</td>
      <td>drawable</td>
      <td>Allows overriding the color configuration to set a drawable as the knob graphics.<br>
          If present, both kKnob* and kKnobCenter* attributes will be ignored.</td></tr>
  <tr><td>cKnobDrawableRotates</td>
      <td>boolean</td>
      <td>When true, the drawable will be rotated accordingly; otherwise, it will stay still.</td></tr>
  <tr><td>cNumberOfStates</td>
      <td>int</td>
      <td>Number of possible states. States are numbered from 0 to n-1. This number can be changed runtime, and the indicator will adjust its position accordingly.</td></tr>
  <tr><td>cSelectedStateMarkerColor</td>
      <td>color</td>
      <td>Color of the selected line marker.</td></tr>
  <tr><td>cSelectedStateMarkerContinuous</td>
      <td>boolean</td>
      <td>If continuous mode is chosen, the knob will act like a gauge, selecting all the markers from the minimum to the current value. When false, only one marker is selected at any time.</td></tr>
  <tr><td>cShowBalloonValues</td>
      <td>boolean</td>
      <td>Enable popup balloon values.</td></tr>
  <tr><td>cStateMarkersAccentColor</td>
      <td>color</td>
      <td>Change accent color.</td></tr>
  <tr><td>cStateMarkersAccentPeriodicity</td>
      <td>int</td>
      <td>How often these markers are shown. 0 = disable.</td></tr>
  <tr><td>cStateMarkersColor</td>
      <td>color</td>
      <td>Color of the line markers.</td></tr>
  <tr><td>cStateMarkersRelativeLength</td>
      <td>int</td>
      <td>Length of the line markers, relative to the largest possible circle inside the view.<br>
          1 = draw from edge to center, 0.5 = draw half length starting from the edge.</td></tr>
  <tr><td>cStateMarkersWidth</td>
      <td>dimension</td>
      <td>Width of the line markers.</td></tr>
  <tr><td>cSwipe</td>
      <td>string</td>
      <td>Enable swipe. Values: off, vertical, horizontal, both, or circular (default: circular).</td></tr>
</table>



### 3. XML Integration

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


### 4. Implementation

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
