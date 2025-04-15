# Car Rental Speed Monitor

A lightweight Android Automotive application that monitors a renter's vehicle speed and alerts the
rental company if the speed exceeds a fleet-defined limit. 

## Use Case

A car rental company wants to:

- Set a maximum speed limit per renter before the rental begins.
- Monitor vehicle speed in real-time during the rental.
- Alert the company if the speed exceeds the limit.
- Optionally, alert the driver within the vehicle system.
- Support Firebase** or AWS as backends for alert communication.

##  Architecture

- No UI or MVVM — a clean, self-contained MainActivity.kt handles all logic.
- Utilizes Android Automotive’s CarPropertyManager to read real-time vehicle speed via PERF_VEHICLE_SPEED.
- Renter-specific speed limits are defined using an in-memory configuration map (speedLimitMap) that simulates input from a fleet management system.
- The app tracks speed changes and ignores repeated values to reduce noise.
- When the vehicle speed exceeds the configured limit for the renterThe rental company is alerted via a backend call (currently stubbed; designed to integrate with Firebase or AWS).
- The driver is warned inside the vehicle (currently simulated with a log message; designed for future integration with an in-vehicle audio/visual alert system or HMI display).

## Future Enhancements (Planned)
- Replace MainActivity with a background Service that runs independently of any UI.
- Register a BroadcastReceiver that starts the monitoring service on boot, allowing the app to work completely in the background — no user interaction required.
- Store renter configurations (e.g., speed limit) dynamically from cloud backends (Firebase/Firestore or AWS).



