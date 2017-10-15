# StreetSmart Cities

![alt text](https://github.com/vontell/StreetSmartCities-Android/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)
(See [Devpost](https://devpost.com/software/streetsmartcities-android) for updates and screenshots.)

## Inspiration

StreetSmart Cities is inspired by the growing industry and use of IoT devices, and is motivated by the fact that not enough has been done to utilize these tools effectively, especially in a way which gets the community involved. StreetSmart Cities is a platform which aims to do this, and includes a chat bot, collaborative work system, and data analytics from IoT APIs which work together to get us closer to the dream of smart cities.

## What it does

SmartCities is centered around collaboration, enabled by big data and cognitive services. City folk can log into the app to view information about their City SmartScore and their own SmartScore. These scores essentially relate to how involved the community, city, and themselves are in using technology to better their environment and make important decisions.

Users can log a task using the Microsoft Bot Service, which asks questions about the issue you want to raise; this may be a crime, advice, repair, etc... The bot will log this task, which other users can now see. The community is now able to work together to better their city.

However, some tasks may require the assistance of public figures and government officials; this is where the Twitter API comes in, which allows users to track, retweet, and apply pressure to their city to get things done. In-app, anyone can share and retweet issues or complaints, which tag local government automatically (for example @Atlantacitygov). This platform therefore ensures that the city takes their constituents into consideration during the city planning process.

However, the platform would be incomplete without the addition of IoT devices. Within both a data analytics section and an interactive map, users can see information about the city's pedestrian traffic, temperature, humidity, and cameras provided by GE Current. This allows the entire town to use these tools in an attempt to further their goal of making their city both safer and cleaner.

To learn more about what it does, I suggest checking out the screenshots above!

## How I built it

I built StreetSmart Cities using the following technologies:

GE Current API
Azure + Bot Services
WRLD3D + AR Maps
AWS through Heroku
Lottie for After Effects
Twitter APIs
Picasso + OkHttp3
Jupiter Notebook
Django
Domain.com (purchased http://streetsmartcity.org, to be linked)
I started by getting a Django RESTful server up and running, and quickly made a few scripts to automatically generate data. Next, I spent time configuring the chat bot and tweaking it to what I was looking for. I then spent a lot of time preparing the basic infrastructure of the app, and making sure that each UI component was just right. After getting Twitter, Lottie, and WRLD3D integrated into the application, I went back to the server to begin some data mining with the GE Current API. After obtaining tons of data on temperature, humidity, and pedestrian behavior, I created endpoints to get that information, and integrated it into my application.