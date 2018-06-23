# Private Calendar Scheduling

## Introduction

This project features a fully functional prototype for decentralized, privacy-preserving appointment scheduling based on secure multi-party computation (MPC). It was developed as part of my master thesis *Interactive Private Multi-Party Calendar Scheduling* at RWTH Aachen University, chair i5 Prof. Stefan Decker, during winter 2017/18. Special thanks go to my advisor Benjamin Heitmann, Ph.D., who provided ongoing support to make this work possible.

## Goal

The goal was to design and implement an easy-to-use application that allows a number of users to agree on a common appointment date without disclosing information on their personal timetables, neither towards other participants nor towards a third party. In particular, the following requirements needed to be fulfilled:

* **Privacy**: The implemented system does not rely on a trusted central server and allows users to find a date for an appointment while keeping their timetables private from the others.
* **Scalability**: It is necessary that the scheduling process remains interactive even for a large number of participants. As a reference, organizing meetings for up to 30 attendees should be supported.
* **Usability**: The application exposes a clean and intuitive graphical user interface and integrates into established calendar software such as *Microsoft Outlook*, *Thunderbird Lightning* and *Calendar* on Mac operating systems.
* **Extensibility**: The underlying protocol is designed for extensibility and does not impose unnecessary restrictions regarding its use case. In particular, the scheduling scheme, i.e. the algorithm which decides on an appointment date dependent on the user's private inputs, should be easily customizable.

## General Approach

This prototype relies on secure multi-party computation (MPC), which enables users to collaboratively evaluate the scheduling functionality expressed in boolean logic. The inputs of each party, namely information whether certain time slots are free or unavailable, are encrypted by means of secret sharing and therefore remain private. Prior to the secure computation, a public session setup phase allows for discovery of potential participants as well as to agree on common protocol parameters. iCalendar files are used as a standardized way for interoperability with external calendar software.

A more extensive description of the conceptual approach, insights regarding the implementation as well as evaluation results can be found in the written thesis.

## Build Instructions

The application is fully written in Java and can be built via Maven, requiring Java 8 in order to run. It uses [*SCAPI*](https://github.com/cryptobiu/scapi) as an MPC framework and [*biweekly*](https://github.com/mangstadt/biweekly) for parsing iCalendar files (see below for third-party licenses). Note that the provided Maven configuration is specifically tailored to 64-Bit Windows operating systems and was only tested under Windows 7 and 10. Since *SCAPI* relies on native code, maintaining platform independence would have come with additional effort. However, it should be possible in general to also build the project under Linux and Mac. Please refer to the [*SCAPI* building instructions](http://scapi.readthedocs.io/en/latest/install.html) for more information.

## License

This project is published under the [MIT license](LICENSE.md). Licenses of the used third-party libraries [*SCAPI*](https://github.com/cryptobiu/scapi/blob/master/LICENSE.md) and [*biweekly*](https://github.com/mangstadt/biweekly/blob/master/LICENSE) can be found on the respective project pages.
