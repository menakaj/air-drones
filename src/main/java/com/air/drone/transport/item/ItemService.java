package com.air.drone.transport.item;

import com.air.drone.transport.exceptions.NoItemFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NoItemFoundException(itemId));
    }

    public List<Item> addItems(List<Item> items) {
        return itemRepository.saveAllAndFlush(items);
    }
}
