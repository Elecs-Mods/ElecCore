package elec332.core.asm.asmload;

/**
 * Created by Elec332 on 26-11-2015.
 */
public interface IASMClassTransformer {

    public String getDeObfuscatedClassName();

    public byte[] transformClass(byte[] bytes);

}
