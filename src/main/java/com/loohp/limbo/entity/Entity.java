/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.entity;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.entity.DataWatcher.WatchableField;
import com.loohp.limbo.entity.DataWatcher.WatchableObjectType;
import com.loohp.limbo.location.Location;
import com.loohp.limbo.utils.BungeecordAdventureConversionUtils;
import com.loohp.limbo.world.World;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;

import java.util.UUID;

public abstract class Entity implements Sound.Emitter {
	
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x01) 
	protected boolean onFire = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x02) 
	protected boolean crouching = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x04) 
	protected boolean unused = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x08) 
	protected boolean sprinting = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x10) 
	protected boolean swimming = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x20) 
	protected boolean invisible = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x40) 
	protected boolean glowing = false;
	@WatchableField(MetadataIndex = 0, WatchableObjectType = WatchableObjectType.BYTE, IsBitmask = true, Bitmask = 0x80) 
	protected boolean elytraFlying = false;
	@WatchableField(MetadataIndex = 1, WatchableObjectType = WatchableObjectType.VARINT) 
	protected int air = 300;
	@WatchableField(MetadataIndex = 2, WatchableObjectType = WatchableObjectType.CHAT, IsOptional = true) 
	protected Component customName = null;
	@WatchableField(MetadataIndex = 3, WatchableObjectType = WatchableObjectType.BOOLEAN) 
	protected boolean customNameVisible = false;
	@WatchableField(MetadataIndex = 4, WatchableObjectType = WatchableObjectType.BOOLEAN) 
	protected boolean silent = false;
	@WatchableField(MetadataIndex = 5, WatchableObjectType = WatchableObjectType.BOOLEAN) 
	protected boolean noGravity = false;
	@WatchableField(MetadataIndex = 6, WatchableObjectType = WatchableObjectType.POSE) 
	protected Pose pose = Pose.STANDING;
	@WatchableField(MetadataIndex = 7, WatchableObjectType = WatchableObjectType.VARINT)
	protected int frozenTicks = 0;
	
	protected final EntityType type;
	
	protected int entityId;
	protected UUID uuid;
	protected World world;
	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;
	
	public Entity(EntityType type, int entityId, UUID uuid, World world, double x, double y, double z, float yaw, float pitch) {
		this.type = type;
		this.entityId = entityId;
		this.uuid = uuid;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Entity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float pitch) {
		this(type, Limbo.getInstance().getNextEntityId(), uuid, world, x, y, z, yaw, pitch);
	}
	
	public Entity(EntityType type, World world, double x, double y, double z, float yaw, float pitch) { 
		this(type, Limbo.getInstance().getNextEntityId(), UUID.randomUUID(), world, x, y, z, yaw, pitch);
	}
	
	public Entity(EntityType type, UUID uuid, Location location) { 
		this(type, Limbo.getInstance().getNextEntityId(), uuid, location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	public Entity(EntityType type, Location location) { 
		this(type, Limbo.getInstance().getNextEntityId(), UUID.randomUUID(), location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}
	
	public EntityType getType() {
		return type;
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	public void teleport(Location location) {
		this.world = location.getWorld();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}
	
	public Component getCustomName() {
		return customName;
	}
	
	public void setCustomName(String name) {
		this.customName = name == null ? null : LegacyComponentSerializer.legacySection().deserialize(name);
	}
	
	public void setCustomName(Component component) {
		this.customName = component;
	}
	
	@Deprecated
	public void setCustomName(BaseComponent component) {
		setCustomName(component == null ? null : BungeecordAdventureConversionUtils.toComponent(component));
	}
	
	@Deprecated
	public void setCustomName(BaseComponent[] components) {
		setCustomName(components == null ? null : BungeecordAdventureConversionUtils.toComponent(components));
	}

	public boolean isOnFire() {
		return onFire;
	}

	public void setOnFire(boolean onFire) {
		this.onFire = onFire;
	}

	public boolean isCrouching() {
		return crouching;
	}

	public void setCrouching(boolean crouching) {
		this.crouching = crouching;
	}

	public boolean isSprinting() {
		return sprinting;
	}

	public void setSprinting(boolean sprinting) {
		this.sprinting = sprinting;
	}

	public boolean isSwimming() {
		return swimming;
	}

	public void setSwimming(boolean swimming) {
		this.swimming = swimming;
	}

	public boolean isInvisible() {
		return invisible;
	}

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public boolean isGlowing() {
		return glowing;
	}

	public void setGlowing(boolean glowing) {
		this.glowing = glowing;
	}

	public boolean isElytraFlying() {
		return elytraFlying;
	}

	public void setElytraFlying(boolean elytraFlying) {
		this.elytraFlying = elytraFlying;
	}

	public int getAir() {
		return air;
	}

	public void setAir(int air) {
		this.air = air;
	}

	public boolean isCustomNameVisible() {
		return customNameVisible;
	}

	public void setCustomNameVisible(boolean customNameVisible) {
		this.customNameVisible = customNameVisible;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean hasGravity() {
		return !noGravity;
	}

	public void setGravity(boolean gravity) {
		this.noGravity = !gravity;
	}

	public Pose getPose() {
		return pose;
	}

	public void setPose(Pose pose) {
		this.pose = pose;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public int getEntityId() {
		return entityId;
	}

	public UUID getUniqueId() {
		return uuid;
	}
	
	public boolean isValid() {
		return world.getEntities().contains(this);
	}
	
	@SuppressWarnings("deprecation")
	public void remove() {
		Limbo.getInstance().getUnsafe().a(world, this);
	}
	
	@SuppressWarnings("deprecation")
	public DataWatcher getDataWatcher() {
		return Limbo.getInstance().getUnsafe().b(world, this);
	}

}
