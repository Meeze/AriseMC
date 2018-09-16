package de.hardcorepvp.utils;

public enum ShopItems {

	SULPHUR(0, 15),
	IRON_INGOT(0, 50),
	GOLD_INGOT(0, 200),
	DIAMOND(0, 250);

	private int buyPrice;
	private int sellPrice;

	private ShopItems(int buyPrice, int sellPrice) {
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
	}

	public int getBuyPrice() {
		return buyPrice;
	}

	public int getSellPrice() {
		return sellPrice;
	}
}
