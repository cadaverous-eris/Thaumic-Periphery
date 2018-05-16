package thaumicperiphery.entities;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.SoundsTC;

public class EntityMagicArrow extends EntityArrow {
	
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>() {
        public boolean apply(@Nullable Entity p_apply_1_) {
            return p_apply_1_.canBeCollidedWith();
        }
    });
	private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    private int inData;
	private int knockbackStrength;
	private int ticksInGround;
    private int ticksInAir;
	
	public EntityMagicArrow(World world) {
		super(world);
		this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
		this.setDamage(2.0);
        this.isImmuneToFire = true;
        this.pickupStatus = PickupStatus.DISALLOWED;
	}
	
	public EntityMagicArrow(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }
	
	public EntityMagicArrow(World world, EntityLivingBase shooter) {
        this(world, shooter.posX, shooter.posY + shooter.getEyeHeight() - 0.1, shooter.posZ);
        this.shootingEntity = shooter;
    }
	
	@Override
	public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / f;
        y = y / f;
        z = z / f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937 * inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937 * inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937 * inaccuracy;
        x = x * velocity;
        y = y * velocity;
        z = z * velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float) (MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float) (MathHelper.atan2(y, f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.ticksInGround = 0;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public void setVelocity(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (180D / Math.PI));
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }
	
	@Override
	public void move(MoverType type, double x, double y, double z) {
        super.move(type, x, y, z);

        if (this.inGround) {
            this.xTile = MathHelper.floor(this.posX);
            this.yTile = MathHelper.floor(this.posY);
            this.zTile = MathHelper.floor(this.posZ);
        }
    }

	@Override
	public void onUpdate() {
		if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (iblockstate.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0) {
            --this.arrowShake;
        }

        if (this.inGround) {
            int j = block.getMetaFromState(iblockstate);

            if ((block != this.inTile || j != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D))) {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2F;
                this.motionY *= this.rand.nextFloat() * 0.2F;
                this.motionZ *= this.rand.nextFloat() * 0.2F;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            } else {
                ++this.ticksInGround;

                if (this.ticksInGround >= 1) {
                	if (world.isRemote) {
                		int p = this.rand.nextInt(7) + 9;
                		for (int i = 0; i < 9; i++) {
                			FXGeneric fb = new FXGeneric(this.world, this.posX, this.posY, this.posZ, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D, this.rand.nextGaussian() * 0.05D);
                	        fb.setMaxAge(15 + this.rand.nextInt(20));
                	        if (this.getIsCritical()) {
                	        	fb.setRBGColorF(1.0F, 0.2F, 0.6F);
                	        } else {
                	        	fb.setRBGColorF(1.0F, 0.2F, 0.8F);
                	        }
                	        fb.setAlphaF(0.5F);
                	        fb.setGridSize(64);
                	        fb.setParticles(264, 8, 1);
                	        fb.setScale(new float[] { 3.0F, 1F });
                	        fb.setLayer(0);
                	        fb.setSlowDown(1D);
                	        fb.setGravity(0.1F);
                	        fb.setWind(0.001D);
                	        ParticleEngine.addEffect(this.world, fb);
                		}
                	}
                	
                    this.setDead();
                }
            }

            ++this.timeInGround;
        } else {
            this.timeInGround = 0;
            ++this.ticksInAir;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null) {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null) {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer)) {
                    raytraceresult = null;
                }
            }

            if (raytraceresult != null && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                this.onHit(raytraceresult);
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            for (this.rotationPitch = (float) (MathHelper.atan2(this.motionY, f4) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f1 = 0.99F;
            float f2 = 0.05F;

            if (this.isInWater()) {
                for (int i = 0; i < 4; ++i) {
                    float f3 = 0.25F;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
                }

                f1 = 0.6F;
            }

            if (this.isWet()) {
                this.extinguish();
            }

            this.motionX *= f1;
            this.motionY *= f1;
            this.motionZ *= f1;

            if (!this.hasNoGravity()) {
                this.motionY -= 0.05000000074505806D;
            }

            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }
	
	protected void onHit(RayTraceResult raytraceResultIn) {
        Entity entity = raytraceResultIn.entityHit;
        
        if (entity != null) {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            int i = MathHelper.ceil(f * this.getDamage());

            if (this.getIsCritical()) {
                i += this.rand.nextInt(i / 3 + 1);
            }

            DamageSource damagesource;

            if (this.shootingEntity == null) {
                damagesource = DamageSource.causeIndirectMagicDamage(this, this);
            } else {
                damagesource = DamageSource.causeIndirectMagicDamage(this, this.shootingEntity);
            }

            if (entity.attackEntityFrom(damagesource, i)) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                    
                    if (this.knockbackStrength > 0) {
                        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                        if (f1 > 0.0F) {
                            entitylivingbase.addVelocity(this.motionX * this.knockbackStrength * 0.6000000238418579D / f1, 0.1D, this.motionZ * this.knockbackStrength * 0.6000000238418579D / f1);
                        }
                    }

                    if (this.shootingEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) this.shootingEntity, entitylivingbase);
                    }

                    this.arrowHit(entitylivingbase);

                    if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                    }
                }

                this.playSound(SoundsTC.zap, 0.75F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                if (!(entity instanceof EntityEnderman)) {
                    this.setDead();
                }
            } else {
                this.motionX *= -0.10000000149011612D;
                this.motionY *= -0.10000000149011612D;
                this.motionZ *= -0.10000000149011612D;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                this.ticksInAir = 0;

                if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D) {
                    this.setDead();
                }
            }
        } else {
            BlockPos blockpos = raytraceResultIn.getBlockPos();
            this.xTile = blockpos.getX();
            this.yTile = blockpos.getY();
            this.zTile = blockpos.getZ();
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            this.inTile = iblockstate.getBlock();
            this.inData = this.inTile.getMetaFromState(iblockstate);
            this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
            this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
            this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
            float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
            this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
            this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
            this.playSound(SoundsTC.zap, 0.75F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;

            if (iblockstate.getMaterial() != Material.AIR) {
                this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
            }
        }
    }
	
	@Override
	@Nullable
    protected Entity findEntityOnPath(Vec3d start, Vec3d end) {
        Entity entity = null;
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
        double d0 = 0.0D;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = list.get(i);

            if (entity1 != this.shootingEntity || this.ticksInAir >= 5) {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

                if (raytraceresult != null) {
                    double d1 = start.squareDistanceTo(raytraceresult.hitVec);

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }
	
	@Override
	public void setKnockbackStrength(int knockbackStrengthIn) {
		super.setKnockbackStrength(knockbackStrengthIn);
        this.knockbackStrength = knockbackStrengthIn;
    }

	@Override
	protected ItemStack getArrowStack() {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setShort("life", (short)this.ticksInGround);
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("inData", (byte) this.inData);
        compound.setByte("shake", (byte) this.arrowShake);
        compound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        compound.setByte("pickup", (byte) this.pickupStatus.ordinal());
        compound.setDouble("damage", this.getDamage());
        compound.setBoolean("crit", this.getIsCritical());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.ticksInGround = compound.getShort("life");

        if (compound.hasKey("inTile", 8)) {
            this.inTile = Block.getBlockFromName(compound.getString("inTile"));
        } else {
            this.inTile = Block.getBlockById(compound.getByte("inTile") & 255);
        }

        this.inData = compound.getByte("inData") & 255;
        this.arrowShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;

        if (compound.hasKey("damage", 99)) {
            this.setDamage(compound.getDouble("damage"));
        }

        if (compound.hasKey("pickup", 99)) {
            this.pickupStatus = EntityArrow.PickupStatus.getByOrdinal(compound.getByte("pickup"));
        } else if (compound.hasKey("player", 99)) {
            this.pickupStatus = compound.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
        }

        this.setIsCritical(compound.getBoolean("crit"));
    }
    
}
