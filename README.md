# EasyLink SDK - Proof of Concept

This repository contains a simple Android project that demonstrates the usage of the EasyLink SDK
to implement basic facilitation of card transactions using the EMV standard.

Currently, only the PAX M50 device is supported.

## Device Communication Libraries
- the EasyLink SDK and other device SDK libraries are not included in this repository
  - see the nested `README.md` file in the `libs/easylink` directory for a complete list of required libraries

## Tech Stack
- Kotlin
    - coroutines
- Android
    - Jetpack Compose
- EasyLink & Neptune libraries (not included in the repository)
