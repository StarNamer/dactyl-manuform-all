I want a keyboard like the dactyl manuform, but with the two halves joined, 5x3 keys in each well, and 2 thumb keys on each side.

First, I'm trying to get actually 5x3 keys in the wells. If you reduce nrows from 4 to 3, it's the topmost row that gets removed, whereas I want to remove the bottom-most row where there are actually only two keys out of five present.

I found code that treated columns 2 and 3 differently and removed that conditional. Next, I think I need to fix the webbing. There are vars lastrow and lastcol which I think are used as off-by-one indices, I can try modifying those.

I think I'll want to modify loop indices to that '3 rows' means from -1 to 1 instead of 0 to 2 or whatever.

NOTE:
lastrow
cornerrow
lastcol

changing cornerrow from 'dec lastrow' to 'dec numrows' moved the thumb cluster down one row

uhh...just had a dumb idea, I'm going to revert that and instead have ALL columns treated as the non-2 and 3 columns are. Since that might do the things I want immediately without having to offset in many places.

Okay, that worked up to a point: the 'holes' (holders) for the switches are gone, and I think the caps in the preview mode too. The shell/connectors around the holes are still there. If I comment out the main column, row, and diagonal connectors, they're still there, so they come from elsewhere in the code.

NOTE: can comment out a whole form (asdf asdf asdf (asdf)) by just adding 'comment' as in (comment asdf asdf asdf (asdf))

okay, they're somewhere in the 'thumb-connectors' form but so are many parts of the thumbs.

Three forms I commented out in this commit are the 'weird' ones in the connectors. May need reintroducing with modifications to join the hull.
Still need to modify the shell itself.

back, left, right wall sections aren't part of the weird bit.
front wall *is* part of the weird bit.

can wrap a form with (color [1, 0, 0] ...) to have it show up in red.

I think one of the 'front' sections is actually a 'back' section, I removed it since it was actually a duplicate.

I edited the case/shell between column indexes 3 and 4, now need to do the part between the thumbs and column index 3. One of the screw inserts also lives along this wall and so will need to move.

Okay, did a fix to the thumb wall, now some commented-out lines need fixing

Okay, I've fixed the hull. The thumb part sticks out a bit sharply now though.