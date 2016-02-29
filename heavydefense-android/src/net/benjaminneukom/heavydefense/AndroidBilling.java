package net.benjaminneukom.heavydefense;

import net.benjaminneukom.heavydefense.system.Billing;
import net.benjaminneukom.heavydefense.util.IabHelper;
import net.benjaminneukom.heavydefense.util.IabHelper.QueryInventoryFinishedListener;
import net.benjaminneukom.heavydefense.util.IabResult;
import net.benjaminneukom.heavydefense.util.Inventory;
import net.benjaminneukom.heavydefense.util.Purchase;

import com.badlogic.gdx.scenes.scene2d.ui.List;

// TODO billing error callback
// TODO if billing not available, forward to homepage
public class AndroidBilling implements Billing {

	private IabHelper iabHelper;
	private Activity activity;
	private boolean isAvailable;

	public AndroidBilling(Activity activity) {
		this.activity = activity;

		iabHelper = new IabHelper(
				activity, null);
		iabHelper.enableDebugLogging(true);

		throw new IllegalStateException("Add your iab id.");
	}

	public IabHelper getIabHelper() {
		return iabHelper;
	}

	@Override
	public void initlaize() {

		iabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				isAvailable = result.isSuccess();
				// FIXME consume all donations (in case there was an error and they weren't consumed)

				if (!result.isSuccess()) {
					return;
				}

				iabHelper.queryInventoryAsync(new QueryInventoryFinishedListener() {

					@Override
					public void onQueryInventoryFinished(IabResult result, Inventory inv) {
						if (!result.isSuccess()) return;

						final List<String> allOwnedSkus = inv.getAllOwnedSkus();

						// consume
						for (String sku : allOwnedSkus) {
							Purchase purchase = inv.getPurchase(sku);
							if (purchase != null)
								consumePurchase(purchase);
						}
					}
				});
			}
		});

	}

	@Override
	public void initiateDonate(String donateSku) {
		if (!isAvailable || iabHelper.isAsyncInProgress()) return;

		iabHelper.launchPurchaseFlow(activity, donateSku, 1002, new IabHelper.OnIabPurchaseFinishedListener() {

			@Override
			public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
				if (!result.isSuccess()) {
					// TODO report problem
					return;
				}

				// consume
				consumePurchase(purchase);
			}

		});
	}

	private void consumePurchase(Purchase purchase) {
		iabHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {

			@Override
			public void onConsumeFinished(Purchase purchase, IabResult result) {

			}
		});
	}

	@Override
	public boolean isAvailable() {
		return isAvailable;
	}

	@Override
	public void dispose() {
		iabHelper.dispose();
	}
}
