# Testing Plan

## Testing Strategy
Ludora enforces a multi-tier testing pipeline combining automated unit tests, headless game engine simulations, UI composable verification, network disruption tests, and end-to-end integration suites.

## Unit Testing
- Scope: Domain models, utility functions, scoring algorithms, and progression math.
- Framework: JUnit 5, MockK, Truth / AssertJ.
- Coverage Target: 90%+ branch coverage on all business logic modules.
- Fast Execution: Runs within pure JVM without starting Android emulators.

## Game Engine Rule Verification
The game engine is tested headlessly against comprehensive test matrices covering every rule variation:
- Ludo Rule Test Cases:
  - `test_token_release_requires_six`: Verifies tokens in base cannot leave on rolls 1 through 5.
  - `test_token_release_consumes_six`: Confirms token leaves base onto start square on rolling 6.
  - `test_token_advances_exact_rolled_steps`: Validates single token advances from position N to N+roll.
  - `test_token_capture_returns_opponent_to_base`: Verifies landing on unstarred square occupied by an opponent returns the opponent token to base.
  - `test_capture_grants_bonus_roll`: Verifies player receives an extra roll immediately after capturing an opponent piece.
  - `test_starred_safe_square_prevents_capture`: Verifies landing on star tiles co-exists with opponents without capture.
  - `test_home_stretch_turn`: Verifies token properly turns from outer circuit into the colored home path.
  - `test_exact_roll_required_for_home`: Verifies roll exceeding steps to center goal is rejected.
  - `test_three_consecutive_sixes_forfeits_turn`: Validates consecutive six rule when flag is enabled.
  - `test_win_declared_when_all_four_tokens_reach_home`: Validates final match completion and ranking sequence.
- Snake & Ladder Rule Test Cases:
  - `test_ladder_base_transfers_token_to_top`: Verifies landing on ladder base moves piece directly to top cell.
  - `test_passing_over_ladder_does_not_climb`: Confirms token hopping past ladder base continues along standard track.
  - `test_snake_head_drops_token_to_tail`: Verifies landing on snake head moves piece down to tail cell.
  - `test_square_100_exact_win`: Confirms reaching square 100 triggers win condition.
- Remix Rule Test Cases:
  - `test_ludo_circuit_snake_drops_token_back_six_spaces`: Confirms hazard square behavior on Ludo track.
  - `test_power_card_shield_blocks_capture`: Verifies active shield card negates incoming capture attempt.

## UI and Snapshot Testing
- Composable Previews & Tests: Compose UI Test Library (`createComposeRule`).
- Visual Regression: Roborazzi or Paparazzi snapshot testing verifying UI layouts across multiple screen densities and themes.
- Touch Target Auditing: Automated assertions verifying all interactive composables meet 48x48dp minimum bounds.

## Integration Testing
- ViewModel + Repository + Room Database integration tests running on Robolectric or instrumentation.
- Verifies local profile mutations, match history persistence, and quest progression tracking.

## Multiplayer Simulation Testing
- Headless Multi-Client Runner: Spawns 2 to 4 automated virtual clients interacting over local WebSocket loops.
- Simulates complete matches to verify state synchronization, sequence ID ordering, and win declarations under varying latencies.

## Offline Testing
- Zero Connectivity Test Suite: Executes full match runs inside an emulator with network interfaces programmatically disabled.
- Verifies zero thrown unhandled exceptions, zero network timeouts, and zero ad initialization attempts during offline operation.

## Network Interruption Testing
- Simulates sudden packet drops, DNS resolution failures, and switching between Wi-Fi and mobile LTE.
- Verifies that transient socket disconnects trigger smooth retry loops without freezing the Compose rendering thread.

## Reconnection Testing
- Procedure:
  1. Client actively seated in match drops socket.
  2. Match proceeds on server with 15s turn timer.
  3. Client reconnects at second 25 with active match token.
  4. Server sends full state snapshot.
  5. Client reconciles local board coordinates to server state within 500ms.

## Security and Validation Testing
- Malicious Packet Injection: Emits forged `MOVE_TOKEN` and `ROLL_DICE` packets to verify server rejects unearned actions.
- Rate Limit Flooding: Transmits high-frequency emote bursts to confirm automatic socket throttling.
- Verification checks executed in accordance with `11_SECURITY_PLAN.md`.

## Performance and Benchmark Testing
- Macrobenchmark & Microbenchmark libraries measuring:
  - Cold startup time (Target: < 1500ms).
  - Composable recomposition frequency during dice rolls and moves.
  - Frame drop rates during board animations (Target: 0 dropped frames on 60Hz/120Hz displays).

## Device and OS Compatibility Testing
- Matrix testing across physical and virtual devices:
  - OS Versions: Android 8.0 (API 26) through Android 14+ (API 34).
  - Form Factors: 5.5-inch compact phone, 6.7-inch standard phone, and 10-inch tablet.
  - Hardware profiles: Low-end 2GB RAM device, mid-range 4GB device, flagship 8GB+ device.

## Regression Testing Suite
- Automated smoke test suite executed on every pull request prior to merge.
- Validates that newly added Remix modifiers do not alter classic Ludo or Snake & Ladder rule correctness.
