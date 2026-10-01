# Performance Plan

## Performance Targets
Ludora maintains strict performance budgets to guarantee fluid gameplay across all supported Android hardware tiers:
- Target Frame Rate: Consistent 60fps (or 120fps on high-refresh-rate panels) with zero frame drops during token hops and dice rolls.
- Maximum Cold Startup Time: < 1500ms on mid-range devices; < 2500ms on budget Android devices.
- Maximum Memory Footprint: < 120MB active heap size during board matches.
- Thermal and Battery: Zero CPU throttling during continuous 30-minute play sessions.

## Startup Performance
- Baseline Profiles: Generate and bundle Android Baseline Profiles to compile hot code paths ahead of time (AOT) via AndroidX Benchmark.
- Lazy Initialization: Defer non-critical subsystems (audio synthesis, achievement evaluators, ad providers) until after the first frame renders.
- Composable Loading: The initial Home Dashboard renders immediately using cached local profile state without blocking on disk scans.

## Memory Management and Leak Prevention
- LeakCanary integration in development builds to detect retained ViewModel or Activity references.
- Efficient Image and Asset Caching: Vector graphics (XML/SVG) preferred over dense raster bitmaps. Raster assets (textures and avatars) sized to exact display dimensions.
- Bitmap Recycling: Clear off-screen board caches when navigating out of the match shell.

## Battery Consumption Optimization
- Idle Power Conservation: When the board awaits user input, frame rendering drops to 0fps (no continuous redraw loop).
- Background Pause: The game engine immediately suspends coroutine dispatchers and releases audio focus when the app moves to the background.
- Hardware Acceleration: Utilize hardware-accelerated Canvas drawing for Compose layout passes.

## UI and Board Rendering Performance
- Recomposition Scoping:
  - Isolate mutable states (e.g. dice roll countdown, active token highlights) into dedicated composable leaf nodes.
  - Avoid passing entire raw match states into sub-components; pass derived primitive values or stable UI state wrappers.
- Custom Draw Modifiers: Board circuit squares and track lines draw directly onto a cached Canvas backing layer (`Modifier.drawBehind`) rather than instantiating hundreds of nested Box composables.

## Animation Frame Budgeting
- Frame Budget: 16.6ms per frame (or 8.3ms on 120Hz displays).
- Layout Calculation Avoidance: Animations interpolate transform properties (`graphicsLayer` translation and scale) instead of modifying layout constraints (such as `padding` or `offset`).
- Particle Throttle: Particle systems (confetti, capture shockwaves) enforce a hard limit of 30 simultaneous particles with early recycling.

## Storage Footprint
- APK Size Budget: < 25MB initial download size.
- Database Footprint: Prune match history logs locally to retain the last 50 matches, archiving aggregate statistics into the player profile.
- Compact Serialization: Use Protobuf or compact JSON serialization to minimize disk writes.

## Network Bandwidth Usage
- Minimal Packet Payloads: In-game state delta packets stay under 256 bytes per event.
- Throttled Heartbeats: Pings transmit at 5-second intervals, consuming negligible cellular bandwidth.
- Delta Updates: Clients receive incremental updates during active matches rather than full board snapshots.

## Offline Performance Profiling
- Benchmark cold starts, local AI move evaluations, and Room database transactions while device network interfaces are completely disabled.
- Automated CI benchmark runs ensure offline latency never regresses.

## Low-End Android Device Optimization
- Target Configuration: Tested against devices with 2GB RAM running Android 8.0 (API 26).
- Performance Fallbacks:
  - Reduced particle count on devices reporting low RAM.
  - Simplified shadow rendering (replacing multi-pass Gaussian blur with flat vector outlines).
  - Audio asset compression to reduce memory footprint.
