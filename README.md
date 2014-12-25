![logo](https://github.com/stanleyrya/afterglow-figment/raw/master/logo.png)

Description
==========

Our goal is to create a real-time long-exposure mirror. Long-exposure photography combines stationary elements with moving elements into one image. Stationary elements are clear while objects in motion look blurred or stretched. Normally this is achieved with a long-duration shutter speed, however in our implementation we will be using a camera, computer, and projector. The camera will pick up bright light which will be processed by the computer before being projected. After a short period of time, the light will fade back to blackness. We will use the dark surroundings to our advantage to only display bright lights such as phone screens, glow sticks, glow-in-the-dark blocks, and toy lightsabers.

All passerbyes will be able to interact with our “mirror” and work together to create pieces of art. Use of phones and New Year’s light toys will be encouraged, and we will also have devices for the public to use. These devices range from glow in the dark blocks that kids can build (and subsequently knock down) to toy lightsabers.

Installation Instructions
=========================

Currently, we've only tested this project on OS X, but it should be possible to make it run on other operating systems without too much work. To install it on a Mac, you will need [Homebrew](http://brew.sh/ "Homebrew — The missing package manager for OS X").

Once you have homebrew, run the following commands in Terminal to install OpenCV, a necessary library for the project:
```
brew tap homebrew/science
brew install opencv --with-java
```

Then, clone the Github repo, and open the project in Eclipse. Build it and run. All the other libraries are already in the classpath, so it should work right away.
