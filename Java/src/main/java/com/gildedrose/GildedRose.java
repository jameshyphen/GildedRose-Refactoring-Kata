package com.gildedrose;

import javax.print.DocFlavor;

class GildedRose {
    Item[] items;

    private static final String CONJURED_PREFIX = "[Conjured]";

    private static final String AGED_BRIE = "Aged Brie";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    private static final String BACKSTAGE_PASSES = "Backstage passes to a TAFKAL80ETC concert";

    private static final int MAX_QUALITY = 50;


    public GildedRose(Item[] items) {
        this.items = items;
    }

    public void updateQuality() {
        for (Item item : items) {
            String itemName = item.name.replace(CONJURED_PREFIX, "").trim();

            // Decrease sellIn
            item.sellIn = item.sellIn - 1;

            // Skip Sulfuras, as it does not change
            if (itemName.equals(SULFURAS)) {
                continue; // Sulfuras does not change
            }

            if (qualityIsOutOfBounds(item)) continue; // Skip handling

            // Base quality modifier for quality changes
            int baseQualityChangeModifier = 1;

            if (item.sellIn < 0) baseQualityChangeModifier *= 2;
            if (item.name.contains(CONJURED_PREFIX))
                baseQualityChangeModifier *= 2; // Conjured items degrade in quality twice as fast

            // Handle quality updates
            switch (itemName) {
                case AGED_BRIE:
                    handleAgedBrie(item, baseQualityChangeModifier);
                    break;
                case BACKSTAGE_PASSES:
                    handleBackstagePasses(item, baseQualityChangeModifier);
                    break;
                default:
                    handleNormalItems(item, baseQualityChangeModifier);
                    break;
            }
        }
    }

    private static boolean qualityIsOutOfBounds(Item item) {
        if (item.quality >= MAX_QUALITY && !item.name.contains(SULFURAS)) {
            item.quality = MAX_QUALITY; // Cap quality at MAX_QUALITY
            return true;
        }
        if (item.quality <= 0) {
            item.quality = 0; // Ensure quality does not go below 0
            return true;
        }
        return false;
    }

    private void handleAgedBrie(Item item, int baseQualityChangeModifier) {
        if (item.quality < MAX_QUALITY) {
            item.quality = Math.min(MAX_QUALITY, item.quality + baseQualityChangeModifier);
        }
    }

    private void handleBackstagePasses(Item item, int baseQualityChangeModifier) {
        if (item.sellIn < 0) {
            item.quality = 0; // Backstage passes are worthless after the concert
        } else {
            if (item.quality < MAX_QUALITY) {
                if (item.sellIn < 5) {
                    item.quality = Math.min(MAX_QUALITY, item.quality + 3 * baseQualityChangeModifier);
                } else if (item.sellIn < 10) {
                    item.quality = Math.min(MAX_QUALITY, item.quality + 2 * baseQualityChangeModifier);
                } else {
                    item.quality = Math.min(MAX_QUALITY, item.quality + baseQualityChangeModifier);
                }
            }
        }
    }

    private static void handleNormalItems(Item item, int baseQualityChangeModifier) {
        item.quality = Math.max(0, item.quality - baseQualityChangeModifier);
    }
}
