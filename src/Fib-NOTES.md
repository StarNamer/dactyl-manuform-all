I want a keyboard like the dactyl manuform, but with the two halves joined, 5x3 keys in each well, and 2 thumb keys on each side.

First, I'm trying to get actually 5x3 keys in the wells. If you reduce nrows from 4 to 3, it's the topmost row that gets removed, whereas I want to remove the bottom-most row where there are actually only two keys out of five present.

I found code that treated columns 2 and 3 differently and removed that conditional. Next, I think I need to fix the webbing. There are vars lastrow and lastcol which I think are used as off-by-one indices, I can try modifying those.

I think I'll want to modify loop indices to that '3 rows' means from -1 to 1 instead of 0 to 2 or whatever.