# Assumptions

1. **Quality exceeding 50**:
    - With the current implementation, the quality _can_ be out of bounds (x<0 or x>50) if created through the constructor.
    - The spec is stating that:
      - we cannot have items with quality above 50, except for Sulfuras.
      - we cannot have items with quality below 0.
      - we cannot alter the class objects, because the goblin does not allow us.
    - **Assumption**: Items with quality above or below the thresholds are allowed to be created, but the values will not be altered.
2. **Aged Brie when SellIn goes 0 and below**:
    - The spec states that Aged Brie should increase in quality by 2 when SellIn is 0 or below.
    - **Assumption**: This behavior is implemented correctly in the code.
3. **Conjured items**:
    - The spec states that we cannot alter the class objects, because the goblin does not allow us.
    - **Assumption**: If an item has "[Conjured]" in its name, it is a conjured.
5. **Conjured Aged Brie**:
   - The spec states that conjured items should degrade in quality twice as fast.
   - **Assumption**: A Conjured Aged Brie should increase in quality by 2 when SellIn is above 0, and by 4 when SellIn is 0 or below. (Even though it sounds weird, we'll stay consistent with the spec)
6. **Conjured Sulfuras**:
   - The spec states that Sulfuras does not degrade in quality.
   - **Assumption**: A Conjured Sulfuras should not degrade in quality.
7. **"Full Rewrite not necessary"**:
   - In the email, it was stated that a full rewrite is not necessary.
   - **Assumption**: The existing code structure can be used to implement the new features without a complete overhaul. This means a full "rewrite" of the logic inside the function is allowed, but we have to keep everything backwards compatible. _(e.g. changing class structure or function signatures is not allowed)_
