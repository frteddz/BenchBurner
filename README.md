
<div align="center">

[![Ko-fi](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/kofi_icon_bg.png)](https://ko-fi.com/majdsafi)
[![Modrinth](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/modrinth_icon_bg.png)](https://modrinth.com/user/teddz)
[![Github](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/github_icon_bg.png)](https://github.com/frteddz)
[![CurseForge](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/cruseforge_icon_bg.png)](https://www.curseforge.com/members/teddz/)

</div>

## about
hi. you're reading a mod description, which means either you care about your fps or you got lost. both are fine.

benchburner adds an afterburner-style performance overlay to minecraft with a built-in benchmark recorder. see your fps, frametime graph, cpu load and ram at a glance, then press a key to record a run and get a full summary when you stop. every run can be exported to csv, so you can finally win arguments in discord with actual numbers.

stats are sampled on a separate thread so the overlay costs very little fps. it would be embarrassing for a performance mod to lag your game, so i made sure it doesn't. the mod is purely client-side, it doesn't need to be installed on servers and works in any environment.

## features
- fps with average, 1% low and 0.1% low (the number that exposes your stutters)
- live frametime graph, for watching your game fall apart in real time
- cpu load, total and per core
- ram usage and gpu name
- cpu temperature where your system exposes it
- benchmark recording with a summary and csv export
- drag to reposition, plus scale, opacity and color settings
- minimal, standard and full presets, for when you want a little info or all of it

## overlay
![Overlay](YOUR_OVERLAY_SCREENSHOT_URL)
every stat can be toggled on or off. open the config screen to drag the overlay anywhere on your screen, or pick a preset to switch the whole layout in one click. yes you can put it in the corner. no i won't judge you for putting it in the middle.

## benchmark
![Benchmark](YOUR_BENCHMARK_SCREENSHOT_URL)
press the benchmark key to start recording and press it again to stop. you get average fps, min and max fps, 1% and 0.1% lows and average frametime. the full run is saved as a csv file in your game directory with a timestamped name. screenshot it, post it, brag about it. or quietly delete it if the numbers hurt.

## requirements
- minecraft 1.21.11 with fabric loader and fabric api
- java 21
- nothing needed on the server, this mod is client-side only

## faq

<details>
<summary><b>does it work on servers?</b></summary>

yes. install it on your client and join anything. <u>the server never needs it.</u> your server admin can stay blissfully unaware.

</details>

<details>
<summary><b>does it hurt my fps?</b></summary>

barely. system stats are read on a background thread and the render path does no extra allocation. a tiny cost for knowing exactly how bad things are.

</details>

<details>
<summary><b>why is cpu temperature missing?</b></summary>

some systems, mostly windows, don't expose it to java. when it can't be read the stat is hidden. not a bug, java is just shy.

</details>

<details>
<summary><b>where are my benchmark files?</b></summary>

in your game directory, saved as timestamped csv files. go look, they're not hiding.

</details>
<details>
<summary><b>something is wrong or missing?</b></summary>

open an [issue](https://github.com/frteddz/BenchBurner/issues) on github with what is off. screenshots help. rage doesn't, but i get it.

</details>
