# OverscrollBouncy
An Android library which simulates iOS-like overscroll animation. It uses spring mechanism for animating the scrollback. It currently supports RecyclerView with LinearLayoutManager. I'm working on ListView and ScrollView.

## Demo
![Demo](https://github.com/chthai64/overscroll-bouncy-android/raw/master/arts/OverscrollDemo.gif)

## Basic Usage

##### Dependencies
```groovy
dependencies {
    compile 'com.chauthai.overscroll:overscroll-bouncy:0.1.0'
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

## Advanced Usage
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
* ```tension``` Tension of the spring. It should be set to a high value (ex. 1000) for smooth animation.
* ```friction``` Friction of the spring. High friction value will slow down the scroll-back speed.
* ```gapLimit``` The maximum over-scroll gap size (in dp). The default value is 220dp.
* ```speedFactor``` The higher the speedFactor is, the less the view will utilize the gap limit. Minimum value is 1.
* ```viewCountEstimateSize``` (for RecyclerView/Listview) The number of children views to estimate the content size of RecyclerView (or ListView). The estimation is computed by averaging the children views size then multiply by the total items inside the adapter.
* ```maxAdapterSizeToEstimate``` (for RecyclerView/Listview) The maximum adapter size (number of items in the adapter) that the system will include content size estimation of the RecyclerView (or ListView) in the calculation.
 
## Credits
[Rebound](http://facebook.github.io/rebound/) library.

## License
```
 The MIT License (MIT)

 Copyright (c) 2016 Chau Thai

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
```
