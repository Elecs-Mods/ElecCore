package elec332.core.client.model;

import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 19-11-2015.
 */
public interface ISpecialItemRenderer {

    /**
     * Renders an item in first person.
     *
     * @param stack The stack that needs to be rendered
     * @param partialTicks The partial ticks
     */
    public void renderInFirstPerson(ItemStack stack, float partialTicks);

    /**
     * Whether the specified ItemStack should use this renderer to render the item in first person.
     *
     * @param stack The ItemStack
     * @return If the stack should the special renderInFirstPerson method from this handler.
     */
    public boolean shouldUseSpecialRendererInFirstPerson(ItemStack stack);

    /**
     * NOTE: This only gets called if #shouldUseSpecialRendererInFirstPerson(ItemStack stack) returned true.
     *
     * Whether the first person renderer should do some annoying stuff for you,
     * eg: lighting.
     *
     * @param stack The ItemStack
     * @return Return false is you want to do everything by yourself. (Not recommended)
     */
    public boolean shouldRendererHelpOut(ItemStack stack);

}
