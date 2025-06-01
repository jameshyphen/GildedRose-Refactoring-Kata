package com.gildedrose;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GildedRoseTest {

    String AGED_BRIE = "Aged Brie";
    String SULFURAS = "Sulfuras, Hand of Ragnaros";

    String CONJURED_PREFIX = "[Conjured]";

    @Test
    void gildedRoseConstructor_shouldStoreItemsCorrectly() {
        // Given item data
        String expectedNameOne = "First Item";
        int sellInOne = 10, qualityOne = 20;
        String expectedNameTwo = "Another Item";
        int sellInTwo = 5, qualityTwo = 15;

        Item[] items = new Item[]{
            new Item(expectedNameOne, sellInOne, qualityOne),
            new Item(expectedNameTwo, sellInTwo, qualityTwo)
        };

        // When creating a GildedRose instance
        GildedRose app = new GildedRose(items);

        // Then the items should be initialized correctly
        assertEquals(2, app.items.length);

        assertEquals(expectedNameOne, app.items[0].name);
        assertEquals(sellInOne, app.items[0].sellIn);
        assertEquals(qualityOne, app.items[0].quality);

        assertEquals(expectedNameTwo, app.items[1].name);
        assertEquals(sellInTwo, app.items[1].sellIn);
        assertEquals(qualityTwo, app.items[1].quality);
    }

    @Test
    void normalItem_shouldDecreaseQualityAndSellInByOne_beforeSellByDate() {
        // Given an item in the GildedRose
        String expectedName = "Triforce";
        int startingSellIn = 20;
        int startingQuality = 10;
        Item[] items = new Item[]{new Item(expectedName, startingSellIn, startingQuality)};
        GildedRose app = new GildedRose(items);

        // When updating quality
        app.updateQuality();

        // Then the item should have the quality and sellIn updated
        assertEquals(startingSellIn - 1, app.items[0].sellIn);
        assertEquals(startingQuality - 1, app.items[0].quality);
    }

    @Test
    void multipleNormalItems_shouldUpdateQualityAndSellInCorrectly() {
        // Given multiple normal items
        Item[] items = new Item[]{
            new Item("Bandos Chestplate", 5, 10),
            new Item("Zamorakian Spear", 3, 15)
        };
        GildedRose app = new GildedRose(items);

        // When updating quality
        app.updateQuality();

        // Then both items should have their sellIn and quality updated correctly
        assertEquals(4, app.items[0].sellIn);
        assertEquals(9, app.items[0].quality);
        assertEquals(2, app.items[1].sellIn);
        assertEquals(14, app.items[1].quality);
    }

    @Test
    void normalItem_shouldDegradeQualityByTwo_afterSellInDateIsZero_andNotGoBelowZero() {
        // Given a normal item with a sellIn of 1
        Item[] items = new Item[]{new Item("Bandos Chestplate", 1, 10)};
        GildedRose app = new GildedRose(items);

        // Given a list of expected qualities and sellins after each update
        int[] expectedSellIns = {0, -1, -2, -3, -4, -5, -6};
        int[] expectedQualities = {9, 7, 5, 3, 1, 0, 0};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities
                )
            ),
            expectedQualities.length
        );
    }

    @Test
    void agedBrie_shouldIncreaseInQuality_andFasterAfterSellInDateIsZero() {
        // Given an Aged Brie item
        Item[] items = new Item[]{new Item(AGED_BRIE, 1, 10)};
        GildedRose app = new GildedRose(items);

        // Given a list of expected qualities and sellins after each update
        // Day 1: SellIn = 0, Quality = 11
        // Day 2: SellIn = -1, Quality = 13
        // etc
        int[] expectedSellIns = {0, -1, -2, -3, -4};
        int[] expectedQualities = {11, 13, 15, 17, 19};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities
                )
            ),
            expectedQualities.length
        );
    }

    @Test
    void itemQuality_shouldNotExceedFifty_forItemsThatIncreaseInQuality() {
        // Given an Aged Brie item that starts with a quality of 49
        Item[] items = new Item[]{new Item(AGED_BRIE, 1, 48)};
        GildedRose app = new GildedRose(items);

        // Given a list of expected qualities after each update
        int[] expectedSellIns = {0, -1, -2, -3, -4, -5};
        int[] expectedQualities = {49, 50, 50, 50, 50, 50};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities
                )
            ),
            expectedQualities.length
        );
    }

    @Test
    void sulfuras_shouldNeverChangeQualityOrSellIn() {
        // Given a Sulfuras item
        Item[] items = new Item[]{new Item(SULFURAS, 2, 80)};
        GildedRose app = new GildedRose(items);

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    new int[]{2, 2, 2, 2, 2}, // SellIn should remain constant
                    new int[]{80, 80, 80, 80, 80} // Quality should remain constant
                )
            ),
            5
        );
    }

    @Test
    void backstagePass_shouldFollowComplexQualityUpdateRules() {
        // Given a Backstage Pass item with various sellIn values
        Item[] items = new Item[]{new Item("Backstage passes to a TAFKAL80ETC concert", 11, 20)};
        GildedRose app = new GildedRose(items);

        // Given a list of expected qualities after each update
        int[] expectedSellIns = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -1, -2};
        int[] expectedQualities = {21, 23, 25, 27, 29, 31, 34, 37, 40, 43, 46, 0, 0};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities
                )
            ),
            expectedQualities.length
        );
    }

    @Test
    void conjuredNormalItem_shouldDegradeQualityTwiceAsFastAsNormalItems() {
        // Given a conjured normal item
        Item[] items = new Item[]{new Item(CONJURED_PREFIX + " Spirit Axe", 3, 11)};
        GildedRose app = new GildedRose(items);

        // Given a list of expected qualities after each update
        int[] expectedSellIns = {2, 1, 0, -1, -2, -3};
        int[] expectedQualities = {9, 7, 5, 1, 0, 0};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities
                )
            ),
            expectedQualities.length
        );
    }

    @Test
    void conjuredAgedBrie_shouldIncreaseQualityConsistently() {
        // Given a conjured Aged Brie item
        Item[] items = new Item[]{new Item(CONJURED_PREFIX + " " + AGED_BRIE, 2, 10)};
        GildedRose app = new GildedRose(items);

        // Given a list of expected qualities after each update
        // Day 1: SellIn = 1, Quality = 12
        // Day 2: SellIn = 0, Quality = 14
        // Day 3: SellIn = -1, Quality = 18
        // etc
        int[] expectedSellIns = {1, 0, -1, -2, -3, -4};
        int[] expectedQualities = {12, 14, 18, 22, 26, 30};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities
                )
            ),
            expectedQualities.length
        );
    }

    @Test
    void conjuredSulfuras_shouldDegradeLikeANormalItem() {
        // Given a conjured Sulfuras item
        Item item = new Item(CONJURED_PREFIX + " " + SULFURAS, 2, 80);
        GildedRose app = new GildedRose(new Item[]{item});

        // Given a list of expected qualities and sellins after each update
        int[] expectedSellIns = {1, 0, -1, -2, -3};
        int[] expectedQualities = {79, 78, 76, 74, 72};

        assertUpdatedValues(
            app,
            List.of(
                new ExpectedItemState(
                    app.items[0],
                    expectedSellIns,
                    expectedQualities)
            ),
            expectedQualities.length);
    }

    private record ExpectedItemState(Item item, int[] expectedSellIn, int[] expectedQuality) {
    }

    private void assertUpdatedValues(
        GildedRose app,
        List<ExpectedItemState> expectedStates,
        int iterations
    ) {
        expectedStates.forEach(state -> {
            // If both values are present, ensure the expected arrays are the same length
            if (state.expectedSellIn != null && state.expectedQuality != null) {
                assertEquals(state.expectedSellIn.length, state.expectedQuality.length,
                    "Expected sellIn and quality arrays must be of the same length for item: " + state.item.name);
            }
        });

        for (int i = 0; i < iterations; i++) {
            app.updateQuality();
            for (int j = 0; j < expectedStates.size(); j++) {
                ExpectedItemState itemState = expectedStates.get(j);
                Item item = itemState.item;
                assertEquals(itemState.expectedSellIn[i], app.items[j].sellIn, "SellIn mismatch for " + item.name);
                assertEquals(itemState.expectedQuality[i], app.items[j].quality, "Quality mismatch for " + item.name);
            }
        }
    }
}
