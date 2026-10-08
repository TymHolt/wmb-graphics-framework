# wmb-graphics-framework

The graphics framework for the [World Map Builder](https://github.com/TymHolt/world-map-builder) Project.

## Install

To use this library for other projects, use **Maven**. Run
> mvn install

to have it built and installed as a usable dependency. This library ships a fat JAR, thus you do not need any other
dependencies to use it in your own application.

```XML
<dependencies>
    <dependency>
        <groupId>org.wmbgf</groupId>
        <artifactId>wmb-graphics-framework</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Build

This project can be built using **Maven**. Run
> mvn clean package

## Third-Party Licenses
This project uses [LWJGL](https://www.lwjgl.org/) and [JOML](https://github.com/JOML-CI/JOML/tree/main).
**LWJGL** is licensed under the *BSD 3-Clause License*, **JOML** under the *MIT License*. The project also includes
third-party  components distributed with these dependencies. Their respective license texts are included in the
`licenses/` directory.

