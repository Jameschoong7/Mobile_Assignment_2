package com.cet3014n.assignment1

class ProductRepository(private val productDao: ProductDao) {

    suspend fun insertSampleProducts() {
        val sampleProducts = listOf(
            Product(name = "Espresso", price = 4.00, description = "Kaw kaw (thick thick) black coffee...", category = "Coffee", dietary = listOf("Vegetarian", "Gluten-Free"), imageResId = R.drawable.espresso),
            Product(name = "Americano", price = 7.00, description = "A diluted espresso with hot water...", category = "Coffee", dietary = listOf("Vegetarian", "Gluten-Free"), imageResId = R.drawable.americano),
            Product(name = "Latte", price = 9.00, description = "A creamy coffee drink made with espresso...", category = "Coffee", dietary = listOf("Vegetarian", "Gluten-Free"), imageResId = R.drawable.latte),
            Product(name = "Green Tea", price = 10.00, description = "Refreshing herbal tea, suitable for people who is on diet", category = "Tea", dietary = listOf("Vegetarian", "Gluten-Free"), imageResId = R.drawable.green_tea),
            Product(name = "English Breakfast Tea", price = 8.00, description = "A robust black tea blend, perfect for starting your day.", category = "Tea", dietary = listOf("Vegetarian", "Gluten-Free"), imageResId = R.drawable.english_breakfast_tea),
            Product(name = "Matcha", price = 12.50, description = "A vibrant green tea powder whisked with hot water, known for its earthy flavor.", category = "Tea", dietary = listOf("Vegetarian", "Gluten-Free"), imageResId = R.drawable.matcha),
            Product(name = "Croissant", price = 5.50, description = "Buttery pastry with a flaky crust and a golden center, yum!", category = "Pastries", dietary = listOf("Vegetarian"), imageResId = R.drawable.croissant),
            Product(name = "Curry Puff", price = 2.50, description = "A savory pastry filled with spiced curry filling.", category = "Pastries", dietary = listOf("Vegetarian"), imageResId = R.drawable.curry_puff),
            Product(name = "Cinnamon Roll", price = 8.00, description = "A sweet roll with cinnamon filling, topped with icing.", category = "Pastries", dietary = listOf("Vegetarian"), imageResId = R.drawable.cinnamon_roll)
        )
        productDao.insertAll(*sampleProducts.toTypedArray())
    }
}
