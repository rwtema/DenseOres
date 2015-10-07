DenseOres by RWTema
=========

<br><b>Note: Dense Ores has updated to 1.8. If you are looking for the 1.7 code select the 1.7 from the branch list</b><br>

<a rel="license" href="http://creativecommons.org/licenses/by/4.0/deed.en_GB"><img alt="Creative Commons Licence" style="border-width:0" src="http://i.creativecommons.org/l/by/4.0/88x31.png" /></a><br />This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/4.0/deed.en_GB">Creative Commons Attribution 4.0 International License</a>.

Code is provided without guarantee of being error-free or optimal.


<br><b>Download of the latest release:</b> <a href="http://addons.curse.cursecdn.com/files/2234/692/denseores-1.6.2.jar">Version 1.6.2</a><br><br>


If you wish to add another mods ores to the game, you will need to alter the config file. I generally wouldn't recommend doing this if you are a casual player since this can cause issues. It's best to see if there is a modpack that does it properly for you. There is a simple denseores.cfg that includes a number of ores from some common mods here <a href="https://github.com/rwtema/DenseOres/blob/master/example_configs/denseores.cfg">https://github.com/rwtema/DenseOres/blob/master/example_configs/denseores.cfg</a><br>

You can also find individual example configs for common mods at <a href="https://github.com/rwtema/DenseOres/tree/master/example_configs/mods">https://github.com/rwtema/DenseOres/tree/master/example_configs/mods</a>. This will require you to change the ore ids when you add them to your config file. If anyone wishes to add more example configs for common mods, then please feel free to submit a pull request for it.<br>


<a href="http://www.curse.com/mc-mods/minecraft/224942-dense-ores#t1:other-downloads"><b>Older Versions:</b></a><br>
<a href="http://addons.curse.cursecdn.com/files/2233/926/denseores-1.6.1.jar">Version 1.6.1</a><br>
<a href="http://addons.curse.cursecdn.com/files/2233/888/denseores-1.6.jar">Version 1.6</a><br>

<a href="http://addons.curse.cursecdn.com/files/2216/805/denseores-1.5.jar">Version 1.5</a><br>
<a href="http://www.mediafire.com/download/leudza3jpd8uvd8/denseores-1.4.1.jar">Version 1.4.1</a><br>
<a href="http://www.mediafire.com/download/ac7a4hb8cuzm6rb/denseores-1.4.jar">Dense Ores 1.4</a><br>
<a href="http://www.mediafire.com/download/vcluluqcd8k0atp/denseores-1.3.jar">Dense Ores 1.3</a><br>
There was no 1.2 version for some reason.<br>
<a href="http://www.mediafire.com/download/7okkg0vqm2zm5z2/denseores-1.1.jar">Dense Ores 1.1</a><br>
<a href="http://www.mediafire.com/download/5lh66z373w40bx7/denseores-1.0.0.jar">Dense Ores 1.1.0</a><br>

<b>Config Info</b><br>
<i>S:baseBlock</i> - The ore block that you wish to replace. This is in the form modid:blockname<br>
<i>I:baseBlockMeta</i> - The metadata value for the block (0-15)<br>
<i>S:baseBlockTexture</i> - The ores texture name (as found in assets/modid/textures/blocks)<br>
<i>D:denseOreProbability</i> - Currently unused.<br>
<i>I:renderType</i> - This changes the way the texture generation works (see <a href="https://i.imgur.com/CGfhSss.png">here</a> for details).<br>
<i>I:retroGenId</i> - Retrogen number. Set it to non-zero to enable retrogen. You can change it to a diffent number to run retrogen again.<br>
<i>S:underlyingBlock</i> - The texture of the base block (usually stone or netherrack), see baseBlockTexture.<br>

