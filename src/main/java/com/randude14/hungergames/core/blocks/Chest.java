package com.randude14.hungergames.core.blocks;

import com.randude14.hungergames.core.LocalInventory;
import com.randude14.hungergames.core.LocalWorld;
import com.randude14.hungergames.core.Vector;

public abstract class Chest extends Block implements LocalInventory {
	protected final LocalWorld world;
	protected final Vector pos;

	public Chest(int id, LocalWorld world, Vector pos) {
		super(id);
		this.world = world;
		this.pos = pos;
	}
}
