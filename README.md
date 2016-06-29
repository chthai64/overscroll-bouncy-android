# OverscrollBouncy
An Android library which simulates iOS-like overscroll animation. It use springs mechanism for animating the scrollback. It currently supports RecyclerView with LinearLayoutManager. I'm working on ListView and ScrollView.

### Demo
![Demo](https://github.com/chthai64/overscroll-bouncy-android/raw/master/arts/OverscrollDemo.gif)

### Basic Usage

##### Dependencies
```groovy
dependencies {
    // coming soon
}
```

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
##### Optional params:
* ```tension``` Tension of the spring. It should be set to a high value (ex. 1000) for smooth animation. The default value is 1000.
* ```friction``` Friction of the spring. High friction value will slow down the scroll-back speed. The default value is 200.
* ```gapLimit``` The maximum over-scroll gap size (in dp). The default value is 220dp.
* ```speedFactor``` The higher the speedFactor is, the less the view will utilize the gap limit. Minimum value is 1. The default value is 5.
* ```viewCountEstimateSize``` The number of children views to estimate the content size of RecyclerView (or ListView). The estimation is computed by averaging the children views size then multiply by the total items inside the adapter. The default value is 5.
* ```maxAdapterSizeToEstimate``` The maximum adapter size (number of items in the adapter) that the system will include content size estimation of the RecyclerView (or ListView) in the calculation. The default value is 20.
