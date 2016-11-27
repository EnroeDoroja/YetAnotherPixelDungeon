/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Yet Another Pixel Dungeon
 * Copyright (C) 2015-2016 Considered Hamster
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.consideredhamster.yetanotherpixeldungeon.ui;

import java.util.ArrayList;

import com.consideredhamster.yetanotherpixeldungeon.Dungeon;
import com.consideredhamster.yetanotherpixeldungeon.actors.Char;
import com.consideredhamster.yetanotherpixeldungeon.actors.mobs.Mob;
import com.consideredhamster.yetanotherpixeldungeon.levels.Level;
import com.consideredhamster.yetanotherpixeldungeon.scenes.PixelScene;
import com.consideredhamster.yetanotherpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Random;

public class AttackIndicator extends Tag {
	
	private static final float ENABLED	= 1.0f;
	private static final float DISABLED	= 0.3f;
	
	public static AttackIndicator instance;
	
	private CharSprite sprite = null;
	
	private static Mob lastTarget = null;
	private ArrayList<Mob> candidates = new ArrayList<>();
	
	public AttackIndicator() {
		super( DangerIndicator.COLOR );
		
		instance = this;
		
		setSize( 24, 24 );
		visible( false );
		enable( false );
	}
	
	@Override
	protected void createChildren() {
		super.createChildren();
	}
	
	@Override
	protected void layout() {
		super.layout();
		
		if (sprite != null) {
			sprite.x = x + (width - sprite.width()) / 2;
			sprite.y = y + (height - sprite.height()) / 2;
			PixelScene.align( sprite );
		}
	}	
	
	@Override
	public void update() {
		super.update();
		
		if (Dungeon.hero.isAlive()) {
			
			if (!Dungeon.hero.ready) {
				enable( false );
			}		
			
		} else {
			visible( false );
			enable( false );
		}
	}

	private void checkEnemies() {

//		int heroPos = Dungeon.hero.pos;

		candidates.clear();

		int v = Dungeon.hero.visibleEnemies();

		for (int i=0; i < v; i++) {
			Mob mob = Dungeon.hero.visibleEnemy( i );
//			if (Level.adjacent( heroPos, mob.pos )) {
				candidates.add( mob );
//			}
		}
		
		if (!candidates.contains( lastTarget )) {
			if (candidates.isEmpty()) {
				lastTarget = null;
			} else {
				lastTarget = candidates.get(0);
				updateImage();				
				flash();
			}
		} else {
			if (!bg.visible) {
				flash();
			}
		}
		
		visible( lastTarget != null );
		enable( bg.visible );
	}
	
	private void updateImage() {
		
		if (sprite != null) {
			sprite.killAndErase();
			sprite = null;
		}
		
		try {
			sprite = lastTarget.spriteClass.newInstance();
			sprite.idle();
			sprite.paused = true;
			add( sprite );

			sprite.x = x + (width - sprite.width()) / 2;
			sprite.y = y + (height - sprite.height()) / 2;
			PixelScene.align( sprite );
			
		} catch (Exception e) {
		}
	}
	
	private boolean enabled = true;

	private void enable( boolean value ) {
		enabled = value;
		if (sprite != null) {
			sprite.alpha( value ? ENABLED : DISABLED );
		}
	}
	
	private void visible( boolean value ) {
		bg.visible = value;
		if (sprite != null) {
			sprite.visible = value;
		}
	}
	
	@Override
	protected void onClick() {
		if (enabled) {
			Dungeon.hero.handle( lastTarget.pos );
            enable( false );
		}
	}

    @Override
    protected boolean onLongClick() {

        if (enabled) {
            Toolbar.examineMob( lastTarget.pos );
        }

        return enabled;
    }
	
	public static void target( Mob target ) {
		lastTarget = target;
		instance.updateImage();
		
		HealthIndicator.instance.target( target );
	}
	
	public static void updateState() {

        if( instance != null ) {
            instance.checkEnemies();
        }
	}
}
