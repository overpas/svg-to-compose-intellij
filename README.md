# svg-to-compose-intellij
[![Detekt](https://github.com/overpas/svg-to-compose-intellij/actions/workflows/detekt.yml/badge.svg)](https://github.com/overpas/svg-to-compose-intellij/actions/workflows/detekt.yml)
[![Test](https://github.com/overpas/svg-to-compose-intellij/actions/workflows/test.yml/badge.svg)](https://github.com/overpas/svg-to-compose-intellij/actions/workflows/test.yml)

A simple Android Studio plugin to generate Jetpack Compose ImageVector icons. In fact, it's a wrapper around the [svg-to-compose](https://github.com/DevSrSouza/svg-to-compose) tool.

## Installation
Install from [Jetbrains Marketplace](https://plugins.jetbrains.com/plugin/18619-svg-to-compose)

## Usage
1. Open up a Jetpack Compose project 
2. Right click on a kotlin/java directory 
3. Select "Compose ImageVector" at the bottom 
4. Enter the necessary info to generate compose ImageVector icons: accessor name, vector image type, vector images directory, all assets property name. The output directory should be configured automatically.
