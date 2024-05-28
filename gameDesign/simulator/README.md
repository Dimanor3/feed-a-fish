# May 27 2024 Rules of Fish Game:

- Fish have stats:
  - minWeight: uniform 10.0-30.0
  - maxWeight: uniform 75.0-100.0
  - weight: uniform minWeight+5 to maxWeight-5
  - gainWeightHungerLevel: uniform int 0-5
  - loseWeightHungerLevel: uniform int 18-24
  - hunger: uniform int gainWeightHungerLevel + 2 to loseWeightHungerLevel - 3

Two things happen to affect a fish:

- It can be fed by click arbitrary times
  - hunger--
  - if hunger < gainWeightHungerLevel, weight++
  - if weight now > maxWeight or < minWeight:
    - kill fish
- Every hour (offset within hour is unknown, changes per instance):
  - the fish gets +1 hunger
  - IF hunger > "loseWeightHungerLevel":
    - Fish loses weight: 1 + floor((hunger - loseWeightHungerLevel) / 5)
  - At this time, enforce mortal obesity:
    - If weight > max weight or < min weight:
      - kill fish

---

Questions:

- How many fish are doomed to die on birth if not fed immediately?
- Without knowing how hungry the fish is on birth, how much do you have to feed it to make it not die?
- How long can a fish be made to live without refeeding?
- How many clicks definitely kill a fish?
- Is there any safe number of clicks to feed a fish in general?

---

On Hunger:

- Affects weight only, does not directly kill.
- Lower is better fed, higher is hungrier.
  - Unbounded (!)
- Stable weight hunger range varies from 13 to 24 units across.
  - ... so every fish can be kept at same weight given feedings every 12 hours, but almost no fish can be kept stable given feedings every 24 hours.

On Weight:

- Initial weight varies randomly across range of 35 to 80 units, depending.
  - ... so pretty random.
- Livable weight range varies the same: 35 to 80 units across.
  - ... so all fish can be safely allowed to fluctuate at least 34 units in weight.
  - ... so fattening a fish up can keep it alive and stable for at least 2 feedings, although combined with the stable weight hunger range, at least 3 feedings.
    - BAD CONCLUSIONS BELOW: I FORGOT ABOUT THE floor((hunger - loseWeightHungerLevel) / 5) PORTION OF LOSING WEIGHT.
      - This accellerates fish weight loss substantially.
    - The hardiest fish have a stable weight range of 24 units and at least 79 safe units of weight variation for 103 hours of unattended life, or over 8 feeding periods of 12 hours (4 days).
    - The weakest fish have a stable weight range of 13 units and 34 safe units of weight variation for 47 hours of unattended life, or over 3 feeding periods of 12 hours (1.5 days).
      - Takeaway: be careful trying to make a fish last 4 feeding periods unattended, becuase the weakest of fish cannot quite make it 4 periods unfed.

The weight loss rule:

- The weight loss formula every hour seems to have interesting properties (see figure below, which shows just the `floor()` portion. Add +1 to every cell to get total hourly weight loss)
  - There is 1 weight loss for 4 values of `hunger` for every fish, just over the threshold when the weight loss rule kicks in.
  - Weight loss accellerates every 5 hours thereafter by one additional unit of speed.

![](2024-05-28%2000_24_57-Book1%20-%20Excel.png)

On Strategy:

- Initial weight gives you 4 safe units of weight to grow after spawning a fish for sure.
- Keeping hunger within the stable weight range is trivial:
  - First feeding: this is the trickiest part.
    - Starting hunger: 2-7 to 15-21, uniform random int.
    - Goal: get hunger to loseWeightHungerLevel - 1 (for safety) - 12 more (for time).
  - Subsequent feedings: add 12 clicks

Below: # of clicks on fish intially to get to ideal.

![](2024-05-28%2000_48_34-Book1.xlsx%20-%20Excel.png)

> Without cheating to see the loseWeightHungerLevel and initial hunger, you can't reliably hit the ideal stable weight for a new fish. Given the odds, you are slightly better off letting the fish gain hunger for 12 hours and coming back, because you get at least a little buffer on the weight loss. Even after that first 12 hour period, though, you can't see how hungry the fish is and whether it lost weight, so you don't have the info you need to chose the # of clicks to re-up the fish.