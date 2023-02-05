package com.air.drone.transport;

import com.air.drone.transport.item.Item;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ItemTest {

    private static final String TEST_ITEM_CODE = "ITEM-1";

    public static final String ITEM_NAME = "TestItem";

    @Test
    public void testCreateInstance() {
        Item item = new Item(ITEM_NAME, TEST_ITEM_CODE, 100,"image url");
        Assertions.assertEquals(item.getCode(), TEST_ITEM_CODE);
        Assertions.assertEquals(item.getName(), ITEM_NAME);
    }
}
