package com.remodstudios.endless

import net.minecraft.entity.damage.DamageSource

// stupid protected methods
class ModDamageSource(name: String): DamageSource(Endless.MOD_ID + "." + name) {
    public override fun setBypassesArmor(): ModDamageSource {
        super.setBypassesArmor()
        return this
    }

    public override fun setOutOfWorld(): ModDamageSource {
        super.setOutOfWorld()
        return this
    }

    public override fun setUnblockable(): ModDamageSource {
        super.setUnblockable()
        return this
    }

    public override fun setFire(): ModDamageSource {
        super.setFire()
        return this
    }
}