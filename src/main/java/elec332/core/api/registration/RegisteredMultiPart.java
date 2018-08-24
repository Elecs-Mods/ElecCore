package elec332.core.api.registration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 8-3-2016.
 *
 * Used to register multiparts, annotate your multipart class to register it
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RegisteredMultiPart {

    public String value();

}
