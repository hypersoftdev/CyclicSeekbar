# Cyclic Seekbar Library
This library provides an intuitive and customizable cyclic seekbar for Android apps. By integrating it into your project, you can offer users a circular slider to adjust values seamlessly within a defined range.

# Key Features:
1. Easy Integration: Simply add the library to your project and start using the cyclic seekbar immediately.
2. Highly Customizable:  Modify its appearance, size, and behavior to fit your app’s design and functionality.
3. Smooth Circular Control: Provides a fluid, continuous control experience for users, perfect for volume, brightness, or other circular value adjustments.

# Contributing

We welcome feedback, and code contributions!❤️

# Step-by-Step Usage: 

## Dependency Addition
To use the cyclic seekbar, add it to your project by updating your Gradle files:


## Gradle Integration

### Step A: Add Maven Repository

In your project-level build.gradle or settings.gradle file, add the required repository:
```
repositories {
    google()
    mavenCentral()
    maven { url "https://jitpack.io" }
}
```  

### Step B: Add Dependencies

Next, include the cyclic seekbar library in your app-level build.gradle file. Replace x.x.x with the latest version:
```
implementation 'com.github.hypersoftdev:CyclicSeekBar:1.0.0'

```


## XML Integration

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
    app:cKnobDrawable="@drawable/ic_drawable"  //use your own drawable  
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


## Implementation

### Kotlin Example:

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
        cyclicSeekBar.setOnStateChanged(object : CyclicSeekbar.OnStateChanged {progress->
            Toast.makeText(this, "Progress: $progress", Toast.LENGTH_SHORT).show()
        }
    }
}
```



