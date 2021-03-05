# Mandiri Movie Database 

This application used for searching movies by:
 
* genre
* release date
* original language
* title keyword

To look for genre feature go to the burger icon and choose discover movies
After that choose genres that you want and click tick icon at the top right

For the youtube trailer just go to detail and media bottom bar

Movies can be saved to: 
* watchlist (online usage)
* local personal lists (possible offline usage)

The information is retrieved from [The Movie Database (TMDb)](https://www.themoviedb.org).

# Main methodologies and libraries used

### General
* MVVM Patern
* [Android Jetpack](https://developer.android.com/jetpack) for a single activity approach
* Parsing JSON from REST API using [Retrofit](https://square.github.io/retrofit/) with coroutines
* [Room Database](https://developer.android.com/training/data-storage/room) for saving movies locally
* View and Data binding
* LiveData
* [Glide library](https://bumptech.github.io/glide/)
* [YoutubePlayer](https://developers.google.com/youtube/android/player)

### UI components
* RecyclerView with StaggeredGridLayout, horizontal and vertical LinearLayouts
* [ExpandableRecyclerView](https://github.com/thoughtbot/expandable-recycler-view)
* DrawerLayout with NavigationView
* BottomNavigationView
* [MultiSlider](https://github.com/apptik/MultiSlider/tree/v1.3)
* SearchView
