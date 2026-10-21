![Bruce banner.....](https://cdn.modrinth.com/data/cached_images/70d325065ca5eff2b2f973f0f47fb632edaaa8ff.png)
<div align="center">

[![Ko-fi](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/kofi_icon_bg.png)](https://ko-fi.com/majdsafi)
[![Modrinth](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/modrinth_icon_bg.png)](https://modrinth.com/user/teddz)
[![Github](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/github_icon_bg.png)](https://github.com/frteddz)
[![CurseForge](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/cruseforge_icon_bg.png)](https://www.curseforge.com/members/teddz/)

</div>

## about

you're either trying to impress people with your fps, trying to benchmark your pc, or you just like staring at numbers. no judgment, all three are valid. you're in the right place.

benchburner is an msi afterburner style overlay for minecraft with a built in benchmark recorder. it shows fps, frametime, gpu, cpu and ram in one chunky block, includes a bundled benchmark world built for repeatable a/b testing, and tells you exactly how a run went. export it to csv and win arguments in discord, or lose them with proof.

if your game runs like a powerpoint after installing 47 performance mods, now you can watch it happen live, with a graph. you're welcome.

client-side only. your server admin never finds out about your 12 fps.

## features

- **one overlay block:** fps, gpu, cpu, ram, frametime graph and benchmark status in a single chunk afterburner panel. every individual line can be turned off in the settings screen
- **fps:** current, average, 1% low and 0.1% low (the number your friend with 400 fps asked you not to look at)
- **gpu:** name, usage, temperature, power draw, fan speed, vram, core and memory clock. read through nvidia-smi or your amd linux driver where they exist, hidden where they don't. intel gets its name and a moment of silence
- **cpu:** load, clock speed, per core grid, temperature where your system exposes it. on windows, java usually does not
- **frametime graph:** a live chart of your game falling apart, free entertainment
- **traffic light colors:** red, yellow and green on values, with thresholds you set for fps, usage and temperatures
- **settings screen:** sections with fold chevrons, so it doesn't look like a tax form. left click turns things on, lowers values and cycles colors forward. right click turns things off, raises values and cycles colors back
- **presets:** minimal, standard and full, plus custom presets you name and save. share configs through clipboard export and import, because pasting json into discord is its own kind of flex
- **overlay toggle key:** hide the whole block with one press, show it again with the same press. benchmark recording doesn't stop when the overlay does
- **a benchburner button** in the top right of the title screen and the esc menu, plus a keybind. digging through mods folders for a config screen is a chore

## keybinds

- `R` start or stop benchmark
- `N` export last benchmark again
- `K` overlay settings
- `O` hide or show overlay
- `B` benchmark world

all of them live in a dedicated benchburner category in the controls screen, rebind as you like.

## benchmark

press `R` to start a run, press it again to stop. you get average fps, min and max fps, 1% and 0.1% lows and average frametime, in a summary panel and in chat. the full run exports to csv in your game directory with a timestamped name as soon as it ends. press `N` to export the last run again. post it, or delete it and pretend it never happened. i won't tell.

## the benchmark world

press `B`. the mod unpacks a bundled test world into your saves folder if it isn't already there, opens it, and prepares the crime scene: time frozen at noon, no mobs, no weather, no phantoms ambushing a benchmark, no fires spreading. you get dropped at the world spawn every time, the world settles for 5 seconds, then a run records itself for a fixed length, stops, summarizes and exports. run length is 60 seconds by default and adjustable from 10 to 600 in the settings.

why do you care? because comparing modpacks needs identical scenes. modpack a in the world, modpack b in the world, same time of day, same spot, same length. bigger number wins. the mod can't stop you from walking a different path, but it removes everything it can remove.

## requirements

- minecraft 1.20.1 through 26.3 with fabric loader and fabric api
- java 17 or newer
- a pc. any pc. i'm not picky, i've seen your fps

## faq

<details>
<summary><b>does it hurt my fps?</b></summary>

barely. stats are read on a background thread so the render path stays clean. a performance mod that lags your game would be hilarious, and i refuse to give you that.

</details>

<details>
<summary><b>why is cpu temperature missing?</b></summary>

some systems, mostly windows, don't let java read it. when it can't be read the stat is hidden. not a bug, java is just shy.

</details>

<details>
<summary><b>why is my gpu panel half empty?</b></summary>

the mod reads what system tools can read: nvidia-smi on nvidia, sysfs files on amd linux. hidden lines mean the source on your system has nothing to give. installing this mod does not fight your driver.

</details>

<details>
<summary><b>my fps is bad, is the mod broken?</b></summary>

no. the mod works perfectly. it is simply telling you the truth. nobody warned you about that part.

</details>

<details>
<summary><b>can i use this to flex my fps?</b></summary>

absolutely. screenshot it, post it, ruin someone's day. just don't post the 0.1% lows.

</details>

<details>
<summary><b>can i run this on my potato?</b></summary>

yes. the mod is light. the potato is the problem, and now you'll have the data to prove it.

</details>

<details>
<summary><b>other versions and forge?</b></summary>

one jar now covers fabric builds from 1.20.1 through 26.3, and it picks the right internals at startup. forge and beyond 26.3 come later, after this proves itself in the wild. asking "when" in the comments will not speed it up. i checked.

</details>

<details>
<summary><b>something is wrong or missing?</b></summary>

open an [issue](https://github.com/frteddz/BenchBurner/issues) on github with what is off. screenshots help. angry paragraphs don't, but i'll read them anyway.

</details>

## conclusion

so that's benchburner. it shows your fps, records your runs, and never lies to you, which is more than i can say for most things on the internet.

if you like it, leave a follow or a ko-fi, it helps more than you think. if you hate it, open an issue and tell me why. if your fps is bad, that's not my fault, i just made the graph.

now go benchmark something.

---

## building

```
./gradlew build
```

the jar ends up in `build/libs`. it compiles the same way every other fabric mod does, no magic, no vibes.

## license

mit. do whatever, just don't ship the bugs back to me as features.
