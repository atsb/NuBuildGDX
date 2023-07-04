package ru.m210projects.Blood.Types.Seq;

import static ru.m210projects.Blood.Actor.kAttrAiming;
import static ru.m210projects.Blood.Actor.kAttrFalling;
import static ru.m210projects.Blood.Actor.kAttrFlipX;
import static ru.m210projects.Blood.Actor.kAttrFlipY;
import static ru.m210projects.Blood.Actor.kAttrGravity;
import static ru.m210projects.Blood.Actor.kAttrSmoke;
import static ru.m210projects.Blood.DB.kMaxXSprites;
import static ru.m210projects.Blood.DB.xsprite;
import static ru.m210projects.Blood.Globals.kMaxSprites;
import static ru.m210projects.Blood.Globals.kSpriteBlocking;
import static ru.m210projects.Blood.Globals.kSpriteHitscan;
import static ru.m210projects.Blood.Globals.kSpriteInvisible;
import static ru.m210projects.Blood.Globals.kSpritePushable;
import static ru.m210projects.Blood.Globals.kSpriteTranslucent;
import static ru.m210projects.Blood.Globals.kSpriteTranslucentR;
import static ru.m210projects.Blood.Main.engine;
import static ru.m210projects.Blood.Main.game;
import static ru.m210projects.Build.Engine.sprite;

import ru.m210projects.Blood.Gameutils;
import ru.m210projects.Build.Types.SPRITE;

public class SpriteInst extends SeqInst {

	@Override
	protected void updateInstance(int index) {
		if(!(index > 0 && index < kMaxXSprites))
			game.dassert("nXSprite > 0 && nXSprite < kMaxXSprites");
		int nSprite = xsprite[index].reference;
		if(!(nSprite >= 0 && nSprite < kMaxSprites))
			game.dassert("nSprite >= 0 && nSprite < kMaxSprites");
		SPRITE pSprite = sprite[nSprite];
		if(pSprite.extra != index)
			game.dassert("pSprite.extra == nXSprite");

		SeqFrame pFrame = pSequence.getFrame(frameIndex);

		// set the motion bit if the vertical size or position of the sprite changes

		if ( (pSprite.hitag & kAttrGravity) != 0 &&
			( pSprite.picnum >= 0 && (engine.getTile(pSprite.picnum).getHeight() != engine.getTile(pFrame.nTile).getHeight() ||
					engine.getTile(pSprite.picnum).getOffsetY() != engine.getTile(pFrame.nTile).getOffsetY()) ||
			pFrame.yrepeat != 0 && pFrame.yrepeat != pSprite.yrepeat) )
			pSprite.hitag |= kAttrFalling;

		pSprite.picnum = (short) pFrame.nTile;

		if(pFrame.pal != 0)
			pSprite.pal = (byte) pFrame.pal;
		pSprite.shade = pFrame.shade;

		int scale = (Gameutils.IsPlayerSprite(pSprite)) ? xsprite[pSprite.extra].data1 : 0; // player SEQ size scale
		if (pFrame.xrepeat != 0) {
			if (scale < 0) pSprite.xrepeat = (short) (pFrame.xrepeat / Math.abs(scale));
			else if (scale > 0) pSprite.xrepeat = (short) (pFrame.xrepeat * scale);
			else pSprite.xrepeat = pFrame.xrepeat;
		}

		if (pFrame.yrepeat != 0) {
			if (scale < 0) pSprite.yrepeat = (short) (pFrame.yrepeat / Math.abs(scale));
			else if (scale > 0) pSprite.yrepeat = (short) (pFrame.yrepeat * scale);
			else pSprite.yrepeat = pFrame.yrepeat;

		}

		if ( pFrame.translucent )
			pSprite.cstat |= kSpriteTranslucent;
		else
			pSprite.cstat &= ~kSpriteTranslucent;

		if ( pFrame.translucentR )
			pSprite.cstat |= kSpriteTranslucentR;
		else
			pSprite.cstat &= ~kSpriteTranslucentR;

		if ( pFrame.blocking )
			pSprite.cstat |= kSpriteBlocking;
		else
			pSprite.cstat &= ~kSpriteBlocking;

		if ( pFrame.hitscan )
			pSprite.cstat |= kSpriteHitscan;
		else
			pSprite.cstat &= ~kSpriteHitscan;

		if( pFrame.invisible )
			pSprite.cstat |= kSpriteInvisible;
		else pSprite.cstat &= ~kSpriteInvisible;

		if( pFrame.pushable )
			pSprite.cstat |= kSpritePushable;
		else pSprite.cstat &= ~kSpritePushable;

		if ( pFrame.smoke )
			pSprite.hitag |= kAttrSmoke;
		else pSprite.hitag &= ~kAttrSmoke;

		if ( pFrame.aiming )
			pSprite.hitag |= kAttrAiming;
		else pSprite.hitag &= ~kAttrAiming;

		if ( pFrame.flipx )
			pSprite.hitag |= kAttrFlipX;
		else pSprite.hitag &= ~kAttrFlipX;
		if ( pFrame.flipy )
			pSprite.hitag |= kAttrFlipY;
		else pSprite.hitag &= ~kAttrFlipY;

//		if///
//		sfxStart3DSound XXX
	}
}