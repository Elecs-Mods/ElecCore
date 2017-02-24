package elec332.core.util;

import elec332.core.api.registration.NamedFieldGetter;
import net.minecraft.util.DamageSource;

/**
 * Created by Elec332 on 30-1-2017.
 */
@NamedFieldGetter.Definition(declaringClass = DamageSource.class, field = "damageType")
public class DamageSourceHelper {

    @NamedFieldGetter(name = "inFire,field_76372_a")
    public static DamageSource IN_FIRE;

    @NamedFieldGetter(name = "onFire,field_76370_b")
    public static DamageSource ON_FIRE;

}
