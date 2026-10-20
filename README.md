![Bruce banner.....](https://cdn.modrinth.com/data/cached_images/70d325065ca5eff2b2f973f0f47fb632edaaa8ff.png)
<div align="center">

[![Ko-fi](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/kofi_icon_bg.png)](https://ko-fi.com/majdsafi)
[![Modrinth](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/modrinth_icon_bg.png)](https://modrinth.com/user/teddz)
[![Github](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/github_icon_bg.png)](https://github.com/frteddz)
[![CurseForge](https://raw.githubusercontent.com/frteddz/pixel-icons-library-/main/96x/cruseforge_icon_bg.png)](https://www.curseforge.com/members/teddz/)

</div>

## about
you're either trying to impress people with your fps or trying to benchmark your pc, or you just like staring at numbers. no judgment, all three are valid. you're in the right place.

benchburner is an afterburner-style overlay for minecraft with a built-in benchmark recorder. it shows fps, frametime, cpu and ram, then records a run and tells you exactly how it went. export it to csv and win arguments in discord, or lose them with proof.

if your game runs like a powerpoint after installing 47 performance mods, now you can watch it happen live, with a graph. you're welcome.

client-side only. your server admin never finds out about your 12 fps.

## features
- **fps:** current, average, 1% low and 0.1% low (the number your friend with 400 fps asked you not to look at)
- **frametime graph:** a live chart of your game falling apart, free entertainment
- **cpu and ram:** per core load, ram usage, gpu name. yes, it will expose the 94 chrome tabs
- **cpu temp:** shown only if java allows it. java usually does not
- **benchmark mode:** record a run, get a summary, export to csv, cry in a spreadsheet
- **customizable:** drag it anywhere, change scale, opacity and color, or use a preset (minimal, standard, full). you can finally make your suffering look good

## how it works
**overlay:** toggle any stat on or off and drag it wherever you want. top left is classic. dead center of the screen is a bold choice for someone who needs to see where they're going.

**benchmark:** press the key to start, press it again to stop. you get average, min and max fps, 1% and 0.1% lows and average frametime, saved as a timestamped csv in your game directory. post it, or delete it and pretend it never happened. i won't tell.

## requirements
- minecraft 1.21.11 with fabric loader and fabric api
- java 21
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

more versions and forge are coming soon. asking "when" in the comments will not speed it up. i checked.

</details>

<details>
<summary><b>something is wrong or missing?</b></summary>

open an [issue](https://github.com/frteddz/BenchBurner/issues) on github with what is off. screenshots help. angry paragraphs don't, but i'll read them anyway.

</details>

## conclusion
so that's benchburner. it shows your fps, records your runs, and never lies to you, which is more than i can say for most things on the internet.

if you like it, leave a follow or a ko-fi, it helps more than you think. if you hate it, open an issue and tell me why. if your fps is bad, that's not my fault, i just made the graph.

now go benchmark something.
