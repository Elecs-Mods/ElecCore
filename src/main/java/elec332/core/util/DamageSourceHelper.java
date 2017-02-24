package elec332.core.util;

import elec332.core.api.registration.NamedFieldGetter;
import net.minecraft.util.DamageSource;

/**
 * Created by Elec332 on 30-1-2017.
 */
@NamedFieldGetter.Definition(declaringClass = DamageSource.class, field = "damageType,field_76373_n")
public class DamageSourceHelper {

    @NamedFieldGetter(name = "inFire")
    public static DamageSource IN_FIRE;

    @NamedFieldGetter(name = "onFire")
    public static DamageSource ON_FIRE;

}
