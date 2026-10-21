![Bruce banner.....](https://cdn.modrinth.com/data/cached_images/70d325065ca5eff2b2f973f0f47fb632edaaa8ff.png)
<div align="center">

# benchburner

an afterburner style fps overlay and benchmark recorder for minecraft. a graph of your game falling apart.

[![Ko-fi](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/kofi_icon_bg.png)](https://ko-fi.com/majdsafi)
[![Modrinth](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/modrinth_icon_bg.png)](https://modrinth.com/user/teddz)
[![Github](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/github_icon_bg.png)](https://github.com/frteddz)
[![CurseForge](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/cruseforge_icon_bg.png)](https://www.curseforge.com/members/teddz/)

</div>

## about

you're either trying to impress people with your fps, trying to benchmark your pc, or you just like staring at numbers while pretending it's science. no judgment, all three are valid.

benchburner shows fps, frametime, gpu, cpu and ram in one chunky overlay block, comes with a bundled benchmark world for repeatable a/b testing, and exports every run to csv. win arguments in discord, or lose them with proof.

client-side only. your server admin never finds out about your 12 fps.

## download

- [modrinth](https://modrinth.com/user/teddz)
- [curseforge](https://www.curseforge.com/members/teddz/)

or build it yourself, see below.

## features

- **one overlay block:** fps, gpu, cpu, ram, frametime graph and benchmark status in a single panel. every line can be turned off in settings
- **fps:** current, average, 1% low and 0.1% low
- **gpu:** name, usage, temperature, power draw, fan speed, vram, core and memory clock. read through nvidia-smi or the amd linux driver where they exist, hidden where they don't
- **cpu:** load, clock speed, per core grid, temperature where your system exposes it
- **frametime graph:** live, and not flattering
- **traffic light colors:** red, yellow and green with thresholds you set
- **settings screen:** foldable sections. left click turns things on, lowers values and cycles colors forward. right click does the opposite
- **presets:** minimal, standard and full, plus custom presets with clipboard export and import
- **overlay toggle key:** hides the block without stopping a benchmark
- **benchburner button** on the title screen and esc menu

## keybinds

| key | action |
| --- | --- |
| `R` | start or stop benchmark |
| `N` | export last benchmark again |
| `K` | overlay settings |
| `O` | hide or show overlay |
| `B` | benchmark world |

all of them are in a benchburner category in the controls screen, rebind them however you want.

## benchmark

press `R` to start a run and press it again to stop. you get average fps, min and max fps, 1% and 0.1% lows and average frametime in a summary panel and in chat. the full run exports to a timestamped csv in your game directory the moment it ends.

## benchmark world

press `B`. the mod unpacks a bundled test world into your saves folder if it isn't there yet and opens it with time frozen at noon, no mobs, no weather and no fire spread. you spawn at the same spot every time, the world settles for 5 seconds, then a run records itself, stops, summarizes and exports. default length is 60 seconds, adjustable from 10 to 600 in settings.

same scene, same spot, same length. that's what makes comparing modpacks fair. the mod can't stop you from walking a different path, the rest is on your honor.

## requirements

- minecraft 1.21.11
- fabric loader and fabric api
- java 21

## build

```bash
git clone https://github.com/frteddz/BenchBurner.git
cd BenchBurner
./gradlew build
```

the jar ends up in `build/libs`. to test in a dev client:

```bash
./gradlew runClient
```

## faq

<details>
<summary><b>does it hurt my fps?</b></summary>

barely. stats are read on a background thread so the render path stays clean.

</details>

<details>
<summary><b>why is cpu temperature missing?</b></summary>

some systems, mostly windows, don't let java read it. when it can't be read the stat is hidden. not a bug, java is just shy.

</details>

<details>
<summary><b>why is my gpu panel half empty?</b></summary>

the mod reads what system tools can read: nvidia-smi on nvidia, sysfs files on amd linux. hidden lines mean the source on your system has nothing to give. i can't make your driver talk. i've tried.

</details>

<details>
<summary><b>my fps is bad, is the mod broken?</b></summary>

no. it is simply telling you the truth.

</details>

<details>
<summary><b>other versions and forge?</b></summary>

right now it's built for 1.21.11 and fabric only. more versions and loaders are planned once this one proves itself. asking "when" in the issues will not speed it up. i checked.

</details>

## contributing

found a bug or have an idea? open an [issue](https://github.com/frteddz/BenchBurner/issues) with what is off or what you want. screenshots help. angry paragraphs don't, but i'll read them anyway.

pull requests are welcome. keep the code comment free and keep the text plain.

## license

mit. do whatever, just don't ship the bugs back to me as features.

made by [T3ddz](https://teddzfr.carrd.co/)
