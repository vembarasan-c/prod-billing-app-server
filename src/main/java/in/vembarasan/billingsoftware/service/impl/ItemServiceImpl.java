package in.vembarasan.billingsoftware.service.impl;

import in.vembarasan.billingsoftware.entity.CategoryEntity;
import in.vembarasan.billingsoftware.entity.ItemEntity;
import in.vembarasan.billingsoftware.io.ItemRequest;
import in.vembarasan.billingsoftware.io.ItemResponse;
import in.vembarasan.billingsoftware.repository.CategoryRepository;
import in.vembarasan.billingsoftware.repository.ItemRepository;
import in.vembarasan.billingsoftware.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemResponse add(ItemRequest request) {
        ItemEntity newItem = convertToEntity(request);
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found: "+request.getCategoryId()));
        newItem.setCategory(existingCategory);
        newItem = itemRepository.save(newItem);
        return convertToResponse(newItem);
    }

    private ItemResponse convertToResponse(ItemEntity newItem) {
        return ItemResponse.builder()
                .itemId(newItem.getItemId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .price(newItem.getPrice())
                .priceBack(newItem.getPriceBack())
                .categoryName(newItem.getCategory().getName())
                .categoryId(newItem.getCategory().getCategoryId())
                .createdAt(newItem.getCreatedAt())
                .updatedAt(newItem.getUpdatedAt())
                .build();
    }

    private ItemEntity convertToEntity(ItemRequest request) {
        return ItemEntity.builder()
//                .itemId(UUID.randomUUID().toString())
                .itemId(request.getItemId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .priceBack(request.getPriceBack())
                .build();
    }

    @Override
    public List<ItemResponse> fetchItems() {
        return itemRepository.findAll()
                .stream()
                .map(itemEntity -> convertToResponse(itemEntity))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(String itemId) {
        ItemEntity existingItem = itemRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: "+itemId));
        itemRepository.delete(existingItem);
    }
}
