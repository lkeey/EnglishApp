# Engilize

[![CodeFactor](https://www.codefactor.io/repository/github/lkeey/englishapp/badge)](https://www.codefactor.io/repository/github/lkeey/englishapp)

Engilize is an Android application in which you can learn English by speaking absolutely any language.

The idea is that you choose your own learning trajectory

## Project characteristics and tech-stack

<img src="https://raw.githubusercontent.com/lkeey/EnglishApp/master/app/src/main/res/drawable/app_logo_large.png" width="336" align="right" hspace="20">

* Storage of information
  * Firebase Firestore - storing user data
  * Firebase Storage - storing profile photos
  * Room Database - saving the studied words
  * SharedPreferences - storing simple data (word counter, wallpaper change status)
* Authorization
  * One-Tap Google Sign In
  * Firebase Authentication
* Layouts
  * LinearLayout
  * ConstraintLayout
  * CollapsingLayout
  * FrameLayout
  * RefreshLayout
  * RecyclerView
  * ScrollView, NestedScrollView
* Google Speech Services - text recognition and pronunciation
* ML Kit Translate - translate words from all languages
* Retrofit - send GET and POST requests
* GSON - parsing requests
* Instabug - crash tracking
* Fragment, BottomSheetFragment - using multiple screens inside activity
* Adapters - adding multiple items to RecyclerView
* Google Map - sdk to show map
* Layers architecture
* Material design
* GitHub Actions

## Architecture
The entire application follows `layers architecture`.

<img src="https://raw.githubusercontent.com/lkeey/EnglishApp/master/app/src/main/res/drawable/layer.png" width="700" hspace="5" vspace ="10">

## Availability
You can download the app from Google Disk

<img src="https://raw.githubusercontent.com/lkeey/EnglishApp/master/app/src/main/res/drawable/qr.png" width="700" hspace="5" vspace ="10">

