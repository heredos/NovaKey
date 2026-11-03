# NovaKey
> An alternative input method. 

This is a fork of the now defunct strellaStudios/Novaykey with a bunch of modernizations and fixes.  
The goal here isn't to make the code pretty or anything, it is just a bunch of vibe-slopped patches to make this messy code usable again

The intent of NovaKey was to create an open platform for alternative input methods, but the project fell though. I still consider it to be one of the most usable one-handed android keyboard ever made, and nothing I found ever came close.

# Usage
tap a sector to type the letter in the middle of it. swipe over a separator to type the letter on the edge of that separator.  
swipe from the bottom to the top to capitalize, and from the top to the bottom to send. Swipe from the left to the right to add a space, and from the right to the left to delete a character.  
if you need to insert a special character, keep pressing after the input and circle your finger around the center to select the character to add.  

# Advanced usage
rotate multiple sectors to move the cursor left and right, go back to the center to change which part of the selection to change. stay in the center then rotate to copy, cut or paste.  
keep pressing after a down swipe to add a new line  keep pressing after an up swipe to start selecting/moving the cursor up and down (very handy for terminal users).  
Keep pressing and rotate after a left swipe to delete multiple characters, and rotate in the opposite direction to add the characters back (if the input box supports it).  
keep pressing after a right swipe to add a space (handy for terminal users).  
swipe up with two fingers to configure the position and size of the keyboard.  
I think that covers everything.

# Contribute
You are free to do so, I am not sure I can handle this project as it should, if you are interested in fixing it by hand, you are welcome to contact me and i will gladly let you do so.

### Bugs
If you would like to report a bug or crash you can by submitting an issue to this repository labeled with `bug`. I can't promise I'll be able to fix it but i'll do my best.

# Architecture
At a high level NovaKey uses the [Model-View-Controller(MVC)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) pattern. Taking a closer look though the logic is centered around `Elements` and `Actions`. The diagram below shows the architecture with more detail:

![architecturediagram](https://user-images.githubusercontent.com/13156162/51805618-c7af1380-223d-11e9-9eb9-55aeae3253df.png)

As shown by the diagram above `Elements` have three inputs and 2 outputs. `Elements` use the `User Input`, `State` & `Themes` to output to the `View` and to perform `Actions` which update the text & the `Model`.

This causes the app to behave like a [Finite State Machine](https://en.wikipedia.org/wiki/Finite-state_machine). For example, if the app is in the `Default` state and the user performs a shift action, the state will become `Shift`. Then if the user performs a shift action again the state will become `Caps Lock`. 

# Licence

```
NovaKey - An alternative touchscreen input method
Copyright (C) 2019  Viviano Cantu

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>

Any questions about the program or source may be directed to <strellastudios@gmail.com>
```
