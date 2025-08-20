package com.exist;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElementTest {

    @Test
    void testConstructorAndGetterInString() {
        Element element = new Element("key", "value");
        assertEquals("key", element.getKey());
        assertEquals("value", element.getValue());
    }

    @Test
    void testSettersAndGettersForReplacement() {
        Element element = new Element("oldKey", "oldValue");
        element.setKey("newKey");
        element.setValue("newValue");
        assertEquals("newKey", element.getKey());
        assertEquals("newValue", element.getValue());
    }
}
