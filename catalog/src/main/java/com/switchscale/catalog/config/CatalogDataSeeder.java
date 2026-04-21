package com.switchscale.catalog.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.switchscale.catalog.model.CategoryModel;
import com.switchscale.catalog.model.ProductModel;
import com.switchscale.catalog.repository.CategoryRepository;
import com.switchscale.catalog.repository.ProductRepository;
import com.switchscale.catalog.service.CatelogService;

@Component
public class CatalogDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CatelogService catelogService;

    public CatalogDataSeeder(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            CatelogService catelogService) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.catelogService = catelogService;
    }

    @Override
    public void run(String... args) {
        Map<String, String> categoryIds = ensureCategories();
        seedProducts(categoryIds);
    }

    private Map<String, String> ensureCategories() {
        List<CategorySeed> categorySeeds = List.of(
                new CategorySeed("Fruits", "https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=800"),
                new CategorySeed("Vegetables", "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800"),
                new CategorySeed("Dairy", "https://images.unsplash.com/photo-1628088062854-d1870b4553da?w=800"),
                new CategorySeed("Bakery", "https://images.unsplash.com/photo-1608198093002-ad4e005484ec?w=800"),
                new CategorySeed("Beverages", "https://images.unsplash.com/photo-1497534446932-c925b458314e?w=800"),
                new CategorySeed("Snacks", "https://images.unsplash.com/photo-1621939514649-280e2ee25f60?w=800"),
                new CategorySeed("Personal Care", "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800"),
                new CategorySeed("Household", "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800"),
                new CategorySeed("Frozen Foods", "https://images.unsplash.com/photo-1515003197210-e0cd71810b5f?w=800"),
                new CategorySeed("Baby Care", "https://images.unsplash.com/photo-1519689680058-324335c77eba?w=800"));

        Map<String, String> idsByName = new LinkedHashMap<>();

        for (CategorySeed seed : categorySeeds) {
            CategoryModel category = categoryRepository.findByNameIgnoreCase(seed.name())
                    .orElseGet(() -> {
                        CategoryModel newCategory = new CategoryModel();
                        newCategory.setName(seed.name());
                        newCategory.setImageUrl(seed.imageUrl());
                        newCategory.setParentId(null);
                        return categoryRepository.save(newCategory);
                    });

            idsByName.put(seed.name(), category.getId());
        }

        return idsByName;
    }

    private void seedProducts(Map<String, String> categoryIds) {
        List<ProductSeed> productSeeds = List.of(
                // Fruits (5)
                new ProductSeed("Banana Premium", "Farm-fresh bananas", 48.0, 60.0, "6 pcs",
                        "https://images.unsplash.com/photo-1603833665858-e61d17a86224?w=800", List.of("Fruits")),
                new ProductSeed("Apple Royal Gala", "Crisp red apples", 180.0, 220.0, "1 kg",
                        "https://images.unsplash.com/photo-1567306226416-28f0efdc88ce?w=800", List.of("Fruits")),
                new ProductSeed("Orange Valencia", "Juicy oranges", 120.0, 150.0, "1 kg",
                        "https://images.unsplash.com/photo-1582979512210-99b6a53386f9?w=800", List.of("Fruits")),
                new ProductSeed("Pomegranate Arils", "Ready-to-eat seeds", 95.0, 120.0, "250 g",
                        "https://images.unsplash.com/photo-1519996521433-3b1d0f5f2c0f?w=800", List.of("Fruits")),
                new ProductSeed("Watermelon Cubes", "Chilled fresh-cut cubes", 80.0, 100.0, "500 g",
                        "https://images.unsplash.com/photo-1563114773-84221bd62daa?w=800", List.of("Fruits")),

                // Vegetables (5)
                new ProductSeed("Tomato Hybrid", "Fresh red tomatoes", 35.0, 45.0, "500 g",
                        "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=800", List.of("Vegetables")),
                new ProductSeed("Potato Fresh", "Washed potatoes", 40.0, 55.0, "1 kg",
                        "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=800", List.of("Vegetables")),
                new ProductSeed("Onion Red", "Premium red onions", 45.0, 60.0, "1 kg",
                        "https://images.unsplash.com/photo-1508747703725-719777637510?w=800", List.of("Vegetables")),
                new ProductSeed("Spinach Bunch", "Tender spinach leaves", 25.0, 35.0, "250 g",
                        "https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=800", List.of("Vegetables")),
                new ProductSeed("Carrot Crunch", "Sweet crunchy carrots", 38.0, 50.0, "500 g",
                        "https://images.unsplash.com/photo-1447175008436-054170c2e979?w=800", List.of("Vegetables")),

                // Dairy (5)
                new ProductSeed("Full Cream Milk", "Rich full cream milk", 68.0, 75.0, "1 L",
                        "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=800", List.of("Dairy")),
                new ProductSeed("Greek Yogurt Plain", "High protein yogurt", 120.0, 150.0, "400 g",
                        "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=800", List.of("Dairy")),
                new ProductSeed("Cheddar Cheese Block", "Mild cheddar", 210.0, 260.0, "200 g",
                        "https://images.unsplash.com/photo-1486297678162-eb2a19b0a32d?w=800", List.of("Dairy")),
                new ProductSeed("Butter Salted", "Creamy table butter", 56.0, 70.0, "100 g",
                        "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=800", List.of("Dairy")),
                new ProductSeed("Paneer Fresh", "Soft paneer cubes", 94.0, 120.0, "200 g",
                        "https://images.unsplash.com/photo-1604908554027-d7f7f6b2f0f5?w=800", List.of("Dairy")),

                // Bakery (5)
                new ProductSeed("Multigrain Bread", "Freshly baked loaf", 48.0, 60.0, "400 g",
                        "https://images.unsplash.com/photo-1608198093002-ad4e005484ec?w=800", List.of("Bakery")),
                new ProductSeed("Burger Buns", "Soft sesame buns", 52.0, 65.0, "4 pcs",
                        "https://images.unsplash.com/photo-1550547660-d9450f859349?w=800", List.of("Bakery")),
                new ProductSeed("Whole Wheat Tortilla", "Ready wraps", 78.0, 95.0, "8 pcs",
                        "https://images.unsplash.com/photo-1618040996337-56904b7850b9?w=800", List.of("Bakery")),
                new ProductSeed("Chocolate Muffin", "Moist choco muffin", 35.0, 45.0, "2 pcs",
                        "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=800", List.of("Bakery")),
                new ProductSeed("Garlic Bread", "Herb garlic loaf", 72.0, 90.0, "250 g",
                        "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=800", List.of("Bakery")),

                // Beverages (5)
                new ProductSeed("Orange Juice", "Cold-pressed juice", 110.0, 130.0, "1 L",
                        "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=800", List.of("Beverages")),
                new ProductSeed("Cold Coffee", "Ready-to-drink coffee", 95.0, 120.0, "300 ml",
                        "https://images.unsplash.com/photo-1445116572660-236099ec97a0?w=800", List.of("Beverages")),
                new ProductSeed("Green Tea Bags", "25 tea bags", 145.0, 180.0, "50 g",
                        "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=800", List.of("Beverages")),
                new ProductSeed("Sparkling Water", "Lemon sparkling water", 50.0, 60.0, "330 ml",
                        "https://images.unsplash.com/photo-1536935338788-846bb9981813?w=800", List.of("Beverages")),
                new ProductSeed("Mango Shake", "Mango dairy shake", 85.0, 105.0, "250 ml",
                        "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=800", List.of("Beverages")),

                // Snacks (5)
                new ProductSeed("Nacho Chips", "Corn nacho chips", 65.0, 80.0, "150 g",
                        "https://images.unsplash.com/photo-1621939514649-280e2ee25f60?w=800", List.of("Snacks")),
                new ProductSeed("Roasted Makhana", "Masala fox nuts", 89.0, 110.0, "90 g",
                        "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800", List.of("Snacks")),
                new ProductSeed("Salted Peanuts", "Crunchy peanuts", 55.0, 70.0, "200 g",
                        "https://images.unsplash.com/photo-1585238342024-78d387f4a707?w=800", List.of("Snacks")),
                new ProductSeed("Oats Cookies", "Whole oats biscuits", 72.0, 90.0, "180 g",
                        "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?w=800", List.of("Snacks")),
                new ProductSeed("Protein Bar", "Chocolate protein bar", 120.0, 145.0, "5 bars",
                        "https://images.unsplash.com/photo-1515003197210-e0cd71810b5f?w=800", List.of("Snacks")),

                // Personal Care (5)
                new ProductSeed("Face Wash Gel", "Gentle face cleanser", 189.0, 249.0, "150 ml",
                        "https://images.unsplash.com/photo-1556228720-195a672e8a03?w=800", List.of("Personal Care")),
                new ProductSeed("Shampoo Herbal", "Anti-hairfall shampoo", 220.0, 279.0, "340 ml",
                        "https://images.unsplash.com/photo-1556228578-8c89e6adf883?w=800", List.of("Personal Care")),
                new ProductSeed("Toothpaste Mint", "Fresh mint toothpaste", 98.0, 120.0, "150 g",
                        "https://images.unsplash.com/photo-1607613009820-a29f7bb81c04?w=800", List.of("Personal Care")),
                new ProductSeed("Hand Wash", "Moisturizing hand wash", 110.0, 140.0, "250 ml",
                        "https://images.unsplash.com/photo-1584483766114-2cea6facdf57?w=800", List.of("Personal Care")),
                new ProductSeed("Body Lotion", "Hydrating body lotion", 249.0, 320.0, "300 ml",
                        "https://images.unsplash.com/photo-1611930022073-b7a4ba5fcccd?w=800", List.of("Personal Care")),

                // Household (5)
                new ProductSeed("Dishwash Liquid", "Lemon dish cleaner", 125.0, 150.0, "750 ml",
                        "https://images.unsplash.com/photo-1626806787461-102c1bfaaea1?w=800", List.of("Household")),
                new ProductSeed("Floor Cleaner", "Floral floor cleaner", 165.0, 210.0, "1 L",
                        "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800", List.of("Household")),
                new ProductSeed("Laundry Detergent", "Top-load detergent", 235.0, 290.0, "1 kg",
                        "https://images.unsplash.com/photo-1610552050890-fe99536c2614?w=800", List.of("Household")),
                new ProductSeed("Garbage Bags", "Medium garbage bags", 88.0, 105.0, "30 pcs",
                        "https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?w=800", List.of("Household")),
                new ProductSeed("Tissue Roll", "Soft tissue rolls", 145.0, 180.0, "6 rolls",
                        "https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=800", List.of("Household")),

                // Frozen Foods (5)
                new ProductSeed("Frozen Peas", "Premium green peas", 92.0, 115.0, "500 g",
                        "https://images.unsplash.com/photo-1582515073490-39981397c445?w=800", List.of("Frozen Foods")),
                new ProductSeed("Sweet Corn Frozen", "Frozen sweet corn", 99.0, 125.0, "500 g",
                        "https://images.unsplash.com/photo-1551754655-cd27e38d2076?w=800", List.of("Frozen Foods")),
                new ProductSeed("Chicken Nuggets", "Crispy nuggets", 249.0, 310.0, "400 g",
                        "https://images.unsplash.com/photo-1562967916-eb82221dfb92?w=800", List.of("Frozen Foods")),
                new ProductSeed("French Fries", "Classic fries", 135.0, 170.0, "500 g",
                        "https://images.unsplash.com/photo-1576107232684-1279f390859f?w=800", List.of("Frozen Foods")),
                new ProductSeed("Frozen Paratha", "Layered frozen paratha", 110.0, 135.0, "6 pcs",
                        "https://images.unsplash.com/photo-1601050690597-df0568f70950?w=800", List.of("Frozen Foods")),

                // Baby Care (5)
                new ProductSeed("Baby Diapers M", "Leak-lock diapers", 420.0, 520.0, "36 pcs",
                        "https://images.unsplash.com/photo-1519689680058-324335c77eba?w=800", List.of("Baby Care")),
                new ProductSeed("Baby Wipes", "Aloe baby wipes", 175.0, 220.0, "72 pulls",
                        "https://images.unsplash.com/photo-1584515933487-779824d29309?w=800", List.of("Baby Care")),
                new ProductSeed("Baby Lotion", "Gentle body lotion", 210.0, 260.0, "200 ml",
                        "https://images.unsplash.com/photo-1544207240-42895ede7c40?w=800", List.of("Baby Care")),
                new ProductSeed("Cerelac Stage 2", "Infant cereal", 299.0, 360.0, "300 g",
                        "https://images.unsplash.com/photo-1606702838440-61f8f44c3f14?w=800", List.of("Baby Care")),
                new ProductSeed("Baby Soap", "Mild baby soap", 85.0, 110.0, "75 g",
                        "https://images.unsplash.com/photo-1631729371254-42c2892f0e6e?w=800", List.of("Baby Care")),

                // Multi-category examples (up to 3 categories)
                new ProductSeed("Breakfast Power Combo", "Milk + bread + coffee combo", 199.0, 245.0, "1 pack",
                        "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800",
                        List.of("Dairy", "Bakery", "Beverages")),
                new ProductSeed("Fruit Yogurt Cups", "Assorted fruit yogurt", 150.0, 190.0, "4 cups",
                        "https://images.unsplash.com/photo-1488477181946-6428a0291777?w=800",
                        List.of("Fruits", "Dairy", "Baby Care")));

        for (ProductSeed seed : productSeeds) {
            if (productRepository.findByNameIgnoreCase(seed.name()).isPresent()) {
                continue;
            }

            List<String> resolvedCategoryIds = new ArrayList<>();
            for (String categoryName : seed.categoryNames()) {
                String categoryId = categoryIds.get(categoryName);
                if (categoryId != null) {
                    resolvedCategoryIds.add(categoryId);
                }
            }

            if (resolvedCategoryIds.isEmpty()) {
                continue;
            }

            ProductModel product = new ProductModel();
            product.setName(seed.name());
            product.setDescription(seed.description());
            product.setPrice(seed.price());
            product.setMrp(seed.mrp());
            product.setWeight(seed.weight());
            product.setImageUrl(seed.imageUrl());
            product.setCategoryIds(resolvedCategoryIds);
            product.setCategoryId(resolvedCategoryIds.get(0));
            product.setActive(true);

            catelogService.createProduct(product);
        }
    }

    private record CategorySeed(String name, String imageUrl) {
    }

    private record ProductSeed(
            String name,
            String description,
            Double price,
            Double mrp,
            String weight,
            String imageUrl,
            List<String> categoryNames) {
    }
}
