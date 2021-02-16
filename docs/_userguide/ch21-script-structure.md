---
title: Script structure
---

Script files are stored in the `res/script` folder. 

## .lua and .lvn files

NVList supports script files with two different types of file extension. Files with names ending in .lvn use the NVList script format as described in the rest of this chapter (.lvn stands for Lua VN). For script files that only contain code, you can also the .lua file extension. Files with the .lua extension are interpreted as standard Lua code.

## Using multiple script files

You can split up your scripts across multiple files, for example by using a separate script file for each scene/chapter. The `jump` and `call` commands can be used to move to a different script file:

{% include sourcecode.html id="jumpCall" content="
@call(\"interlude\")
This text is shown when 'interlude' finishes.
@jump(\"chapter2\")
"%}

The quoted text is the filename of the script that you're trying to jump to (the file extension can be omitted). The difference between `call` and `jump` is what happens when the end of the other script is reached. If you use `call`, script execution continues with the next line after the `call` command. `jump` doesn't do this.

## Backup

Please make regular backups of your script files and store the backups somewhere online. There are far too many stories of people losing dozens/hundreds of hours of work after a hardware failure.
 