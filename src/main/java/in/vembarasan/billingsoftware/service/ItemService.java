package in.vembarasan.billingsoftware.service;

import in.vembarasan.billingsoftware.io.ItemRequest;
import in.vembarasan.billingsoftware.io.ItemResponse;

import java.util.List;

public interface ItemService {

    ItemResponse add(ItemRequest request);

    List<ItemResponse> fetchItems();

    void deleteItem(String itemId);
}
