# Sky

## Insight

Sky is an android application that was created with idea of improving understanding of android development. It is a weather forecast type application that uses [API](#notes) to retrieve forecast data.

## UI

### City Search

At first user will be greeted with view to search city for forecast. Here user can use one of two ways to select location for forecast. Using button to enable [GPS](#geolocation) location and use latitude with longitude or start typing name of desired city in search view and then selecting one of suggested cities.
(See [Notes](#notes) for more details)

### Forecast

When user has selected the city - app will navigate itself to forecast fragment where user can see selected city name and weather forecast with 3 hour gap. User will see time, weather icon, rain icon, rain volume in millimeters, minimal and maximal temperatures. And if user wants to see more details at each instance of the time he can click on `Expand` button or individual timestamp to display hidden forecast data. Then user will be able to see Pressure, Humidity, Wind and Visibility forecast.
(See [Features](#features) for more details)

### Side Navigation Menu

In side Navigation menu user can select the city to display the forecast for. On each available forecast selection user will see the name of selectable city, miniature icon displaying weather for next 3 hour cycle, "feels like" temperature and [Pinned](#pinning-cities) city star in either system UI or cyan color. At the most bottom user will see Settings button with gear icon that will take user to [Settings](#Settings) fragment.
(See [Features](#features) for more details)

### Settings

Here user will be able to set [Theme](#themes) of the application and select which [units of temperature](#units-of-temperature) to use.
(See [Features](#features) for more details)

### Top Navigation Menu

Here User will only see two Image Buttons. Burger button (on the left) will expand [Side Navigation Menu](#side-navigation-menu). Focus button (on the right) will take user to [City Search](#city-search) fragment.

## Features

### Themes
As mentioned before in [Settings](#settings) fragment user will be able to set the theme of the application.
There are are two options:

* `Use Custom Mode?` which if enabled will allow user to set `Dark Mode?` option, and if disabled will force application to follow devises theme.
* `Dark Mode?` is only available to be checked if `Use Custom Mode?` is checked. It will set applications theme to be independent from devises theme. If the setting is checked it will force application to use Dark mode as default, and if the setting is not checked application will use Light mode.

### Units of temperature

Also in [Settings](#settings) fragment user is able to chose whether to use Celsius or Fahrenheit as units of temperature.

### Pinning cities

As mentioned before in [Side Navigation Menu](#side-navigation-menu) menu user will be able to `Pin` any city in the list. This is done by clicking Star icon at the far right of element. If the star is outlined in Cyan color the city is `Pinned`. `Pinned` city will be displayed at start of application. If no city is `Pinned` (all stars are system UI color) - first city in the list will be displayed.

### Minimization

If application is minimized - currently displaying fragment will be saved and displayed if user returns to the application.

## Usage

### Network
In order to use the applications forecast - device needs to bo connected to `valid` network (Meaning that there is connection and access to the internet).

### Geolocation

In order to use geolocation as method of searching for city user will be asked (if not already granted) to grant the `geolocation` permission. Also if GPS is turned off user will be asked if he wants to navigate to GPS settings and turn them on.

### Forecast

User can refresh forecast data in already displaying city by dragging down the layout.

Unfortunately due to API provided policies it is only possible for application to retrieve forecast data with 3 hour gap but 16 times, witch means that forecast is shown for 2 days from refreshing.
Also worth noticing that temperature units mentioned in [Settings](#settings) fragment will only be changed when user refreshes forecast data.
## Notes
* Forecast data is provided by [OpenWeatherMap](https://openweathermap.org) free API.
* Searchable cities are provided from modified file available at [kaggle](https://www.kaggle.com).  
    (There are 44_691 cities to chose from)

## Post Scriptum
This application was not developed for normal use. It was a learning material. There are diffidently some bugs that I have not discovered left, and there is no guarantee that I Will fix them due to the project already counts as `finished` (but if I find them entertaining i might fix them).
Also there are still some features that can be implemented such as: 

* Different landscapes
* Theme following logic
* More coloring
* Animations
* etc.

but I will not implement them due to them being out of the learning scope for this project.
Overall this application turned out a lot better than I first imagined.