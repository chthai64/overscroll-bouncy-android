# OverscrollBouncy
An Android library which simulates iOS-like overscroll animation. It use springs mechanism for animating the scrollback. It currently supports RecyclerView with LinearLayoutManager. I'm working on ListView and ScrollView.

### Demo
![Demo](https://github.com/chthai64/overscroll-bouncy-android/raw/master/arts/OverscrollDemo.gif)

### Basic Usage

#### Dependencies
```groovy
dependencies {
    // coming soon
}
```

#### Layout file
##### RecyclerView
```xml
<com.chauthai.overscroll.RecyclerViewBouncy
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

##### ListView, ScrollView
coming soon.

### Advanced Usage
```xml
<com.chauthai.overscroll.RecyclerViewBouncy
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"

    app:tension="1000"
    app:friction="200"

    app:gapLimit="220"
    app:speedFactor="5"
    app:viewCountEstimateSize="5"
    app:maxAdapterSizeToEstimate="20"/>
```

* ```tension``` Tension of the spring. It should be set to a high value (ex. 1000) for smooth animation.
