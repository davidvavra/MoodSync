# MoodSync for LIFX®
Simple app which mirrors the screen of Android TV to LIFX® lights.

## Install
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=cz.destil.moodsync)

## See it in action
<a href="http://www.youtube.com/watch?feature=player_embedded&v=Gwom0uEm9gc
" target="_blank"><img src="http://img.youtube.com/vi/Gwom0uEm9gc/0.jpg" 
alt="IMAGE ALT TEXT HERE" width="240" height="180" border="10" /></a>

## How does it work?
 - it periodically captures screen using the new Lollipop [Media Projection API](https://developer.android.com/reference/android/media/projection/package-summary.html)
 - it extracts vibrant color from the image using [Palette library](https://developer.android.com/tools/support-library/features.html)
 - it sends the vibrant color to all lights on the same WiFi using [LIFX SDK](https://github.com/LIFX/lifx-sdk-android)
 - it is designed for Android TV, but works on Lollipop phones and tablets too
 
## Issues, feature requests?
 
Please add them all to [issues](https://github.com/destil/MoodSync/issues). Pull requests are welcome!
 
## How to build the code
 
Import it to [Android Studio](http://developer.android.com/sdk/index.html) or:
 
```
./gradlew assembleDebug
```

Author
-----
- [David 'Destil' Vávra](http://www.destil.cz)
- [Follow me on G+](http://google.com/+DavidVávra) to be notified about future versions
