package sonar.core.common.item;

import net.minecraft.creativetab.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.core.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class SonarMetaItem extends Item {

	public int numSubItems = 1;

	public SonarMetaItem(int numSubItems, Item.Properties properties) {
		super(properties);
		this.numSubItems = numSubItems;
	}

	@Override
	public int getMaxDamage() {
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void fillItemCategory(@Nonnull CreativeModeTab tab, @Nonnull NonNullList<ItemStack> items) {
		if (allowedIn(tab)) {
			for (int i = 0; i < numSubItems; i++) {
				items.add(new ItemStack(this, 1, i));
			}
		}
	}

	@Nonnull
	@Override
	public String getDescriptionId(ItemStack stack) {
		return getDescriptionId() + '.' + stack.getDamageValue();
	}
}
